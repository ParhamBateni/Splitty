package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import javax.inject.Inject;


public class AdminLoginCtrl extends PageController {

    @FXML
    public Text wrongPassword;
    @FXML
    public PasswordField adminPassword;
    @FXML
    public Text adminLoginTitle;
    @FXML
    public Text password;
    @FXML
    public Button cancelButton;
    @FXML
    public Button confirmButton;
    @FXML
    private Tooltip confirmtooltip;
    @FXML
    private Tooltip canceltooltip;


    @Inject
    public AdminLoginCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /***
     * Refresh the page and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
    }

    @Override
    public void refreshLanguage() {
        adminLoginTitle.setText(mainCtrl.getPreferedLanguage().getText("adminLoginTitle"));
        password.setText(mainCtrl.getPreferedLanguage().getText("password"));
        cancelButton.setText(mainCtrl.getPreferedLanguage().getText("cancel"));
        confirmButton.setText(mainCtrl.getPreferedLanguage().getText("confirm"));
        adminPassword.setPromptText(mainCtrl.getPreferedLanguage().getText("enterPassword"));

        confirmtooltip.setText(mainCtrl.getPreferedLanguage().getText("confirmTooltip"));
        canceltooltip.setText(mainCtrl.getPreferedLanguage().getText("cancelTooltip"));
    }


    public void confirmLogin() {
        if (server.checkAdminPassword(adminPassword.getText())) {
            mainCtrl.showAdminPage();
        } else {
            wrongPassword.setText(mainCtrl.getPreferedLanguage().getText("passwordEnterFail"));
        }
    }

    public void cancelLogin() {
        mainCtrl.showStart();
    }

    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {
        adminPassword.setText("");
        wrongPassword.setText("");
    }

    /***
     * Event handler for handling when keys are entered on page
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE -> cancelLogin();
            case ENTER -> confirmLogin();

        }
    }

}
