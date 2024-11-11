package client.scenes;

import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;

public class LoadEventCtrl extends PageController {
    @FXML
    public Text errorText;
    @FXML
    public Text loadEventTitle;
    @FXML
    public Button cancelButton;
    @FXML
    public Button chooseLoadButton;
    @FXML
    public Tooltip loadButtonTooltip;
    @FXML
    public Tooltip cancelLoadButtonTooltip;


    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public LoadEventCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    public void loadEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(mainCtrl.primaryStage);
        if (file == null) {
            errorText.setText(mainCtrl.getPreferedLanguage().getText("invalidFile"));
        }
        else {
            ObjectMapper mapper = new ObjectMapper();

            try {
                if (mainCtrl.showVerificationPopup(
                        mainCtrl.getPreferedLanguage().getText("verifyEventLoad"))) {
                    Event event = mapper.readValue(file, Event.class);

                    try {
                        event = server.addEvent(event);
                        errorText.setText(
                                mainCtrl.getPreferedLanguage().getText("loadEventSuccess"));
                        errorText.setFill(Color.GREEN);
                    } catch (Exception e) {
                        errorText.setText(mainCtrl.getPreferedLanguage().getText("loadEventFail"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel() {
        mainCtrl.closePopupStageAdmin();
    }

    @Override
    public void refreshContent(Object... args) {
        refreshLanguage();
    }

    @Override
    public void refreshLanguage() {
        errorText.setText("");
        loadEventTitle.setText(mainCtrl.getPreferedLanguage().getText("loadEventTitle"));
        cancelButton.setText(mainCtrl.getPreferedLanguage().getText("cancel"));
        chooseLoadButton.setText(mainCtrl.getPreferedLanguage().getText("chooseLoadFile"));

        loadButtonTooltip.setText(mainCtrl.getPreferedLanguage().getText("loadButtonTooltip"));
        cancelLoadButtonTooltip.setText(mainCtrl.getPreferedLanguage()
            .getText("cancelLoadButtonTooltip"));

    }

    @Override
    public void clearFields() {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER -> loadEvent();
            case ESCAPE -> cancel();
        }
    }
}
