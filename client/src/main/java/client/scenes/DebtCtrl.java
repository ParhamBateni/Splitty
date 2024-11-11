package client.scenes;



import client.SplittyConfig;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.PaymentInstruction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DebtCtrl extends PageController implements Initializable {

    // keep track of current event
    @FXML
    public TreeView<HBox> debtView;
    @FXML
    public Text debtsTitle;
    @FXML
    public Button returnButton;

    @Inject
    public DebtCtrl(ServerUtils server, MainCtrl mainCtrl, SplittyConfig splittyConfig) {
        super(server, mainCtrl, splittyConfig);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeItem<HBox> rootItem = new TreeItem<>();
        debtView.setOnKeyPressed((event -> {
            keyPressed(event);
        }));
        debtView.setRoot(rootItem);
        debtView.setShowRoot(false);
        debtView.getRoot().getChildren().clear();
//        debtView.setCellFactory(cellData -> {
//            TreeCell<HBox> item = new TreeItem<>();
//            return item;
//        });
    }

    /***
     * Refresh the page and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        initializeDebtListing();
        server.pollingId++;
        server.registerForUpdates("/event/{eventId}/participant/updates",
                mainCtrl.currentEvent.getId(), p -> {
                    Platform.runLater(() -> simpleRefresh(mainCtrl.currentEvent.getId()));
                }, "DebtCtrl");
        server.registerForUpdates("/event/{eventId}/expense/updates",
                mainCtrl.currentEvent.getId(), p -> {
                    Platform.runLater(() -> simpleRefresh(mainCtrl.currentEvent.getId()));
                }, "DebtCtrl");
        server.registerForUpdates("/event/{eventId}/updates",
                mainCtrl.currentEvent.getId(), p -> {
                    Platform.runLater(() -> simpleRefresh(mainCtrl.currentEvent.getId()));
                }, "DebtCtrl");
    }

    public void simpleRefresh(Object... args) {
        System.out.println("SIMPLE REFRESH");
        clearFields();
        initializeDebtListing();
        refreshLanguage();
    }

    @Override
    public void refreshLanguage() {
        debtsTitle.setText(mainCtrl.getPreferedLanguage().getText("debtsTitle"));

        returnButton.setText(mainCtrl.getPreferedLanguage().getText("return"));
    }

    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {
        debtView.getRoot().getChildren().clear();
    }


    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE -> mainCtrl.closePopupStage();
        }

    }

    /**
     * Method to initialize listing of unmarked debt settlers
     */
    public void initializeDebtListing() {
        List<PaymentInstruction> instructions =
                server.getPaymentInstructionsByEventId(mainCtrl.currentEvent.getId());
        for (var instruction : instructions) {
            debtView.getRoot().getChildren().add(createDebtInstructionItem(instruction));
        }
    }

    private TreeItem<HBox> createDebtInstructionItem(PaymentInstruction instruction) {
        Event event = instruction.getEvent();
        Participant p1 = instruction.getFrom();
        Participant p2 = instruction.getTo();
        System.out.println(p1.getEmail());
        System.out.println(p2.getEmail());
        Integer amount = instruction.getAmount();
        String currency = mainCtrl.preferredCurrency.getCurrencyCode();
        double exchangeRate = mainCtrl.currencyExchangeService.getExchangeRate(
                Currency.getInstance("EUR"), mainCtrl.preferredCurrency, LocalDate.now().toString()
                , server);
        if (exchangeRate == -1.0) {
            currency = "EUR";
            exchangeRate = 1.0;
        }
        Label label = new Label(p1.getName() + " " +
                mainCtrl.getPreferedLanguage().getText("owing")
                + " " + String.format("%.2f ",
                exchangeRate * amount / 100.0) + " " + currency +
                " to " + p2.getName());
        label.getStyleClass().add("small-text");
        label.getStyleClass().add("bold");
        Button completeButton = new Button(mainCtrl.getPreferedLanguage().getText("markSettled"));
        completeButton.setOnAction(ActionEvent -> {
            // Handle button action here
            System.out.println("Button clicked for item");
            if (mainCtrl.showVerificationPopup(
                    mainCtrl.getPreferedLanguage().getText("confirmSettledDebt"))) {
                {
                    // TODO add correct currency
                    Expense expense = new Expense(p1, Currency.getInstance("EUR"),
                            amount, "Debt payment", getDate(), List.of(p2), event, true);

                    expense.setAmountInEuro(amount);

                    server.addExpense(expense);
                }
                completeButton.setDisable(true);
                completeButton.setPrefWidth(completeButton.getWidth());
                completeButton.setText(mainCtrl.getPreferedLanguage().getText("settled"));
            }
        });
        Button emailButton = new Button(mainCtrl.getPreferedLanguage().getText("paymentRequest"));
        emailButton.setOnAction(ActionEvent -> {
            sendPaymentRequest(p1.getEmail());
        });

        if (Participant.isEmailValid(mainCtrl.email)
                && Participant.isEmailValid(p1.getEmail())) {
            emailButton.setDisable(false);
        } else {
            emailButton.setDisable(true);
        }
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        Region space1 = new Region();
        space1.setMinWidth(5);

        HBox info = new HBox(label, space, emailButton, space1, completeButton);
        info.setAlignment(Pos.CENTER);

        HBox additionalInfo = new HBox(
                new Text(
                        mainCtrl.getPreferedLanguage()
                                .getText("accountHolder") + ": " + p2.getName() +
                                "\nIBAN: " + (p2.getIban().isEmpty() ?
                                "Not Provided" : p2.getIban()) +
                                "\nBIC: " + (p2.getBic().isEmpty() ?
                                "Not Provided" : p2.getBic())));


        TreeItem<HBox> res = new TreeItem<>(info);
        res.getChildren().add(new TreeItem<>(additionalInfo));

        // Create an HBox to contain label and button
        return res;
    }

    public String[] getDomain() throws IOException {
        String host;
        String port;
        Scanner scanner = new Scanner(mainCtrl.email);
        scanner.useDelimiter("@");
        scanner.next();
        String domain = scanner.next();
        if (domain.contains("outlook.com")) {
            host = "smtp-mail.outlook.com";
            port = "587";
        } else if (domain.contains("gmail.com")) {
            host = "smtp.gmail.com";
            port = "587";
        } else if (domain.contains("yahoo.com")) {
            host = "smtp.mail.yahoo.com";
            port = "587";
        } else if (domain.contains("student.tudelft.nl")) {
            host = "mx11.surfmailfilter.nl";
            port = "10";
        } else {
            host = "vds";
            port = "153";
        }
        String[] result = new String[2];
        result[0] = host;
        result[1] = port;
        return result;
    }

    /**
     * Sends invitation emails to the targeted participant
     */
    public void sendPaymentRequest(String email) {

        try {

            Properties props = System.getProperties();
            String username = mainCtrl.email;
            String password = mainCtrl.password;
            String[] name = email.split("@");
            String topic = "Hello " + name[0] + "!";
            String body = "A payment has been request form you in the event " +
                    mainCtrl.currentEvent.getTitle() + " with the following link "
                    + mainCtrl.currentEvent.getLink();
            String[] to = {email};
            String[] domain = getDomain();
            String host = domain[0];
            String port = domain[1];
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            MimeMessage message = new MimeMessage(session);

            try {
                message.setFrom(new InternetAddress(username));

                InternetAddress[] toAddress = new InternetAddress[to.length];
                // To get the array of addresses
                for (int i = 0; i < to.length; i++) {
                    toAddress[i] = new InternetAddress(to[i]);

                }
                for (InternetAddress address : toAddress) {

                    message.addRecipient(Message.RecipientType.TO, address);

                }

                message.setSubject(topic);
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(
                        username));
                message.setText(body);


                message.saveChanges();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            try {
                Transport transport = session.getTransport("smtp");
                System.out.println("get protocl");
                transport.connect(host, username, password);
                System.out.println("get host,from and password");
                transport.sendMessage(message, message.getAllRecipients());
                System.out.println("get recipients");
                transport.close();
                System.out.println("close");
                System.out.println("Email Sent Successfully!");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("Complete Process");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    String getDate() {
        StringConverter<LocalDate> stringConverter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };
        return stringConverter.toString(LocalDate.now());
    }
}
