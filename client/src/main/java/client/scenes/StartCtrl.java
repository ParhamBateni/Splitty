package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

public class StartCtrl extends PageController implements Initializable {

    public static class HBoxCell extends HBox {
        Label event = new Label();
        Region space1 = new Region();
        Region space2 = new Region();
        Button button = new Button();
        Button deleteButton = new Button();

        HBoxCell(String eventName) {
            super();

            event.setText(eventName);
            button.setText("Connect");
            deleteButton.setText("X");
            event.getStyleClass().add("event-item");

//            button.getStyleClass().add("connect-button");

            deleteButton.getStyleClass().add("delete-recent-event-button");

            Tooltip connectButtonTooltip = new Tooltip("Click to connect to the event");
            Tooltip.install(button, connectButtonTooltip);


            HBox buttonBox = new HBox(button, deleteButton);
            buttonBox.setSpacing(5);
            buttonBox.setPadding(new Insets(5));
            HBox.setHgrow(space1, Priority.ALWAYS);
            space2.setMinWidth(5);

            this.getChildren().addAll(event, space1, buttonBox, space2);
        }
    }

    public String createEventError1Message = "Event name can't be empty!";

    public String createEventError2Message = "Event name can't have more than" +
            String.format(" %d characters!", 25);
    public String joinEventErrorMessage = "Event link is not valid!";


    @FXML
    private AnchorPane mainPane;
    @FXML
    public VBox recentlyViewedEvents;
    @FXML
    private Text createEventTitle;
    @FXML
    private Text joinEventTitle;
    @FXML
    public TextField createEventField;
    @FXML
    public TextField joinEventField;
    @FXML
    public Text joinEventErrorText;
    @FXML
    public Text createEventErrorText;
    @FXML
    public Button createButton;
    @FXML
    private Button joinButton;
    @FXML
    public Text serverRefreshError;
    @FXML
    private Button languageButton;

    @FXML
    private Tooltip insertEventTooltip;
    @FXML
    private Tooltip createEventTooltip;
    @FXML
    private Tooltip insertInviteTooltip;
    @FXML
    private Tooltip joinEventTooltip;

    @Inject
    public StartCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /***
     * Refresh the page and reload the content
     *
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        showRecentlyViewedEvents();
    }

    @Override
    public void refreshLanguage() {
        // refresh flag icon here
        languageButton.setGraphic(
                mainCtrl.getPreferedLanguage()
                        .getImageView(
                                mainCtrl.getPreferedLanguage()
                                        .getCurrentLanguage()));


        createButton.setText(mainCtrl.preferedLanguage.getText("create"));
        createButton.setPrefWidth(Button.USE_COMPUTED_SIZE);
        joinButton.setText(mainCtrl.preferedLanguage.getText("join"));
        joinButton.setPrefWidth(Button.USE_COMPUTED_SIZE);

        createEventField.setPromptText(mainCtrl.preferedLanguage.getText("createEvent"));
        joinEventField.setPromptText(mainCtrl.preferedLanguage.getText("joinEvent"));

        createEventError1Message = mainCtrl.preferedLanguage.getText("createEventError1");
        createEventError2Message = String.format(mainCtrl.preferedLanguage.
                        getText("createEventError2"),
                25);
        if (createEventField.getText().length() > 25) {
            createEventErrorText.setText(createEventError2Message);
        } else if (createEventField.getText().isEmpty() &&
                !createEventErrorText.getText().isEmpty()) {
            createEventErrorText.setText(createEventError1Message);
        }
        joinEventErrorMessage = mainCtrl.preferedLanguage.getText("joinEventError");
        if (!joinEventErrorText.getText().isEmpty()) {
            joinEventErrorText.setText(joinEventErrorMessage);
        }

        createEventTitle.setText(mainCtrl.preferedLanguage.getText("createEventTitle"));
        joinEventTitle.setText(mainCtrl.preferedLanguage.getText("joinEventTitle"));
        ((Text) recentlyViewedEvents.getChildren().getFirst())
                .setText(mainCtrl.preferedLanguage.getText("recentlyViewedEvents"));

        for (Node cell : recentlyViewedEvents.getChildren()) {
            if (cell instanceof HBoxCell) {
                HBoxCell hhBoxCell = (HBoxCell) cell;
                hhBoxCell.button.setText(mainCtrl.preferedLanguage.getText("connect"));
            }
        }

        insertEventTooltip.setText(mainCtrl.preferedLanguage.getText("insertEventTooltip"));
        createEventTooltip.setText(mainCtrl.preferedLanguage.getText("createEventTooltip"));
        insertInviteTooltip.setText(mainCtrl.preferedLanguage.getText("insertInviteTooltip"));
        joinEventTooltip.setText(mainCtrl.preferedLanguage.getText("joinEventTooltip"));
    }

    public int getServerStatus() {
        return server.check();
    }

    public void checkServerStatus() {
        System.out.println("Pressing");
        int serverStatus = getServerStatus();
        if (serverStatus == 200) {
            mainCtrl.closePopupStage();
            mainCtrl.showStart();
        } else {
            serverRefreshError.setText("Failed to reload the server. Please try again later.");
        }
    }


    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {
        createEventField.clear();
        joinEventField.clear();
        createEventErrorText.setText("");
        joinEventErrorText.setText("");
        createEventField.requestFocus();
    }

    /***
     * Event handler for handling when keys are entered on page
     *
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE -> mainCtrl.closeProgram();
            case C -> {
//                if (keyEvent.isAltDown()) {
                createEvent(new ActionEvent());
//                }
            }
            case S -> {
//                if (keyEvent.isAltDown()) {
                showSettings(new ActionEvent());
//                }
            }
            case J -> {
//                if (keyEvent.isAltDown()) {
                joinEvent(new ActionEvent());
//                }
            }
            case A -> {
//                if (keyEvent.isAltDown()) {
                login();
//                }
            }
            case I -> {
//                if (keyEvent.isAltDown()) {
                showInfo(new ActionEvent());
//                }
            }
            case L -> {
//                if (keyEvent.isAltDown()) {
                changeLanguage(new ActionEvent());
//                }
            }
            case R -> {
                if (keyEvent.isControlDown()) {
                    refreshPage();
                }
            }
            case H -> {
//                if (keyEvent.isAltDown()) {
                mainCtrl.showInfoPage();
//                }
            }
        }
    }

    /**
     * Event handler for when "create event" button is pressed
     *
     * @param actionEvent button action event
     */
    public void createEvent(ActionEvent actionEvent) {
        String eventName = createEventField.getText();
        if (eventName.isEmpty()) {
            createEventErrorText.setText(createEventError1Message);
        } else if (eventName.length() > 25) {
            createEventErrorText.setText(createEventError2Message);
        } else {
            System.out.println(String.format("Request sent to server to create event %s",
                    eventName));
            createEventErrorText.setText("");
            Event event = new Event(eventName);
            System.out.println(event);
            event.setDateCreated(new Date());

            event = server.addEvent(event);

            mainCtrl.recentEventIds.add(0, event.getId());
            mainCtrl.setCurrentEvent(event);

            System.out.println("Event id is: " + event.getId());
            System.out.println("The random invite link is: " + event.getLink());
            mainCtrl.showOverview();
        }
        createEventField.clear();
    }

