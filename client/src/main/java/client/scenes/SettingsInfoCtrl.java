package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class SettingsInfoCtrl extends PageController {
    @FXML
    public Text settingInfoTitle;
    @FXML
    public Text settingPageInfoText;

    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SettingsInfoCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /***
     * Refresh the page content and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {

    }

    @Override
    public void refreshLanguage() {
        settingInfoTitle.setText(mainCtrl.getPreferedLanguage().getText("settingsInfo"));
        settingPageInfoText.setText(mainCtrl.getPreferedLanguage().getText("settingsInfoText"));
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
        }
    }
}
