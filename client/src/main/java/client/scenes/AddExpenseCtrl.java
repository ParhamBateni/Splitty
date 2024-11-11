package client.scenes;

import client.utils.ServerUtils;
import commons.Expense;
import commons.Participant;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.ResourceBundle;


public class AddExpenseCtrl extends PageController implements Initializable {


    @FXML
    public Text sceneTitle;
    @FXML
    public TextField titleName;
    @FXML
    public ComboBox<Participant> selectPayer;
    @FXML
    public ComboBox<String> currency;
    @FXML
    public TextField amount;
    @FXML
    public DatePicker datePicker;
    @FXML
    public FlowPane participantFlowPane;

    @FXML
    public Text resultAddEditText;
    @FXML
    public CheckBox splitEquallyCheckBox;

    @FXML
    public Button removeButton;

    @FXML
    public Button addButton;

    @FXML
    public Button cancelButton;

    @FXML
    public Text questionPaid;
    @FXML
    public Text questionReason;
    @FXML
    public Text questionAmount;
    @FXML
    public Text questionDate;
    @FXML
    public Text questionSplit;

    private Expense selectedExpense;

    private List<Participant> selectedParticipants = new ArrayList<>();


    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
        selectPayer.setCellFactory(param -> new ListCell<Participant>() {
            @Override
            protected void updateItem(Participant participant, boolean empty) {
                super.updateItem(participant, empty);
                if (empty || participant == null) {
                    setText(null);
                } else {
                    setText(participant.getName());
                }
            }
        });