    /**
     * Event handler for when "join event" button is pressed
     *
     * @param actionEvent button action event
     */
    public void joinEvent(ActionEvent actionEvent) {
        String eventLink = joinEventField.getText();
        if (eventLink.isEmpty()) {
            joinEventErrorText.setText(joinEventErrorMessage);
        } else {
            System.out.println(String.format("Request sent to server to join event %s", eventLink));
            joinEventErrorText.setText("");
            Event event = server.getEventByLink(eventLink);
            if (event != null) {
                mainCtrl.recentEventIds.add(0, event.getId());
                mainCtrl.setCurrentEvent(event);
                mainCtrl.showOverview();
            } else {
                joinEventErrorText.setText(joinEventErrorMessage);
            }
        }
        joinEventField.clear();
    }

    /**
     * Displays language selection popup
     *
     * @param actionEvent
     */
    public void changeLanguage(ActionEvent actionEvent) {
        mainCtrl.showLanguage();
    }

    public void showInfo(ActionEvent actionEvent) {
        mainCtrl.showInfoPage();
    }

    public void showSettings(ActionEvent actionEvent) {
        mainCtrl.showSettings();
    }

    /***
     * Show the recently viewed events from the local cache file and
     * check if the events still exist on the database by sending a request to
     * server
     */
    public void showRecentlyViewedEvents() {
        List<Event> events = loadEventsFromFile(mainCtrl.recentEventIds);
        System.out.println("EVENTS: " + events.toString());
        Node header = recentlyViewedEvents.getChildren().getFirst();
        recentlyViewedEvents.getChildren().clear();
        recentlyViewedEvents.getChildren().add(header);
        if (!events.isEmpty()) {
            System.out.println("Adding events to list...");
            addRecentlyViewedEvents(events);
        } else {
            Text textNode = new Text(mainCtrl.getPreferedLanguage().getText("noRecentEvents"));
            textNode.getStyleClass().add("bold-recent-events");

            recentlyViewedEvents.getChildren().add(
                    textNode);
        }
    }

    public List<Event> loadEventsFromFile(List<Long> ids) {
        List<Event> events = new ArrayList<>();
        Set<Long> eventIds = new HashSet<>();

        for (Long id : ids) {
            if (id != -1 && !eventIds.contains(id)) {
                Event event = server.getEventById(id);
                if (event != null) {
                    events.add(event);
                    eventIds.add(id);
                }
            }
        }
        return events;
    }


    /***
     * Adds the recently viewed events to the list on the start page
     * and creates buttons so users can connect to them.
     * @param events The events to be added to the list.
     */
    public void addRecentlyViewedEvents(List<Event> events) {
        for (int i = 0; i < Math.min(events.size(), 5); i++) {
            Event event = events.get(i);
            HBoxCell cell = new HBoxCell(event.getTitle());
            cell.button.setOnAction(e -> {
                mainCtrl.recentEventIds.remove(event.getId());
                mainCtrl.recentEventIds.add(0, event.getId());
                mainCtrl.setCurrentEvent(event);
                mainCtrl.showOverview();
            });
            cell.deleteButton.setOnAction(e -> {
                mainCtrl.recentEventIds.remove(event.getId());
                showRecentlyViewedEvents();
            });
            cell.button.setText(mainCtrl.preferedLanguage.getText("connect"));
            recentlyViewedEvents.getChildren().add(cell);
        }
    }

    public void login() {
        mainCtrl.showAdminLogin();
    }

}
