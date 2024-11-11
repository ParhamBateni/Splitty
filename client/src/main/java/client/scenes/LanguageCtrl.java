package client.scenes;

import client.Language;
import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;


public class LanguageCtrl extends PageController {

    @FXML
    private Button english;
    @FXML
    private Button german;
    @FXML
    private Button dutch;
    @FXML
    private Button download;
    @FXML
    private Label englishText;
    @FXML
    private Label germanText;
    @FXML
    private Label dutchText;
    @FXML
    private Label downloadText;
    @FXML
    private Label languageSelection;

    /**
     * Constructor with injector
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public LanguageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    public void templateDownload(ActionEvent actionEvent) throws URISyntaxException {
        // download logic
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter =
            new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(mainCtrl.primaryStage);
        ObjectMapper objectMapper = new ObjectMapper();
        URL res = getClass()
            .getClassLoader()
            .getResource("client/languages/EN.json");
        File fileJson = Paths.get(res.toURI()).toFile();

        if (file != null) {
            try {
                Map<String, String> map = objectMapper.readValue(
                        fileJson, Map.class);
                for (String a : map.keySet()) {
                    map.put(a, "edit here");
                }
                saveTextToFile(
                        objectMapper.writeValueAsString(map), file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //return to start
        mainCtrl.closePopupStage();
    }

    private void saveTextToFile(String content, File file) throws IOException {

        PrintWriter writer = new PrintWriter(file);
        writer.println(content);
        writer.close();

    }

    public void changeToEnglish(ActionEvent actionEvent) {
        try {
            mainCtrl.setPreferredLanguage(new Language("EN"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return to start
        mainCtrl.closePopupStage();
    }

    public void changeToDutch(ActionEvent actionEvent) {
        try {
            mainCtrl.setPreferredLanguage(new Language("NL"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return to start
        mainCtrl.closePopupStage();
    }

    public void changeToGerman(ActionEvent actionEvent) {
        try {
            mainCtrl.setPreferredLanguage(new Language("DE"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return to start
        mainCtrl.closePopupStage();

    }


    @Override
    public void refreshContent(Object... args) {
        refreshLanguage();
        switch (mainCtrl.getPreferedLanguage().getCurrentLanguage()) {
            case "EN" -> {
                english.requestFocus();
            }
            case "NL" -> {
                dutch.requestFocus();
            }
            case "DE" -> {
                german.requestFocus();
            }
        }
    }

    public void refreshLanguage() {
        englishText.setText(mainCtrl.getPreferedLanguage().getText("englishOption"));
        germanText.setText(mainCtrl.getPreferedLanguage().getText("germanOption"));
        dutchText.setText(mainCtrl.getPreferedLanguage().getText("dutchOption"));
        downloadText.setText(mainCtrl.getPreferedLanguage().getText("downloadOption"));
        languageSelection.setText(mainCtrl.getPreferedLanguage().getText("languageSelection"));


    }

    @Override
    public void clearFields() {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                mainCtrl.closePopupStage();
        }
    }
}