        // Set the value factory to retain the actual Participant object
        selectPayer.setButtonCell(new ListCell<Participant>() {
            @Override
            protected void updateItem(Participant participant, boolean empty) {
                super.updateItem(participant, empty);
                if (empty || participant == null) {
                    setText(mainCtrl.getPreferedLanguage().getText("select"));
                } else {
                    setText(participant.getName());
                }
            }
        });
        currency.getItems().addAll("EUR", "USD", "CHF");
//        selectPayer.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                selectPayer.show();
//
//            } else {
//                selectPayer.hide();
//            }
//        });
        currency.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                currency.show();
            } else {
                currency.hide();
            }
        });
        datePicker.setChronology(Chronology.ofLocale(java.util.Locale.getDefault()));
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
        datePicker.setConverter(stringConverter);
        titleName.setOnAction(this::focusOnNextField);
        amount.setOnAction(this::focusOnNextField);
        selectPayer.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                selectPayer.show();
            }
            if (event.getCode() == KeyCode.ENTER) {
                focusOnNextField(new ActionEvent());
            }
        });
        currency.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                currency.show();
            }
            if (event.getCode() == KeyCode.ENTER) {
                focusOnNextField(new ActionEvent());
            }
        });

        splitEquallyCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                Boolean newValue) {
                for (Node node : participantFlowPane.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) node;
                        checkBox.setSelected(newValue);
                        checkBox.setDisable(newValue);
                    }
                }
            }
        });

    }


    @Override
    public void refreshContent(Object... args) {
        long eventId = (long) args[0];
        selectedExpense = (Expense) args[1];
        boolean add = (boolean) args[2];
        mainCtrl.currentEvent = server.getEventById(eventId);
        selectPayer.requestFocus();


        if (mainCtrl.currentEvent != null) {
            selectPayer.getItems().addAll(mainCtrl.currentEvent.getParticipants());
            datePicker.setValue(LocalDate.now());
            // Set default selection (optional)
            if (mainCtrl.preferedLanguage.toString().equals("USD")) {
                currency.getSelectionModel().select(1);
            } else if (mainCtrl.preferedLanguage.toString().equals("CAD")) {
                currency.getSelectionModel().select(2);
            } else {
                currency.getSelectionModel().selectFirst();
            }
            for (Participant participant : mainCtrl.currentEvent.getParticipants()) {
                CheckBox checkBox = new CheckBox(participant.getName());
                checkBox.getStyleClass().add("check-box1");
                checkBox.setUserData(participant);
                checkBox.setSelected(false);

                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        selectedParticipants.add(participant);
                    } else {
                        selectedParticipants.remove(participant);
                    }
                });

                checkBox.setSelected(selectedParticipants.contains(participant));
                participantFlowPane.getChildren().add(checkBox);
            }

            if (add) {
                sceneTitle.setText(mainCtrl.getPreferedLanguage().getText("expenseAdd"));
                removeButton.setVisible(false);
                cancelButton.setText(mainCtrl.getPreferedLanguage().getText("cancel"));
                addButton.setText(mainCtrl.getPreferedLanguage().getText("add"));
                splitEquallyCheckBox.setSelected(false);
            } else {
                addButton.setText(mainCtrl.getPreferedLanguage().getText("confirm"));
                cancelButton.setText(mainCtrl.getPreferedLanguage().getText("cancel"));
                removeButton.setVisible(true);
                removeButton.setDisable(false);
                sceneTitle.setText(mainCtrl.getPreferedLanguage().getText("expenseEditRemove"));
                splitEquallyCheckBox.setSelected(false);

                titleName.setText(selectedExpense.getTitle());
                selectPayer.setValue(selectedExpense.getPayer());

                Currency expenseCurrency = selectedExpense.getCurrency();

                for (String item : currency.getItems()) {
                    Currency currency1 = Currency.getInstance(item);
                    if (currency1.getCurrencyCode().
                            equalsIgnoreCase(expenseCurrency.getCurrencyCode())) {
                        currency.setValue(item);
                        break;
                    }
                }
                String amountValue = String.valueOf(selectedExpense.getAmount());
                StringBuilder formattedAmount = new StringBuilder(amountValue);
                formattedAmount.insert(amountValue.length() - 2, ".");
                amount.setText(formattedAmount.toString());

                String dateString = selectedExpense.getDate();
                LocalDate date = LocalDate.parse(dateString);
                datePicker.setValue(date);

                List<Participant> participants = selectedExpense.getParticipants();
                int numSelected = 0;
                for (Participant participant : participants) {
                    for (Object item : participantFlowPane.getChildren()) {
                        if (item instanceof CheckBox) {
                            CheckBox checkBox = (CheckBox) item;
                            Participant checkBoxParticipant = (Participant) checkBox.getUserData();
                            if (checkBoxParticipant != null &&
                                    checkBoxParticipant.equals(participant)) {
                                checkBox.setSelected(true);
                                numSelected += 1;
                                break;
                            }
                        }
                    }
                }
                if (numSelected == mainCtrl.currentEvent.getParticipants().size()) {
                    splitEquallyCheckBox.setSelected(true);
                }
            }
        } else {
            System.out.println("The event is not found in the database! " +
                    "It may have been removed by other user!");
            mainCtrl.showOverview();
        }


    }

    @Override
    public void refreshLanguage() {
        questionPaid.setText(mainCtrl.getPreferedLanguage().getText("questionPaid"));
        questionReason.setText(mainCtrl.getPreferedLanguage().getText("questionReason"));
        questionAmount.setText(mainCtrl.getPreferedLanguage().getText("questionAmount"));
        questionDate.setText(mainCtrl.getPreferedLanguage().getText("questionDate"));
        questionSplit.setText(mainCtrl.getPreferedLanguage().getText("questionSplit"));

        removeButton.setText(mainCtrl.getPreferedLanguage().getText(("remove")));
        amount.setPromptText(mainCtrl.getPreferedLanguage().getText(("amount")));
        selectPayer.setPromptText(mainCtrl.getPreferedLanguage().getText(("select")));
        currency.setPromptText(mainCtrl.getPreferedLanguage().getText(("currency")));
        titleName.setPromptText(mainCtrl.getPreferedLanguage().getText(("hereWrite")) + "...");
        datePicker.setPromptText(mainCtrl.getPreferedLanguage().getText(("chooseDate")));
        splitEquallyCheckBox.setText(mainCtrl.getPreferedLanguage().getText(("splitOption")));
        selectPayer.setPromptText(mainCtrl.getPreferedLanguage().getText("select"));
    }

    @Override
    public void clearFields() {
        titleName.clear();
        amount.clear();

        selectPayer.getItems().clear();

        datePicker.setValue(null);

        participantFlowPane.getChildren().clear();

        resultAddEditText.setText("");
        selectedParticipants.clear();
        selectPayer.requestFocus();
    }

    public void focusOnNextField(ActionEvent actionEvent) {
        switch (titleName.getScene().getFocusOwner().getId()) {
            case "selectPayer" -> titleName.requestFocus();
            case "titleName" -> amount.requestFocus();
            case "amount" -> currency.requestFocus();
            case "currency" -> datePicker.requestFocus();
            case "datePicker" -> {
                participantFlowPane.getChildren().getFirst().requestFocus();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                mainCtrl.closePopupStage();
                break;
            case ENTER:
                if (keyEvent.isControlDown()) {
                    // TODO
                }
                break;
        }

    }

    public void add(ActionEvent actionEvent) {

        String title = titleName.getText();
        if (updateTitleError(title)) {
            return;
        }
        System.out.println(title);

        Participant selectedPayer = selectPayer.getValue();
        if (updatePayerError(selectedPayer)) {
            return;
        }

        String currencyCode = currency.getValue();
        Currency selectedCurrency = Currency.getInstance(currencyCode);

        String enteredAmount = amount.getText();
        if (updateAmountError(enteredAmount)) {
            return;
        }
        float amount = Float.parseFloat(enteredAmount);
        int intAmount = Math.round(100 * amount);

        LocalDate localDate = datePicker.getValue();
        if (updateDateError(localDate, datePicker.getConverter())) {
            return;
        }
        if (updateSelectParticipantError(selectedParticipants)) {
            return;
        }

        if (sceneTitle.getText().equals(mainCtrl.getPreferedLanguage().getText("expenseAdd"))) {

            Expense expense = new Expense(selectedPayer, selectedCurrency,
                    intAmount, title, localDate.toString(),
                    selectedParticipants, mainCtrl.currentEvent, false);
            double toEuroRate = mainCtrl.currencyExchangeService.getExchangeRate(selectedCurrency,
                    Currency.getInstance("EUR"), expense.getDate(), server);
            if (toEuroRate == -1.0) {
                toEuroRate = 1.0;
            }
            expense.setAmountInEuro((int) Math.round(toEuroRate * intAmount));

            server.addExpense(expense);
            refreshPage(mainCtrl.currentEvent.getId(), null, true);
            resultAddEditText.setText(
                    String.format(mainCtrl.getPreferedLanguage()
                            .getText("expenseAddSuccess")) + "!");
            resultAddEditText.setFill(Color.GREEN);
        } else {

            System.out.println("FINAL" + selectedParticipants.toString());

            selectedExpense.setPayer(selectedPayer);
            selectedExpense.setTitle(title);
            selectedExpense.setCurrency(selectedCurrency);
            selectedExpense.setAmount(intAmount);
            selectedExpense.setDate(localDate.toString());
            selectedExpense.setEvent(mainCtrl.currentEvent);
            selectedExpense.setParticipants(selectedParticipants);

            if (mainCtrl.showVerificationPopup(String.format(mainCtrl.getPreferedLanguage()
                    .getText("verifyExpenseConfirm") + "\n" +
                    mainCtrl.getPreferedLanguage()
                            .getText("actionUndone")))) {
                server.updateExpense(selectedExpense);

                resultAddEditText.setText(mainCtrl.getPreferedLanguage()
                        .getText("expenseUpdated") + "!");
                resultAddEditText.setFill(Color.GREEN);
            }
        }

    }

    public void editCancel(ActionEvent actionEvent) {
        System.out.println(sceneTitle.getText());
        if (!sceneTitle.getText().equals(mainCtrl
                .getPreferedLanguage().getText("expenseEditRemove"))) {
            keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, null, null,
                    KeyCode.ESCAPE, false, false, false, false));
            return;
        }
        String title = titleName.getText();

        Participant selectedPayer = selectPayer.getValue();

        String currencyCode = currency.getValue();
        Currency selectedCurrency = Currency.getInstance(currencyCode);

        String enteredAmount = amount.getText();
        float amount = Float.parseFloat(enteredAmount);
        int intAmount = Math.round(100 * amount);

        LocalDate localDate = datePicker.getValue();
        if (selectedExpense != null && (!title.equals(selectedExpense.getTitle()) ||
                !(selectedPayer.getId() == selectedExpense.getPayer().getId()) ||
                !selectedCurrency.equals(selectedExpense.getCurrency()) ||
                !(intAmount == selectedExpense.getAmount()) ||
                !localDate.toString().equals(selectedExpense.getDate()))) {
            if (mainCtrl.showVerificationPopup(String.format(mainCtrl.getPreferedLanguage()
                    .getText("verifyExpenseEditCancel")))) {
                keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, null, null,
                        KeyCode.ESCAPE, false, false, false, false));
            }
        } else {
            keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, null, null,
                    KeyCode.ESCAPE, false, false, false, false));
        }
    }

    public void remove(ActionEvent event) {
        if (mainCtrl.showVerificationPopup(mainCtrl.getPreferedLanguage()
                .getText("verifyExpenseDelete") + "?\n" +
                mainCtrl.getPreferedLanguage().getText("actionUndone") + ".")) {
            server.deleteExpense(selectedExpense);

            if (mainCtrl.showConfirmationPopUp(mainCtrl.getPreferedLanguage()
                    .getText("expenseDeleteSuccess"))) {
                goBack(event);
            }

        }

    }


    public boolean updatePayerError(Participant payer) {
        if (payer == null) {
            resultAddEditText.setFill(Color.RED);
            resultAddEditText.setText(mainCtrl.getPreferedLanguage()
                    .getText("noPayerExpense") + "!");
            return true;
        }
        return false;
    }

    public boolean updateDateError(LocalDate localDate, StringConverter<LocalDate> converter) {
        if (localDate == null) {
            resultAddEditText.setText(mainCtrl.getPreferedLanguage()
                    .getText("noDateExpense") + "!");
            resultAddEditText.setFill(Color.RED);
            return true;
        }
        LocalDate minDate = converter.fromString("2020-01-01"); // Example: Set minimum date
        LocalDate maxDate = converter.fromString("2100-12-31"); // Example: Set maximum date

        if (localDate.isBefore(minDate) || localDate.isAfter(maxDate)) {
            resultAddEditText.setText(mainCtrl.getPreferedLanguage()
                    .getText("dateBetween") + minDate
                    + " " + mainCtrl.getPreferedLanguage()
                    .getText("and") + " " + maxDate + "!");
            resultAddEditText.setFill(Color.RED);
            return true;
        }
        return false;
    }

    public boolean updateAmountError(String amount) {
        if (!amount.matches("^\\d+(\\.\\d{1,2})?$")) {
            resultAddEditText.setFill(Color.RED);
            resultAddEditText.setText(mainCtrl.getPreferedLanguage()
                    .getText("invalidAmount") + "!\n" +
                    mainCtrl.getPreferedLanguage().getText("enterValidAmount") + "!");
            return true;
        }
        return false;
    }

    public boolean updateTitleError(String title) {
        if (title.isEmpty()) {
            resultAddEditText.setFill(Color.RED);
            resultAddEditText.setText(mainCtrl.getPreferedLanguage()
                    .getText("noTitleExpense") + "!");
            return true;
        }
        return false;
    }

    public boolean updateSelectParticipantError(List<Participant> participants) {
        if (participants.isEmpty()) {
            resultAddEditText.setFill(Color.RED);
            resultAddEditText.setText(mainCtrl.getPreferedLanguage()
                    .getText("noSelectExpense") + "!");
            return true;
        }
        return false;
    }
}
