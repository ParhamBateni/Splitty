package client.scenes;

import client.utils.ServerUtils;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.util.Currency;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

import static commons.Participant.emailPattern;

public class SettingsCtrl extends PageController implements Initializable {
    @FXML
    public ComboBox<String> currency;
    @FXML
    public Text emailTestResultText;
    @FXML
    public Button testButton;

    @FXML
    public TextField emailTextField;
    public Text emailText;
    @FXML
    public Text usernameText;
    @FXML
    public Text passwordText;
    @FXML
    public TextField passwordTextField;
    @FXML
    public Text currencyText;
    @FXML
    public Text preferredCurrencyText;

    @FXML
    public Text sceneTitle;
    @FXML
    public Button infoButton;
    private String email;
    private String password;


    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currency.getItems().addAll("EUR", "USD", "CHF");
        currency.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                currency.show();
            }
        });
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            mainCtrl.email = newValue;
            if (!Participant.isEmailValid(newValue)) {
                emailTestResultText.setFill(Color.RED);
                emailTestResultText.setText(mainCtrl.getPreferedLanguage()
                        .getText("patternEmail") + emailPattern + "!");
            } else {
                emailTestResultText.setText("");
                testButton.setDisable(false);
                email = newValue;
                mainCtrl.email = email;
                password = passwordTextField.getText();
                mainCtrl.password = password;
            }
        });
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            mainCtrl.password = newValue;
            if (!testButton.isDisabled()) {
                mainCtrl.password = newValue;
            }
        });
        currency.valueProperty().addListener((observable, oldValue, newValue) ->
                mainCtrl.preferredCurrency = Currency.getInstance(newValue));
    }

    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SettingsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }


    /***
     * Refresh the page and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        this.email = (String) args[0];
        this.password = (String) args[1];
        if (email == null || email.isEmpty()) {
            testButton.setDisable(true);
            emailTestResultText.setText("");
        } else {
            passwordTextField.setText(password);
            emailTextField.setText(email);
            if (Participant.isEmailValid(email)) {
                testButton.setDisable(false);
            } else {
                emailTestResultText.setFill(Color.RED);
                emailTestResultText.setText(mainCtrl.getPreferedLanguage()
                        .getText("patternEmail") + emailPattern + "!");
                testButton.setDisable(true);
            }
        }
        switch (mainCtrl.preferredCurrency.getCurrencyCode()) {
            case "EUR" -> currency.getSelectionModel().select(0);
            case "USD" -> currency.getSelectionModel().select(1);
            case "CHF" -> currency.getSelectionModel().select(2);
        }
    }

    @Override
    public void refreshLanguage() {
        sceneTitle.setText(mainCtrl.getPreferedLanguage().getText("settings"));
        emailText.setText(mainCtrl.getPreferedLanguage().getText("email") + ":");
        testButton.setText(mainCtrl.getPreferedLanguage().getText("test"));
        usernameText.setText(mainCtrl.getPreferedLanguage().getText("username") + ":");
        emailTextField.setPromptText(mainCtrl.getPreferedLanguage().getText("enterEmail"));
        passwordText.setText(mainCtrl.getPreferedLanguage().getText("password") + ":");
        passwordTextField.setPromptText(mainCtrl.getPreferedLanguage().getText("enterPassword"));
        currencyText.setText(mainCtrl.getPreferedLanguage().getText("currency") + ":");
        preferredCurrencyText.setText(mainCtrl.getPreferedLanguage()
                .getText("preferredCurrency") + ":");
        currency.setPromptText(mainCtrl.getPreferedLanguage().getText("select"));
    }

    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {
        infoButton.requestFocus();
        emailTestResultText.setText("");
        emailTextField.setText("");
        passwordTextField.setText("");

    }

    /***
     * Event handler for handling when keys are entered on page
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                mainCtrl.showStart();
                break;
            case H:
                mainCtrl.showSettingsInfo();
                break;
        }
    }

    public String[] getDomain() {
        String host;
        String port;
        Scanner scanner = new Scanner(email);
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
            host = "smtp.gmail.com";
            port = "587";
        }
        String[] result = new String[2];
        result[0] = host;
        result[1] = port;
        return result;
    }

    public void testEmail() {
        Properties props = System.getProperties();
        String from = email;
        System.out.println(email);
        System.out.println(password);
        String receipt = email;
        String topic = "Welcome " + email + "!";
        String body = "This is a test";
        String[] to = {receipt};
        String[] domain = getDomain();
        String host = domain[0];
        String port = domain[1];
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));

            InternetAddress[] toAddress = new InternetAddress[to.length];
            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {

                toAddress[i] = new InternetAddress(to[i]);

            }
            for (InternetAddress address : toAddress) {

                message.addRecipient(Message.RecipientType.TO, address);

            }

            message.setSubject(topic);
            message.setText(body);
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(
                    from));
            message.saveChanges();

            Transport transport = session.getTransport("smtp");
            System.out.println("get protocol");
            transport.connect(host, from, password);
            System.out.println("get host,from and password");
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println("get recipients");
            transport.close();
            System.out.println("close");
            System.out.println("Email Sent Successfully!");
            emailTestResultText.setFill(Color.GREEN);
            emailTestResultText.setText(mainCtrl.getPreferedLanguage()
                    .getText("emailSentSuccessfully"));
        } catch (javax.mail.AuthenticationFailedException e) {
            emailTestResultText.setFill(Color.RED);
            emailTestResultText.setText(mainCtrl.getPreferedLanguage()
                    .getText("incorrectEmailOrPassword"));
        } catch (MessagingException e) {
            System.out.println("There is a problem sending email");
            emailTestResultText.setText(mainCtrl.getPreferedLanguage()
                    .getText("problemSendingEmail"));
        } finally {
            System.out.println("Complete Process");
        }
    }

    public void showInfo(ActionEvent actionEvent) {
        mainCtrl.showSettingsInfo();
    }
}
