package client.scenes;

import client.utils.ServerUtils;
import commons.Expense;
import commons.Participant;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OverviewCtrl extends PageController implements Initializable {
    private ObservableList<Participant> participantData;
    @FXML
    public Text eventName;
    @FXML
    public TableView<Expense> expenseTable;

    @FXML
    public Text addEditRemove;

    @FXML
    public TableColumn<Expense, String> expenseDate;
    @FXML
    public TableColumn<Expense, String> expenseTitle;
    @FXML
    public TableColumn<Expense, String> expenseAmount;
    @FXML
    public TableColumn<Expense, String> expensePayer;
    @FXML
    public TableColumn<Expense, String> expenseParticipants;

    @FXML
    public Button sendInvitesButton;

    @FXML
    public Button changeEventTitleButton;
    @FXML
    public Button addExpenseButton;
    @FXML
    public Button settleDebtsButton;
    @FXML
    public Button summaryButton;

    @FXML
    private Tooltip editParticipantTooltip;
    @FXML
    private Tooltip addParticipantTooltip;
    @FXML
    private Tooltip inviteParticipantTooltip;
    @FXML
    private Tooltip addExpenseTooltip;
    @FXML
    private Tooltip settleDebtsTooltip;
    @FXML
    private Tooltip seeSummaryTooltip;

    @FXML
    public Text participantLabel;
    @FXML
    public Text expenseLabel;

    @FXML
    public ComboBox<Participant> filterMenu;

    @FXML
    public CheckBox filterOnPayerCheckbox;
    @FXML
    public CheckBox filterOnPayeeCheckbox;


    @FXML
    public Text participantNames;
    @FXML
    public TextField newEventName;
    @FXML
    public Button titleCancelButton;

    @FXML
    public Text tipText;
    @FXML
    public Button titleConfirmButton;
    @FXML
    public Label changeEventTitle;
    @FXML
    private Button languageButton;


    @FXML
    public Text changeEventNameErrorText;

    @FXML
    public ImageView closeFilterImageView;

    private Boolean initializedInstance;

    private Predicate<Expense> filteringRule;

    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializedInstance = false;
        // initializing formatting of expense entries and initializing columns
        expenseTable.getColumns().clear();

        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Expense selectedExpense = row.getItem();
                    if (selectedExpense != null && !selectedExpense.getIsSettlement()) {
                        editRemoveExpense(selectedExpense);
                        // Call your method with selected expense
                    }
                }
            });
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    row.getStyleClass().add("selected-row");
                } else {
                    row.getStyleClass().remove("selected-row");
                }
            });
            return row;
        });

        expenseDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getDate()
                ));

        // set title or tag of the expense
        expenseTitle.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getTitle()
                ));

        // set amount paid
        expenseAmount.setCellValueFactory(cellData -> {
                String currency = mainCtrl.preferredCurrency.getCurrencyCode();
                double rate = mainCtrl.currencyExchangeService.getExchangeRate(
                        cellData.getValue().getCurrency(),
                        mainCtrl.preferredCurrency, cellData.getValue().getDate(), server);
                if (rate == -1.0) {
                    currency = "EUR";
                    rate = 1.0;
                }
                return new SimpleStringProperty(
                        String.format("%.2f",
                                cellData.getValue().getAmount() * rate / 100.0) + " " +
                                currency
                );
            }
        );
        // set payer
        expensePayer.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getPayer()
                                .getName()
                ));
        // set participant who need to pay
        expenseParticipants.setCellValueFactory(cellData -> {
            List<Participant> participants = cellData.getValue().getParticipants();
            String names = participants.stream()
                    .map(Participant::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(names);
        });


        eventName.setOnMouseEntered(event -> {
            eventName.setCursor(Cursor.HAND);
        });

        // Change cursor back to DEFAULT when mouse exits the Text
        eventName.setOnMouseExited(event -> {
            eventName.setCursor(Cursor.DEFAULT);
        });

        expenseTable.getColumns().addAll(expenseDate, expenseTitle, expenseAmount,
                expensePayer, expenseParticipants);

        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            Tooltip tooltip = new Tooltip(mainCtrl.getPreferedLanguage().getText("doubleClick"));

            Tooltip.install(row, tooltip);

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Expense selectedExpense = row.getItem();
                    if (selectedExpense != null) {
                        editRemoveExpense(selectedExpense);
                        // Call your method with selected expense
                    }
                }
            });


            return row;
        });
        expenseTable.setFocusTraversable(true);

        expenseTable.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                expenseTable.getSelectionModel().selectFirst();
            }
        });

        expenseTable.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case TAB:
                    if (event.isShiftDown()) {
                        if (expenseTable.getSelectionModel().getSelectedIndex() > 0) {
                            expenseTable.getSelectionModel().selectPrevious();
                        }
                    } else {
                        if (expenseTable.getSelectionModel().
                                getSelectedIndex() < expenseTable.getItems().size() - 1) {
                            expenseTable.getSelectionModel().selectNext();
                        }
                    }
                    event.consume();
                    break;
                case ENTER:
                    Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
                    if (selectedExpense != null) {
                        editRemoveExpense(selectedExpense);
                    }
                    event.consume();
                    break;
            }
        });
        filterMenu.setCellFactory(param -> new ListCell<Participant>() {
            @Override
            protected void updateItem(Participant participant, boolean empty) {
                super.updateItem(participant, empty);
                if (participant == null || empty) {
                    setText(null);
                } else {
                    setText(participant.getName());
                }
            }
        });
        filterMenu.setButtonCell(new ListCell<Participant>() {
            @Override
            protected void updateItem(Participant participant, boolean empty) {
                super.updateItem(participant, empty);
                if (participant == null || empty) {
                    setText("Filter");
                    closeFilterImageView.setDisable(true);
                    filterOnPayerCheckbox.setDisable(true);
                    filterOnPayeeCheckbox.setDisable(true);
                    filterOnPayerCheckbox.setSelected(false);
                    filterOnPayeeCheckbox.setSelected(false);
                } else {
                    closeFilterImageView.setDisable(false);
                    if (filterOnPayeeCheckbox.isDisable()) {
                        filterOnPayerCheckbox.setSelected(true);
                        filterOnPayeeCheckbox.setSelected(true);
                    }
                    filterOnPayerCheckbox.setDisable(false);
                    filterOnPayeeCheckbox.setDisable(false);
                    setText(participant.getName());
                    refreshFiltering();
                }
            }
        });
        filterMenu.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                filterMenu.show();
            }
        });
        filterOnPayerCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                closeFilter(null);
            }
            refreshFiltering();
        });

        filterOnPayeeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                closeFilter(null);
            }
            refreshFiltering();
        });

        setFilteringRule(e -> true);
    }

    public void registerListener() {
        if (!initializedInstance) {
            server.registerForMessages("/events/terminator/event",
                    Long.class, e -> {
                        if (e == mainCtrl.currentEvent.getId()) {
                            if (!mainCtrl.adminIsParent) {
                                mainCtrl.showStart();
                            } else {
                                mainCtrl.showAdminPage();
                            }
                        }
                    });
            initializedInstance = true;
        }
    }


    /***
     * Refresh the page and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        registerListener();
        refreshLanguage();
        clearFields();
        setFilteringRule(e -> true);
        long eventId = (long) args[0];
        server.pollingId++;
        mainCtrl.currentEvent = server.getEventById(eventId);
        if (mainCtrl.currentEvent != null) {
            eventName.setText(mainCtrl.currentEvent.getTitle());
            List<Participant> participants = mainCtrl.currentEvent.getParticipants();
            participantData = FXCollections.observableList(participants);
            if (!participants.isEmpty()) {
                participantNames.setText(mainCtrl.currentEvent.getParticipants().stream()
                        .map(Participant::getName).collect(Collectors.joining(", ")));
                filterMenu.getItems().addAll(participants);
                filterMenu.setDisable(false);
            } else {
                participantNames.setText(mainCtrl.preferedLanguage.getText("noParticipants"));
                filterMenu.setDisable(true);
            }
            List<Expense> expenses = mainCtrl.currentEvent.getExpenses();
            if (!expenses.isEmpty()) {
                // create observable list of expense entries
                refreshFiltering();
            } else {
                // No expenses exist to add
                expenseTable.setPlaceholder(new Label(
                        mainCtrl.preferedLanguage.getText("noExpenses")));
            }
            server.registerForUpdates("/event/{eventId}/participant/updates",
                    mainCtrl.currentEvent.getId(), p -> {
                        Platform.runLater(() -> {
                            if (mainCtrl.currentEvent != null) {
                                simpleRefresh(mainCtrl.currentEvent.getId());
                            } else {
                                mainCtrl.showStart();
                            }
                        });
                    }, "OverviewCtrl");
            server.registerForUpdates("/event/{eventId}/expense/updates",
                    mainCtrl.currentEvent.getId(), p -> {
                        Platform.runLater(() -> {
                            if (mainCtrl.currentEvent != null) {
                                simpleRefresh(mainCtrl.currentEvent.getId());
                            } else {
                                mainCtrl.showStart();
                            }
                        });
                    }, "OverviewCtrl");
            server.registerForUpdates("/event/{eventId}/updates",
                    mainCtrl.currentEvent.getId(), p -> {
                        Platform.runLater(() -> {
                            if (mainCtrl.currentEvent != null) {
                                simpleRefresh(mainCtrl.currentEvent.getId());
                            } else {
                                mainCtrl.showStart();
                            }
                        });
                    }, "OverviewCtrl");
        } else {
            System.out.println("The event is not found in the database! " +
                    "It may have been removed by other user!");
            if (mainCtrl.adminIsParent) {
                server.pollingId++;
                mainCtrl.showAdminPage();
            } else {
                server.pollingId++;
                mainCtrl.showStart();
            }
        }
    }

    public void simpleRefresh(Object... args) {
        // TODO finalize this function
        Participant filteredByP = filterMenu.getValue();
        Boolean payerSelection = filterOnPayerCheckbox.isSelected();
        Boolean payeeSelection = filterOnPayeeCheckbox.isSelected();
        refreshLanguage();
        clearFields();
        long eventId = (long) args[0];
        mainCtrl.currentEvent = server.getEventById(eventId);
        if (mainCtrl.currentEvent != null) {
            eventName.setText(mainCtrl.currentEvent.getTitle());
            List<Participant> participants = mainCtrl.currentEvent.getParticipants();
            participantData = FXCollections.observableList(participants);
            if (!participants.isEmpty()) {
                participantNames.setText(mainCtrl.currentEvent.getParticipants().stream()
                        .map(Participant::getName).collect(Collectors.joining(", ")));
                filterMenu.getItems().addAll(participants);
                filterMenu.setDisable(false);
                if (filteredByP != null) {
                    filterMenu.setValue(participants.stream().filter(p -> p.getId() ==
                            filteredByP.getId()).toList().getFirst());
                    filterOnPayeeCheckbox.setSelected(payerSelection);
                    filterOnPayeeCheckbox.setSelected(payeeSelection);
                }
            } else {
                participantNames.setText(mainCtrl.preferedLanguage.getText("noParticipants"));
                filterMenu.setDisable(true);
            }
            List<Expense> expenses = mainCtrl.currentEvent.getExpenses();
            if (!expenses.isEmpty()) {
                // create observable list of expense entries
                refreshFiltering();
            } else {
                // No expenses exist to add
                expenseTable.setPlaceholder(new Label(
                        mainCtrl.preferedLanguage.getText("noExpenses")));
            }
        } else {
            System.out.println("The event is not found in the database! " +
                    "It may have been removed by other user!");
            if (mainCtrl.adminIsParent) {
                server.pollingId++;
                mainCtrl.showAdminPage();
            } else {
                server.pollingId++;
                mainCtrl.showStart();
            }
        }
    }

    public void refreshFiltering() {
        if (filterMenu.getValue() == null) {
            setFilteringRule(e -> true);
        } else {
            Participant participant = filterMenu.getValue();
            Predicate<Expense> p1 = e -> e.getPayer().getId() == participant.getId();
            Predicate<Expense> p2 = e -> e.getParticipants().stream()
                    .anyMatch(p -> p.getId() == participant.getId());
            if (filterOnPayerCheckbox.isSelected()) {
                if (filterOnPayeeCheckbox.isSelected()) {
                    setFilteringRule(e -> p1.test(e) || p2.test(e));
                } else {
                    setFilteringRule(p1);
                }
            } else if (filterOnPayeeCheckbox.isSelected()) {
                setFilteringRule(p2);
            } else {
                setFilteringRule(e -> false);
            }
        }
        runFilter();
    }

    // CAUTION : when filtering by participant prefer using participant ids instead
    // of the whole class participants may change, but the filtering should persist
    public void setFilteringRule(Predicate<Expense> p) {
        filteringRule = p;
    }

    public void runFilter() {
        expenseTable.setItems(FXCollections.observableArrayList(
                mainCtrl.currentEvent.getExpenses().stream()
                        .filter(filteringRule).toList()));
    }

    @Override
    public void refreshLanguage() {
        // refresh flag icon here
        languageButton.setGraphic(
                mainCtrl.getPreferedLanguage()
                        .getImageView(
                                mainCtrl.getPreferedLanguage()
                                        .getCurrentLanguage()));

        newEventName.setPromptText(mainCtrl.getPreferedLanguage().getText("eventNameChange"));
        titleCancelButton.setText(mainCtrl.getPreferedLanguage().getText("cancel"));
        titleConfirmButton.setText(mainCtrl.getPreferedLanguage().getText("confirm"));
        changeEventTitle.setText(mainCtrl.getPreferedLanguage().getText("enterEventTitle"));

        participantLabel.setText(mainCtrl.preferedLanguage.getText("participants"));
        expenseLabel.setText(mainCtrl.preferedLanguage.getText("expenses"));
        addExpenseButton.setText(mainCtrl.preferedLanguage.getText("expenseAdd"));
        settleDebtsButton.setText(mainCtrl.preferedLanguage.getText("settleDebts"));
        sendInvitesButton.setText(mainCtrl.preferedLanguage.getText("sendInvites"));
        filterMenu.setPromptText(mainCtrl.preferedLanguage.getText("filter"));
        addEditRemove.setText(mainCtrl.preferedLanguage.getText("addEditRemove"));

        expenseDate.setText(mainCtrl.preferedLanguage.getText("expenseDate"));
        expenseTitle.setText(mainCtrl.preferedLanguage.getText("expenseTitle"));
        expenseAmount.setText(mainCtrl.preferedLanguage.getText("expenseAmount"));
        expensePayer.setText(mainCtrl.preferedLanguage.getText("expensePayer"));
        expenseParticipants.setText(mainCtrl.preferedLanguage.getText("expenseParticipants"));
        summaryButton.setText(mainCtrl.preferedLanguage.getText("summary"));

        filterOnPayerCheckbox.setText(mainCtrl.preferedLanguage.getText("filterOnPayer"));
        filterOnPayeeCheckbox.setText(mainCtrl.preferedLanguage.getText("filterOnPayee"));

        editParticipantTooltip.setText(mainCtrl.preferedLanguage.getText("editParticipantTooltip"));
        addParticipantTooltip.setText(mainCtrl.preferedLanguage.getText("addParticipantTooltip"));
        inviteParticipantTooltip.setText(mainCtrl.preferedLanguage
                .getText("inviteParticipantTooltip"));
        addExpenseTooltip.setText(mainCtrl.preferedLanguage.getText("addExpenseTooltip"));
        settleDebtsTooltip.setText(mainCtrl.preferedLanguage.getText("settleDebtsTooltip"));
        seeSummaryTooltip.setText(mainCtrl.preferedLanguage.getText("seeSummaryTooltip"));
        tipText.setText(mainCtrl.preferedLanguage.getText("tipText"));
    }

    /**
     * Displays language selection popup
     *
     * @param actionEvent
     */
    public void changeLanguage(ActionEvent actionEvent) {
        mainCtrl.showLanguage();
    }

    public void changeEventName() {
        mainCtrl.currentEvent = server.getEventById(mainCtrl.currentEvent.getId());
        String newTitle = newEventName.getText();
        if (newTitle.isEmpty()) {
            changeEventNameErrorText.setText(
                    mainCtrl.getPreferedLanguage().getText("emptyEventFail"));
            return;
        }
        mainCtrl.currentEvent.setTitle(newTitle);
        server.updateEvent(mainCtrl.currentEvent);
        mainCtrl.closePopupStage();
    }

    public void closeTitleChange() {
        mainCtrl.closePopupStage();
    }

    public void changeName() {
        mainCtrl.changeTitle();
    }

    public void showSummary() {
        mainCtrl.showSummary();
    }

    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {
        participantNames.setText("No participants yet :)");
        newEventName.setText(eventName.getText());
        changeEventNameErrorText.setText("");
        filterMenu.getItems().clear();
        closeFilterImageView.setDisable(true);
        expenseTable.getItems().clear();
    }

    /***
     * Event handler for handling when keys are entered on page
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE -> {
                if (!mainCtrl.adminIsParent) {
                    server.pollingId++;
                    mainCtrl.showStart();
                } else {
                    server.pollingId++;
                    mainCtrl.showAdminPage();
                }
            }
            case P -> mainCtrl.showAddParticipant();
            case E -> mainCtrl.showEditParticipant();
            case I -> mainCtrl.showInviteParticipant();
            case X -> mainCtrl.showAddExpense(null);
            case D -> mainCtrl.showDebt();
            case S -> mainCtrl.showSummary();
            case H -> mainCtrl.showClientInfoPage();
        }
    }

    /***
     * Add expense event handler for when "add expense" is selected
     * @param actionEvent
     */

    public void addExpense(ActionEvent actionEvent) {
        mainCtrl.showAddExpense(null);
    }


    public void editRemoveExpense(Expense expense) {
        mainCtrl.showEditRemoveExpense(expense);
    }

    /**
     * Settle debt event handler for when "settle debt" is selected
     *
     * @param actionEvent
     */
    public void settleDebt(ActionEvent actionEvent) {
        // enter the debt page scene with the current event id
        mainCtrl.showDebt();
    }

    /***
     * Send invite event handler for when "send invites" is selected
     * @param actionEvent
     */
    public void sendInvites(ActionEvent actionEvent) {
        mainCtrl.showInviteParticipant();
    }


    /***
     * Edit user event handler for when "edit participant" is selected
     * @param actionEvent
     */
    public void editUsers(ActionEvent actionEvent) {
        mainCtrl.showEditParticipant();
    }

    /***
     * Add user event handler for when "add participant" is selected
     * @param actionEvent
     */
    public void addUser(ActionEvent actionEvent) {
        mainCtrl.showAddParticipant();
    }

    public void closeFilter(MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            filterMenu.setValue(null);
        }
        setFilteringRule(e -> true);
        runFilter();
    }

    public void showInfo(ActionEvent actionEvent) {
        mainCtrl.showOverviewInfoPage();
    }
}
