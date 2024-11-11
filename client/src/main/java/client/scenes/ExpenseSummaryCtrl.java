package client.scenes;

import client.DebtOverview;
import client.utils.ServerUtils;
import commons.Event;
import commons.Participant;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.ResourceBundle;

public class ExpenseSummaryCtrl extends PageController implements Initializable {

    @FXML
    public Button returnButton;
    @FXML
    private Text eventNameText;
    @FXML
    private TableColumn<DebtOverview, String> participant;
    @FXML
    private TableColumn<DebtOverview, String> debt;
    @FXML
    private TableColumn<DebtOverview, String> debtPercentage;
    @FXML
    private TableView<DebtOverview> debtTable;
    @FXML
    private Text sceneTitle;
    @FXML
    private Text debtAmount;
    @FXML
    private Label expenseTag;

    @Inject
    public ExpenseSummaryCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }


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
        server.pollingId++;
        debtTable.getColumns().clear();

        participant.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getParticipant().getName())
        );

        debt.setCellValueFactory(cellData -> {
                String currency = mainCtrl.preferredCurrency.getCurrencyCode();
                double rate = mainCtrl.currencyExchangeService.getExchangeRate(
                        Currency.getInstance("EUR"),
                        mainCtrl.preferredCurrency, getDate(), server);
                if (rate == -1.0) {
                    currency = "EUR";
                    rate = 1.0;
                }
                return new SimpleStringProperty(
                        String.format("%.2f",
                                cellData.getValue().getDebt() * rate / 100.0) + " " +
                                currency
                );
            }
        );

        debtPercentage.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDebtPercentage() + "%")
        );


        debtTable.getColumns().addAll(participant, debt, debtPercentage);
    }

    @Override
    public void refreshLanguage() {
        sceneTitle.setText(mainCtrl.getPreferedLanguage().getText("expenseSummaryTitle"));
        returnButton.setText(mainCtrl.getPreferedLanguage().getText("return"));
        debt.setText(mainCtrl.getPreferedLanguage().getText("debt"));
        participant.setText(mainCtrl.getPreferedLanguage().getText("participant"));
        debtPercentage.setText(mainCtrl.getPreferedLanguage().getText("debtPercentage"));
        expenseTag.setText(mainCtrl.getPreferedLanguage().getText("expenseTag"));
    }

    public String getDate() {
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

    /***
     * Refresh the page content and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        long eventId = (long) args[0];
        mainCtrl.currentEvent = server.getEventById(eventId);
        server.pollingId++;
        if (mainCtrl.currentEvent != null) {
            eventNameText.setText(mainCtrl.getPreferedLanguage().
                    getText("eventName") + " " + mainCtrl.currentEvent.getTitle());
            int debt = calculateTotalDebt(mainCtrl.currentEvent);
            String debtString = String.format("%.2f", debt / 100.0);
            String currency = mainCtrl.preferredCurrency.getCurrencyCode();

            double rate = mainCtrl.currencyExchangeService.getExchangeRate(
                    Currency.getInstance("EUR"),
                    mainCtrl.preferredCurrency, getDate(), server);
            if (rate == -1.0) {
                currency = "EUR";
                rate = 1.0;
            }
            SimpleStringProperty amountWithCurrency = new SimpleStringProperty(
                    String.format("%.2f",
                            debt * rate / 100.0) + " " +
                            currency
            );

            debtAmount.setText(mainCtrl.getPreferedLanguage()
                    .getText("totalDebt") + " " + amountWithCurrency.get());
            List<DebtOverview> debts = getDebts();
            if (!debts.isEmpty()) {
                ObservableList<DebtOverview> data = FXCollections.observableList(debts);
                debtTable.setItems(data);
            }
            server.registerForUpdates("/event/{eventId}/participant/updates",
                    mainCtrl.currentEvent.getId(), p -> {
                        Platform.runLater(() -> simpleRefresh(mainCtrl.currentEvent.getId()));
                    }, "ExpenseSummaryCtrl");
            server.registerForUpdates("/event/{eventId}/expense/updates",
                    mainCtrl.currentEvent.getId(), p -> {
                        Platform.runLater(() -> simpleRefresh(mainCtrl.currentEvent.getId()));
                    }, "ExpenseSummaryCtrl");
            server.registerForUpdates("/event/{eventId}/updates",
                    mainCtrl.currentEvent.getId(), p -> {
                        Platform.runLater(() -> simpleRefresh(mainCtrl.currentEvent.getId()));
                    }, "ExpenseSummaryCtrl");
        } else {
            System.out.println("The event is not found in the database! " +
                    "It may have been removed by other user!");
            mainCtrl.showStart();
        }
    }

    public void simpleRefresh(Object... args) {
        long eventId = (long) args[0];
        Event event = server.getEventById(eventId);
        clearFields();
        if (event != null) {
            eventNameText.setText(event.getTitle());
            int debt = calculateTotalDebt(event);
            String debtString = String.format("%.2f", debt / 100.0);
            debtAmount.setText(debtString);
            List<DebtOverview> debts = getDebts();
            if (!debts.isEmpty()) {
                ObservableList<DebtOverview> data = FXCollections.observableList(debts);
                debtTable.setItems(data);
            }
        } else {
            server.pollingId++;
            System.out.println("The event is not found in the database! " +
                    "It may have been removed by other user!");
            mainCtrl.showStart();
        }
    }

    public void closeSummary() {
        server.pollingId++;
        mainCtrl.closePopupStage();
    }

    private List<DebtOverview> getDebts() {
        if (mainCtrl.getCurrentEvent() == null) {
            System.out.print("no event found");
            return null;
        }

        List<DebtOverview> debts = new ArrayList<>();

        // get event
        Event event = server.getEventById(mainCtrl.getCurrentEvent().getId());

        int totalDebt = calculateTotalDebt(event);

        System.out.println("event: " + event.getTitle() + " total debt: " + totalDebt);
        System.out.println("amount of participants: " + event.getParticipants().size());
        for (Participant participant : event.getParticipants()) {
            int debt = calculatePersonalDebt(event, participant);
            float between1 = (float) debt / (float) totalDebt;
            int debtPercentage = (int) (between1 * 100);
            debts.add(new DebtOverview(participant, debt, debtPercentage));
            System.out.println("Debt: " + debt + " Debt %: " + debtPercentage);
        }

        System.out.println("Debts: " + debts.toString());
        return debts;
    }

    /***
     * Event handler for handling when keys are pressed on page
     * @param keyEvent
     */
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                server.pollingId++;
                closeSummary();
        }
    }

    public int calculateTotalDebt(Event event) {
        int debt = 0;
        for (Participant participant : event.getParticipants()) {
            debt += participant.getExpenseShare();
        }
        return debt;
    }

    public int calculatePersonalDebt(Event event, Participant participant) {
        return participant.getExpenseShare();
    }

    public void clearFields() {
        eventNameText.setText("No title found");
        debtAmount.setText("null");
        debtTable.setItems(FXCollections.observableArrayList());
    }
}
