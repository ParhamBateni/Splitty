package client.scenes;

import client.utils.ServerUtils;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static commons.Participant.*;

public class AddEditParticipantCtrl extends PageController implements Initializable {
    @FXML
    public HBox selectParticipantBox;
    @FXML
    public ComboBox<Participant> selectParticipantMenu;
    @FXML
    public TextField bicParticipant;
    @FXML
    public TextField nameParticipant;
    @FXML
    public TextField emailParticipant;
    @FXML
    public TextField ibanParticipant;
    @FXML
    public Text sceneTitle;
    @FXML
    public Text nameErrorText;
    @FXML
    public Text emailErrorText;
    @FXML
    public Text ibanErrorText;
    @FXML
    public Text bicErrorText;
    @FXML
    public Text successfulAddEditText;
    @FXML
    public Button removeButton;
    @FXML
    public Button okButton;
    @FXML
    public Button cancelButton;
    @FXML
    public Text selectParticipantText;
    @FXML
    public Text nameText;
    @FXML
    public Text emailText;
    @FXML
    public Text ibanText;
    @FXML
    public Text bicText;
    private Participant selectedParticipant;


    private int counter = 1;

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
        nameParticipant.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                updateNameErrorText(newValue);
            } else {
                nameErrorText.setText("");
            }
        });
        emailParticipant.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                updateEmailErrorText(newValue);
            } else {
                emailErrorText.setText("");
            }
        });
        ibanParticipant.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                updateIbanErrorText(newValue);
            } else {
                ibanErrorText.setText("");
            }
        });
        bicParticipant.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                updateBicErrorText(newValue);
            } else {
                bicErrorText.setText("");
            }
        });
        selectParticipantMenu.setOnAction(selectParticipantBoxHandler());
        selectParticipantMenu.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(mainCtrl.getPreferedLanguage().getText("select"));
                } else {
                    setText(item.getName());
                }
            }
        });
        selectParticipantMenu.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Participant> call(ListView<Participant> param) {
                return new ListCell<Participant>() {
                    @Override
                    protected void updateItem(Participant participant, boolean empty) {
                        super.updateItem(participant, empty);
                        if (empty || participant == null) {
                            setText(null);
                        } else {
                            setText(participant.getName());
                        }
                    }
                };
            }
        });
        selectParticipantMenu.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                selectParticipantMenu.show();
            } else if (event.getCode() == KeyCode.ENTER) {
                nameParticipant.requestFocus();
            }
        });

    }

    /***
     * Event handler for when a participant is selected from the combo box to be edited
     * @return
     */
    private EventHandler<ActionEvent> selectParticipantBoxHandler() {
        return e -> {
            selectedParticipant = selectParticipantMenu.getSelectionModel().getSelectedItem();
            if (selectedParticipant != null) {
                nameParticipant.setText(selectedParticipant.getName());
                emailParticipant.setText(selectedParticipant.getEmail());
                ibanParticipant.setText(selectedParticipant.getIban());
                bicParticipant.setText(selectedParticipant.getBic());
                setFieldsDisable(false);
                removeButton.setDisable(false);
            } else {
                setFieldsDisable(true);
                removeButton.setDisable(true);
            }
        };
    }

    @Inject
    public AddEditParticipantCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);

    }

    /***
     * refreshContent the page and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        long eventId = (long) args[0];
        boolean add = (boolean) args[1];
        mainCtrl.currentEvent = server.getEventById(eventId);
        if (add) {
            selectParticipantBox.setVisible(false);
            removeButton.setVisible(false);
            sceneTitle.setText(mainCtrl.getPreferedLanguage().getText("participantAddTitle"));
            setFieldsDisable(false);
            counter = mainCtrl.currentEvent.getParticipants().size() + 1;
            okButton.setText(mainCtrl.getPreferedLanguage().getText("add"));

        } else {
            selectParticipantBox.setVisible(true);
            selectParticipantMenu.requestFocus();
            removeButton.setVisible(true);
            sceneTitle.setText(mainCtrl.getPreferedLanguage().getText("participantEditTitle"));
            okButton.setText(mainCtrl.getPreferedLanguage().getText("confirm"));

            setFieldsDisable(true);
            if (mainCtrl.currentEvent != null) {
                selectParticipantMenu.getItems()
                        .addAll(mainCtrl.currentEvent.getParticipants());
            } else {
                System.out.println("The event is not found in the database! " +
                        "It may have just been removed by other user!");
                mainCtrl.showStart();
            }
        }
    }

    @Override
    public void refreshLanguage() {
        selectParticipantText.setText(mainCtrl.getPreferedLanguage()
                .getText("selectParticipant") + ":");
        removeButton.setText(mainCtrl.getPreferedLanguage().getText("remove"));
        nameText.setText(mainCtrl.getPreferedLanguage().getText("name") + ":");
        emailText.setText(mainCtrl.getPreferedLanguage().getText("email") + ":");
        ibanText.setText(mainCtrl.getPreferedLanguage().getText("IBAN") + ":");
        bicText.setText(mainCtrl.getPreferedLanguage().getText("BIC") + ":");
        selectParticipantMenu.setPromptText(mainCtrl.getPreferedLanguage().getText("select"));
        okButton.setText(mainCtrl.getPreferedLanguage().getText("add"));
    }

    /***
     * disable all the text fields in the page
     * @param disable
     */
    private void setFieldsDisable(boolean disable) {
        nameParticipant.setDisable(disable);
        emailParticipant.setDisable(disable);
        ibanParticipant.setDisable(disable);
        bicParticipant.setDisable(disable);
    }

    /***
     * clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {
        nameParticipant.clear();
        nameParticipant.setPromptText(mainCtrl.getPreferedLanguage().getText("name"));
        emailParticipant.clear();
        emailParticipant.setPromptText(mainCtrl.getPreferedLanguage().getText("email_Optional"));
        ibanParticipant.clear();
        ibanParticipant.setPromptText(mainCtrl.getPreferedLanguage().getText("IBAN_Optional"));
        bicParticipant.clear();
        bicParticipant.setPromptText(mainCtrl.getPreferedLanguage().getText("BIC_Optional"));
        nameErrorText.setText("");
        emailErrorText.setText("");
        ibanErrorText.setText("");
        bicErrorText.setText("");
        successfulAddEditText.setText("");
        selectParticipantMenu.getItems().clear();
        nameParticipant.requestFocus();
    }

    /***
     * Event handler for handling when keys are entered on page
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                mainCtrl.closePopupStage();
                break;
            case C:
                if (keyEvent.isAltDown()) {
                    cheatAddParticipant();
                }
                break;
            case R:
                if (sceneTitle.getText().equals(
                        mainCtrl.getPreferedLanguage().getText("participantEditTitle"))) {
                    if (!removeButton.isDisabled()) {
                        remove(new ActionEvent());
                    }
                }
                break;
            case S:
                if (sceneTitle.getText().equals(mainCtrl
                        .getPreferedLanguage().getText("participantEditTitle"))) {
                    selectParticipantMenu.show();
                }
                break;
        }
    }

    /***
     * automatically set the fields with default values to facilitate users to enter inputs
     */
    private void cheatAddParticipant() {
        nameParticipant.setText(String.format("User%d", counter));
        emailParticipant.setText(String.format("email%d@gmail.com", counter));
        ibanParticipant.setText(String.format("SAMPLEIBANUSER%d", counter));
        bicParticipant.setText(String.format("BICUSER%d", counter));
        counter++;
    }

    /***
     * Event handler for whenever ok button is clicked on for getting input fields and sending
     * a request to server to either add a new participant or edit the participant information.
     * If there is an issue with any of the fields the text labels are updated and nothing happens.
     * @param actionEvent
     */
    public void ok(ActionEvent actionEvent) {
        String name = nameParticipant.getText();
        String email = emailParticipant.getText();
        String iban = ibanParticipant.getText();
        String bic = bicParticipant.getText();
        if (updateNameErrorText(name)
                | updateIbanErrorText(iban)
                | updateBicErrorText(bic)) {
            successfulAddEditText.setText("");
            return;
        }
        if (sceneTitle.getText().equals(mainCtrl
                .getPreferedLanguage().getText("participantEditTitle"))) {
            // Edit the user information
            if (selectedParticipant == null) {
                // TODO add feedback to the user here?
                System.out.println("Event or participant is not found in the database! " +
                        "It may have been just removed by another user!");
            } else {
                if (mainCtrl.showVerificationPopup(String.format(
                        mainCtrl.getPreferedLanguage()
                                .getText("verifyParticipantConfirm") + " %s?\n" +
                                mainCtrl.getPreferedLanguage()
                                        .getText("actionUndone"), name))) {
                    selectedParticipant.setName(name);
                    selectedParticipant.setEmail(email);
                    selectedParticipant.setIban(iban);
                    selectedParticipant.setBic(bic);
                    selectedParticipant = server.updateParticipant(selectedParticipant);
                    refreshPage(mainCtrl.currentEvent.getId(), false);
                    successfulAddEditText.setText(String.format("%s 's" +
                                    mainCtrl.getPreferedLanguage()
                                            .getText("participantEditSuccess"),
                            name) + "!");
                }
            }


        } else {
            // Add the user
            Participant participant = new Participant(name, email, iban,
                    bic, mainCtrl.currentEvent);
            System.out.println(participant.toString());
            server.addParticipant(participant);
            System.out.println(participant.getId());
            refreshPage(mainCtrl.currentEvent.getId(), true);
            successfulAddEditText
                    .setText(String.format("%s "
                            + mainCtrl.getPreferedLanguage()
                            .getText("participantAddSuccess"), name) + "!");
        }
    }

    public void remove(ActionEvent actionEvent) {
        boolean bad = false;
        for (var e : mainCtrl.currentEvent.getExpenses()) {
            if (e.getPayer() == selectedParticipant || e.getParticipants()
                    .contains(selectedParticipant)) {
                bad = true;
                break;
            }
        }
        if (bad) {
            bicErrorText.setText(
                    mainCtrl.getPreferedLanguage()
                            .getText("participantUsed"));
            return;
        }
        String name = selectedParticipant.getName();
        if (mainCtrl.showVerificationPopup(String.format(mainCtrl.getPreferedLanguage()
                .getText("verifyParticipantDelete") + " %s?\n" +
                mainCtrl.getPreferedLanguage()
                        .getText("actionUndone"), name))) {
            server.deleteParticipant(selectedParticipant);
            successfulAddEditText.setText(String.format("%s " +
                    mainCtrl.getPreferedLanguage()
                            .getText("participantDeleteSuccess"), name));
            keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, null, null,
                    KeyCode.ESCAPE, false, false, false, false));
            mainCtrl.showConfirmationPopUp(String.format("%s " + mainCtrl.getPreferedLanguage()
                    .getText("participantDeletedConfirmation"), name));
        }
    }

    public void editCancel(ActionEvent actionEvent) {
        String name = nameParticipant.getText();
        String email = emailParticipant.getText();
        String iban = ibanParticipant.getText();
        String bic = bicParticipant.getText();
        if (sceneTitle.getText().equals(mainCtrl
                .getPreferedLanguage().getText("participantEditTitle"))
                && selectedParticipant != null && (!name.equals(selectedParticipant.getName()) ||
                !email.equals(selectedParticipant.getEmail()) ||
                !iban.equals(selectedParticipant.getIban()) ||
                !bic.equals(selectedParticipant.getBic()))) {
            if (mainCtrl.showVerificationPopup(String.format(mainCtrl.getPreferedLanguage()
                    .getText("verifyParticipantEditCancel")))) {
                keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, null, null,
                        KeyCode.ESCAPE, false, false, false, false));
            }
        } else if (sceneTitle.getText().equals(mainCtrl
                .getPreferedLanguage().getText("participantAddTitle")) && (!name.isEmpty() ||
                !email.isEmpty() ||
                !iban.isEmpty() ||
                !bic.isEmpty())) {

            if (mainCtrl.showVerificationPopup(String.format(mainCtrl.getPreferedLanguage()
                    .getText("verifyParticipantAddCancel")))) {
                keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, null, null,
                        KeyCode.ESCAPE, false, false, false, false));
            }


        } else {
            keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, null, null,
                    KeyCode.ESCAPE, false, false, false, false));
        }
    }

    /***
     * if name is invalid update the name text label on page
     * @param name
     * @return
     */
    private boolean updateNameErrorText(String name) {
        if (!isNameValid(name)) {
            nameErrorText.setText(
                    mainCtrl.getPreferedLanguage()
                            .getText("patternName"));
        } else {
            nameErrorText.setText("");
        }
        return !isNameValid(name);
    }

    /***
     * if email is invalid update the email text label on page
     * @param email
     * @return
     */
    private boolean updateEmailErrorText(String email) {
        if (!isEmailValid(email)) {
            emailErrorText.setText(
                    mainCtrl.getPreferedLanguage()
                            .getText("patternEmail"));
        } else {
            emailErrorText.setText("");
        }
        return !isEmailValid(email);
    }

    /***
     * if iban is invalid update the iban text label on page
     * @param iban
     * @return
     */
    private boolean updateIbanErrorText(String iban) {
        if (!isIbanValid(iban)) {
            ibanErrorText.setText(
                    mainCtrl.getPreferedLanguage()
                            .getText("patternIBAN"));
        } else {
            ibanErrorText.setText("");
        }
        return !isIbanValid(iban);
    }

    /***
     * if bic is invalid update the bic text label on page
     * @param bic
     * @return
     */
    private boolean updateBicErrorText(String bic) {
        if (!isBicValid(bic)) {
            bicErrorText.setText(
                    mainCtrl.getPreferedLanguage()
                            .getText("patternBIC"));
        } else {
            bicErrorText.setText("");
        }
        return !isBicValid(bic);
    }

    /***
     * This event handler is for changing the focus of the text field to next empty or
     * missing text field whenever the user enters ENTER on a text field. If all the
     * fields are correctly filled already calls the 'ok' method
     * @param actionEvent
     */
    public void focusOnNextField(ActionEvent actionEvent) {
        if (!isNameValid(nameParticipant.getText())) {
            nameParticipant.requestFocus();
        } else if (!isEmailValid(emailParticipant.getText())) {
            emailParticipant.requestFocus();
        } else if (!isIbanValid(ibanParticipant.getText())) {
            ibanParticipant.requestFocus();
        } else if (!isBicValid(bicParticipant.getText())) {
            bicParticipant.requestFocus();
        } else {
            ok(actionEvent);
        }
    }
}