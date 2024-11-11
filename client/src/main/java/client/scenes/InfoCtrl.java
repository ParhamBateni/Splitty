package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class InfoCtrl extends PageController {

    @FXML
    private Button okButton;

    @FXML
    private Text inviteTitle;

    @FXML
    private Text offerCodeText;
    @FXML
    private Text inviteTitle1;
    @FXML
    private Text offerCodeText1;
    @FXML
    private Text offerCodeText11;
    @FXML
    private Text offerCodeText111;
    @FXML
    private Text offerCodeText112;
    @FXML
    private Text offerCodeText121;
    @FXML
    private Text offerCodeText1211;



    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public InfoCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
        inviteTitle.setText(mainCtrl.getPreferedLanguage().getText("infoTitle"));
        okButton.setText(mainCtrl.getPreferedLanguage().getText("done"));
        offerCodeText.setText(mainCtrl.getPreferedLanguage().getText("infoText"));
        inviteTitle1.setText(mainCtrl.getPreferedLanguage().getText("infoTitle1"));
        offerCodeText1.setText(mainCtrl.getPreferedLanguage().getText("infoText1"));
        offerCodeText11.setText(mainCtrl.getPreferedLanguage().getText("infoText11"));
        offerCodeText111.setText(mainCtrl.getPreferedLanguage().getText("infoText111"));
        offerCodeText112.setText(mainCtrl.getPreferedLanguage().getText("infoText112"));
        offerCodeText121.setText(mainCtrl.getPreferedLanguage().getText("infoText121"));
        offerCodeText1211.setText(mainCtrl.getPreferedLanguage().getText("infoText1211"));
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
