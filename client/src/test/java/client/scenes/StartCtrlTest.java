package client.scenes;

import client.Language;
import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Event;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxToolkit;
import org.testfx.framework.spock.ApplicationSpec;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartCtrlTest extends ApplicationSpec {

    private static StartCtrl startCtrl;

    private List<Event> events;

    @Mock
    private ServerUtils server;

    @Mock
    private MainCtrl mainCtrl;


    @BeforeEach
    public void setup() throws IOException, ClassNotFoundException, TimeoutException {
        MockitoAnnotations.openMocks(this);
        // create test data
        events = new ArrayList<>();
        events.add(new Event("Test Event 1"));
        events.getFirst().setId(0);
        events.add(new Event("Test Event 2"));
        events.getLast().setId(1);

        List<Participant> participants1 = Arrays.asList(
                new Participant("Test Participant 1", "email", "iban", "bic", events.get(0)),
                new Participant("Test Participant 2", "email", "iban", "bic", events.get(0)));

        List<Participant> participants2 = Arrays.asList(
                new Participant("Test Participant 3", "email", "iban", "bic", events.get(1)),
                new Participant("Test Participant 4", "email", "iban", "bic", events.get(1)));

        // set participants
        events.get(0).setParticipants(participants1);
        events.get(1).setParticipants(participants2);
        if (GraphicsEnvironment.isHeadless()) {
            Class.forName("com.sun.glass.ui.monocle.MonoclePlatformFactory");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        FxToolkit.registerPrimaryStage();
        Injector injector = createInjector(new MyModule());
        MyFXML myFXML = new MyFXML(injector);
        var start = myFXML.load(StartCtrl.class, "client", "scenes",
                "Start.fxml");
        var serverError = myFXML.load(StartCtrl.class, "client", "scenes", "ServerError.fxml");
        startCtrl = start.getKey();
        startCtrl.server = server;
        startCtrl.mainCtrl = mainCtrl;
        startCtrl.mainCtrl.preferedLanguage = new Language("EN");
        startCtrl.mainCtrl.recentEventIds = new ArrayList<>();
        Mockito.doReturn(startCtrl.mainCtrl.preferedLanguage)
                .when(startCtrl.mainCtrl).getPreferedLanguage();
        // save events
//        startCtrl.saveEvents(events);
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    @AfterEach
    public void clean() throws TimeoutException {
        FxToolkit.hideStage();
        File f = new File("testfile");
        if (f.exists()) {
            new File("testfile").delete();
        }
    }

//    @Test
//    public void testLoadEventsFromFile() throws IOException, ClassNotFoundException {
//        Mockito.doReturn(events.getFirst()).when(server).getEventById(0);
//        Mockito.doReturn(events.getLast()).when(server).getEventById(1);
//        List<Event> responseEvents = startCtrl.loadEventsFromFile();
//        assertEquals(2, responseEvents.size());
//        assertEquals(events.get(0), responseEvents.get(1));
//        assertEquals(events.get(1), responseEvents.get(0));
//    }

//    @Test
//    public void refreshTest() {
//        TextField mockCreateEventName = mock(TextField.class);
//        TextField mockJoinEventLink = mock(TextField.class);
//        Language mockLanguage = mock(Language.class);
//        final boolean[] focused = {false};
//        Mockito.doAnswer(invocation -> {
//            focused[0] = true;
//            return null;
//        }).when(mockCreateEventName).requestFocus();
//        Mockito.doNothing().when(mockCreateEventName).setText(Mockito.anyString());
//        Mockito.doNothing().when(mockJoinEventLink).setText(Mockito.anyString());
//        Mockito.doReturn("").when(mockLanguage).getText(Mockito.anyString());
//        startCtrl.createEventField = mockCreateEventName;
//        startCtrl.joinEventField = mockJoinEventLink;
//        mainCtrl.preferedLanguage = mockLanguage;
//        startCtrl.refreshContent();
//        assertTrue(focused[0]);
//    }

    @Test
    public void checkServerStatusTest() {
        Mockito.doReturn(500).when(server).check();
        startCtrl.checkServerStatus();
        assertEquals("Failed to reload the server. " +
                "Please try again later.", startCtrl.serverRefreshError.getText());
    }

    @Test
    public void checkServerStatusOkTest() {
        Mockito.doReturn(200).when(server).check();
        startCtrl.checkServerStatus();
        assertEquals("", startCtrl.serverRefreshError.getText());
    }

    //TODO Enable this test after language is implemented
    @Disabled
    @Test
    public void refreshLanguageTest() {
        startCtrl.refreshLanguage();
        assertEquals(mainCtrl.preferedLanguage.getText("Create"), startCtrl.createButton.getText());
    }

    @Test
    public void clearFieldsTest() {
        startCtrl.createEventField.setText("asdfdsfdsf");
        startCtrl.joinEventField.setText("werwerwerw");
        startCtrl.clearFields();
        assertEquals("", startCtrl.createEventField.getText());
        assertEquals("", startCtrl.joinEventField.getText());
    }

    @Test
    public void keyPressedTest() {
        Mockito.doNothing().when(mainCtrl).closeProgram();
        KeyEvent escKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false,
                false, false, false);
        startCtrl.keyPressed(escKeyEvent);
        Mockito.verify(mainCtrl, Mockito.times(1)).closeProgram();
    }


    @Test
    public void createEventTest() {
        startCtrl.createEventField.setText("");
        startCtrl.createEvent(new ActionEvent());
        assertEquals("Event name can't be empty!", startCtrl.createEventErrorText.getText());

        startCtrl.createEventField.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        startCtrl.createEvent(new ActionEvent());
        assertEquals("Event name can't have more than" +
                String.format(" %d characters!", 25), startCtrl.createEventErrorText.getText());

        startCtrl.createEventField.setText(events.getFirst().getTitle());
        Mockito.doReturn(events.getFirst()).when(server).addEvent(Mockito.any(Event.class));
        Mockito.doNothing().when(mainCtrl).setCurrentEvent(Mockito.any(Event.class));
        Mockito.doNothing().when(mainCtrl).showOverview();
        startCtrl.createEvent(new ActionEvent());
        assertEquals("", startCtrl.createEventErrorText.getText());
        assertEquals("", startCtrl.createEventField.getText());
        Mockito.verify(server, Mockito.times(1)).addEvent(Mockito.any(Event.class));
        Mockito.verify(mainCtrl, Mockito.times(1)).setCurrentEvent(Mockito.any(Event.class));
        Mockito.verify(mainCtrl, Mockito.times(1)).showOverview();
    }


    @Test
    public void joinEventTest() {
        startCtrl.joinEventField.setText("");
        startCtrl.joinEvent(new ActionEvent());
        assertEquals("Event link is not valid!", startCtrl.joinEventErrorText.getText());
        startCtrl.joinEventField.setText("AAAAAA");
        Mockito.doReturn(events.getFirst()).when(server).getEventByLink("AAAAAA");
        Mockito.doReturn(null).when(server).getEventByLink("BBBBBB");
        Mockito.doNothing().when(mainCtrl).setCurrentEvent(Mockito.any(Event.class));
        Mockito.doNothing().when(mainCtrl).showOverview();
        startCtrl.joinEvent(new ActionEvent());
        Mockito.verify(mainCtrl, Mockito.times(1)).setCurrentEvent(Mockito.any(Event.class));
        Mockito.verify(mainCtrl, Mockito.times(1)).showOverview();
        assertEquals("", startCtrl.joinEventField.getText());
        startCtrl.joinEventField.setText("BBBBBB");
        startCtrl.joinEvent(new ActionEvent());
        assertEquals("Event link is not valid!", startCtrl.joinEventErrorText.getText());
    }

    @Test
    public void changeLanguageTest() {
        Mockito.doNothing().when(mainCtrl).showLanguage();
        startCtrl.changeLanguage(new ActionEvent());
        Mockito.verify(mainCtrl, Mockito.times(1)).showLanguage();
    }


    @Test
    public void showRecentlyViewedEventsTest() {
        startCtrl.showRecentlyViewedEvents();
        assertTrue(startCtrl.recentlyViewedEvents.getChildren().size() > 1 ||
                startCtrl.recentlyViewedEvents.getChildren().getFirst() instanceof HBox ||
                ((Text) startCtrl.recentlyViewedEvents.getChildren()
                        .getFirst()).getText().equals("There are no previous events available!"));

    }


    @Test
    public void loginTest() {
        Mockito.doNothing().when(mainCtrl).showAdminLogin();
        startCtrl.login();
        Mockito.verify(mainCtrl, Mockito.times(1)).showAdminLogin();
    }


    @Test
    public void addRecentlyViewedEventsTest() {
        startCtrl.addRecentlyViewedEvents(events);
        assertEquals("Recently viewed events:",
                ((Text) startCtrl.recentlyViewedEvents
                        .getChildren().get(0)).getText());

        for (int i = 0; i < events.size(); i++) {
            assertEquals(events.get(i).getTitle(),
                    ((StartCtrl.HBoxCell) startCtrl.recentlyViewedEvents
                            .getChildren().get(i + 1)).event.getText());
        }
        Mockito.doNothing().when(mainCtrl).setCurrentEvent(Mockito.any(Event.class));
        Mockito.doNothing().when(mainCtrl).showOverview();
        ((StartCtrl.HBoxCell) startCtrl.recentlyViewedEvents.getChildren()
                .get(1)).button.fireEvent(new ActionEvent());
        Mockito.verify(mainCtrl, Mockito.times(1)).setCurrentEvent(Mockito.any(Event.class));
        Mockito.verify(mainCtrl, Mockito.times(1)).showOverview();
        int initialSize = events.size() + 1;
        ((StartCtrl.HBoxCell) startCtrl.recentlyViewedEvents.getChildren()
                .get(1)).deleteButton.fireEvent(new ActionEvent());
        assertEquals(initialSize - 1, startCtrl.recentlyViewedEvents.getChildren().size());
    }

}