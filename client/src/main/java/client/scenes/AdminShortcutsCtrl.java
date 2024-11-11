package client.scenes;

import client.utils.ServerUtils;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;


import javax.inject.Inject;


public class AdminShortcutsCtrl extends PageController {

    @FXML
    public Button cancelButton;
    @FXML
    public Label shortcutsTitle;
    @FXML
    public Label shortcutD;
    @FXML
    public Label shortcutO;
    @FXML
    public Label shortcutS;
    @FXML
    public Label shortcutL;
    @FXML
    public Label shortcutC;


    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public AdminShortcutsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }
    public void close() {
        mainCtrl.closePopupStageAdmin();
    }
    @Override
    public void refreshContent(Object... args) {
    }
    @Override
    public void refreshLanguage() {
        shortcutsTitle.setText(mainCtrl.getPreferedLanguage().getText("shortcutsTitle"));
        shortcutO.setText(mainCtrl.getPreferedLanguage().getText("shortcutO"));
        shortcutS.setText(mainCtrl.getPreferedLanguage().getText("shortcutS"));
        shortcutD.setText(mainCtrl.getPreferedLanguage().getText("shortcutD"));
        shortcutL.setText(mainCtrl.getPreferedLanguage().getText("shortcutL"));
        shortcutC.setText(mainCtrl.getPreferedLanguage().getText("shortcutC"));
        cancelButton.setText(mainCtrl.getPreferedLanguage().getText("cancel"));

    }

    @Override
    public void clearFields() {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            mainCtrl.closePopupStageAdmin();
        }
    }
}
