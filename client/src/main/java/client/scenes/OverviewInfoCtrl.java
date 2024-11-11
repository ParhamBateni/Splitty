package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class OverviewInfoCtrl extends PageController {

    @FXML
    private Button okButton;

    @FXML
    private Text overviewPageText;

    @FXML
    private Text settleDebtShortcutText;
    @FXML
    private Text editParticipantShortcutText;
    @FXML
    private Text inviteParticipantShortcutText;
    @FXML
    private Text addParticipantShortcutText;
    @FXML
    private Text summaryShortcutText;
    @FXML
    private Text addExpenseShortcutText;
    @FXML
    private Text doubleClickShortcutText;
    @FXML
    private Text participantPageText;
    @FXML
    private Text cheatShortcutText;
    @FXML
    private Text removeParticipantShortcutText;
    @FXML
    private Text expensePageText;
    @FXML
    private Text removeExpenseShortcutText;
    @FXML
    private Button okButtion;

    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public OverviewInfoCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }


    /***
     * read the list of emails and send an invite to the event to the emails
     * @param actionEvent
     */


    /***
     * Refresh the page and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        refreshLanguage();
    }

    @Override
    public void refreshLanguage() {
        okButton.setText(mainCtrl.getPreferedLanguage().getText("done"));

        overviewPageText.setText(mainCtrl.getPreferedLanguage().getText("overviewPageText"));
        settleDebtShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("settleDebtShortcutText"));
        editParticipantShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("editParticipantShortcutText"));
        inviteParticipantShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("inviteParticipantShortcutText"));
        addParticipantShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("addParticipantShortcutText"));
        summaryShortcutText.setText(mainCtrl.getPreferedLanguage().getText("summaryShortcutText"));
        addExpenseShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("addExpenseShortcutText"));
        doubleClickShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("doubleClickShortcutText"));
        participantPageText.setText(mainCtrl.getPreferedLanguage().getText("participantPageText"));
        cheatShortcutText.setText(mainCtrl.getPreferedLanguage().getText("cheatShortcutText"));
        removeParticipantShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("removeParticipantShortcutText"));
        expensePageText.setText(mainCtrl.getPreferedLanguage().getText("expensePageText"));
        removeExpenseShortcutText.setText(mainCtrl.getPreferedLanguage()
            .getText("removeExpenseShortcutText"));


    }


    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {

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
            case ENTER:
                break;
        }
    }
}
