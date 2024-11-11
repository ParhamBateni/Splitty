package client.scenes;

import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCtrl extends PageController implements Initializable {
    @FXML
    public TableColumn<Event, Long> eventId;
    @FXML
    public TableColumn<Event, String> eventName;
    @FXML
    public TableColumn<Event, Date> eventCreationDate;
    @FXML
    public TableColumn<Event, Date> eventLastAction;
    @FXML
    public TableView<Event> eventsTable;
    @FXML
    public Text debugText;
    @FXML
    public Text titleText;
    @FXML
    public Button showOverviewButton;
    @FXML
    public Button loadEventButton;
    @FXML
    public Button closeButton;
    @FXML
    public Button saveEventButton;
    @FXML
    public Button deleteEventButton;
    @FXML
    public Tooltip closeButtonTooltip;
    @FXML
    public Tooltip saveEventButtonTooltip;
    @FXML
    public Tooltip loadEventButtonTooltip;
    @FXML
    public Tooltip deleteEventButtonTooltip;
    @FXML
    public Tooltip showOverviewButtonTooltip;
    @FXML
    public FileChooser fileChooser;
    private List<Event> events;
    private final String fileDir = System.getProperty("user.home");

    public Boolean isInitializedInstance() {
        return initializedInstance;
    }

    private Boolean initializedInstance;

    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public AdminCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }


    public void initialize(URL location, ResourceBundle resources) {
        initializedInstance = false;
    }

    public void registerListener() {
        if (!initializedInstance) {
            server.registerForMessages("/events/collector",
                Event.class, e -> {
                    eventsTable.getItems().add(e);
                });
            server.registerForMessages("/events/modifier",
                Event.class, e -> {
                    var p = eventsTable.getSortPolicy();
                    ObservableList<Event> events = eventsTable.getItems();
                    for (int i = 0; i < events.size(); i++) {
                        if (events.get(i).getId() == e.getId()) {
                            events.set(i, e);
                            eventsTable.setItems(events);
                        }
                    }
                    eventsTable.setSortPolicy(p);
                    eventsTable.sort();
                });
            server.registerForMessages("/events/terminator",
                Event.class, e -> {
                    var p = eventsTable.getSortPolicy();
                    ObservableList<Event> events = eventsTable.getItems();
                    for (int i = 0; i < events.size(); i++) {
                        if (events.get(i).getId() == e.getId()) {
                            events.remove(events.get(i));
                            eventsTable.setItems(events);
                            break;
                        }
                    }
                    eventsTable.setSortPolicy(p);
                    eventsTable.sort();

                });
            initializedInstance = true;
        }
    }

    public void refreshContent(Object... args) {
        registerListener();
        showOverviewButton.setDisable(true);
        deleteEventButton.setDisable(true);
        saveEventButton.setDisable(true);
        debugText.setText("");



        events = server.getEvents();
        eventId.setCellValueFactory(new PropertyValueFactory<Event, Long>("id"));
        eventName.setCellValueFactory(new PropertyValueFactory<Event, String>("title"));

        eventCreationDate.setCellValueFactory(new PropertyValueFactory<Event, Date>("dateCreated"));
        eventLastAction.setCellValueFactory(new PropertyValueFactory<Event, Date>("lastAction"));

        ObservableList<Event> items = eventsTable.getItems();
        items.clear();

        if (events != null) {
            for (Event event : events) {
                eventsTable.getItems().add(event);
            }
            if (events.isEmpty()) {
                Platform.runLater(() -> eventsTable.setPlaceholder(
                        new Text(mainCtrl.getPreferedLanguage().getText("noEvents"))));
            }
        }

    }

    @Override
    public void refreshLanguage() {
        titleText.setText(mainCtrl.getPreferedLanguage().getText("eventsAdmin"));
        showOverviewButton.setText(mainCtrl.getPreferedLanguage().getText("showOverview"));
        loadEventButton.setText(mainCtrl.getPreferedLanguage().getText("loadEvent"));
        closeButton.setText(mainCtrl.getPreferedLanguage().getText("close"));
        saveEventButton.setText(mainCtrl.getPreferedLanguage().getText("saveEvent"));
        deleteEventButton.setText(mainCtrl.getPreferedLanguage().getText("deleteEvent"));
        eventCreationDate.setText(mainCtrl.getPreferedLanguage().getText("eventCreateDate"));
        eventLastAction.setText(mainCtrl.getPreferedLanguage().getText("eventLastAction"));
        eventName.setText(mainCtrl.getPreferedLanguage().getText("eventNameField"));

        closeButtonTooltip.setText(mainCtrl.getPreferedLanguage().getText("returnToLoginPage"));
        saveEventButtonTooltip.setText(mainCtrl.getPreferedLanguage().getText("saveSelectedEvent"));
        loadEventButtonTooltip.setText(mainCtrl.getPreferedLanguage().getText("loadLocalEvent"));
        deleteEventButtonTooltip.setText(mainCtrl.getPreferedLanguage()
            .getText("deleteSelectedEvent"));
        showOverviewButtonTooltip.setText(mainCtrl.getPreferedLanguage()
            .getText("showEventOverview"));
    }

    public void eventSelected() {
        if (eventsTable.getSelectionModel().getSelectedItem() == null) {
            showOverviewButton.setDisable(true);
            deleteEventButton.setDisable(true);
            saveEventButton.setDisable(true);
        } else {
            showOverviewButton.setDisable(false);
            deleteEventButton.setDisable(false);
            saveEventButton.setDisable(false);
        }
    }

    public void deleteEvent() {
        Event event = eventsTable.getSelectionModel().getSelectedItem();
        if (mainCtrl.showVerificationPopup(
            mainCtrl.getPreferedLanguage().getText("verifyEventDelete")
                + " " + event.getTitle())) {
            try {
                server.send("/event/terminator", event.getId());
                debugText.setText(mainCtrl.getPreferedLanguage().getText("eventDeleteSuccess"));
            } catch (Exception e) {
                debugText.setText(mainCtrl.getPreferedLanguage().getText("eventSaveFail"));
            } finally {
                refreshPage();
            }
        }
    }

    public void adminShortcuts() {
        mainCtrl.showAdminShortcuts();
    }

    public void saveEvent() {
        Event event = eventsTable.getSelectionModel().getSelectedItem();

        server.updateEvent(event);
        // download logic
        fileChooser = new FileChooser();


        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter =
            new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        try {
            //Show save file dialog
            File file = fileChooser.showSaveDialog(mainCtrl.primaryStage);
            ObjectMapper objectMapper = new ObjectMapper();

            if (file != null) {
                try {
                    saveTextToFile(
                        objectMapper.writeValueAsString(event), file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {

        }

        //return to start
        mainCtrl.closePopupStageStart();
    }

    private void saveTextToFile(String content, File file) throws IOException {

        PrintWriter writer = new PrintWriter(file);
        writer.println(content);
        writer.close();

    }

    public void loadEvent() {
        mainCtrl.showLoadEvent();
        refreshPage();
    }

    public void eventOverview() {
        Event event = eventsTable.getSelectionModel().getSelectedItem();
        mainCtrl.currentEvent = event;
        mainCtrl.showOverview();
    }

    @Override
    public void clearFields() {
        eventsTable.getItems().clear();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE -> mainCtrl.showAdminLogin();
            case O -> eventOverview();
            case S -> saveEvent();
            case L -> loadEvent();
            case D -> deleteEvent();
            case C -> adminShortcuts();
        }
    }


    public void clear() {
        if (mainCtrl.showVerificationPopup(
            mainCtrl.getPreferedLanguage().getText("confirmClose"))) {
            mainCtrl.showStart();
        }

    }


}
