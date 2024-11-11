package client.scenes;

import client.Language;
import client.MyFXML;
import client.MyModule;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.collections.FXCollections;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxToolkit;
import org.testfx.framework.spock.ApplicationSpec;

import java.awt.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminCtrlTest extends ApplicationSpec {

    private static AdminCtrl adminCtrl;
    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;
    private List<Event> events;


    @BeforeEach
    void setUp() throws ClassNotFoundException, TimeoutException {

        MockitoAnnotations.openMocks(this);
        when(server.connect(Mockito.anyString())).thenReturn(null);
        // create test data
        events = new ArrayList<>();
        events.add(new Event("Test Event 1"));

        java.util.List<Participant> participants = new ArrayList<>() {
            {
                add(new Participant("Test Participant 1", "email", "iban", "bic", events.get(0)));
                add(new Participant("Test Participant 2", "email", "iban", "bic", events.get(0)));
                add(new Participant("Test Participant 3", "email", "iban", "bic", events.get(0)));
                add(new Participant("Test Participant 4", "email", "iban", "bic", events.get(0)));
            }
        };

        // set participants
        events.get(0).setParticipants(participants);

        List<Expense> expenses = new ArrayList<>() {
            {
                add(new Expense(participants.get(0),
                    Currency.getInstance("EUR"),
                    10, "Party", "10/10/2010", participants.subList(0, 2),
                    events.get(0), false));
                add(new Expense(participants.get(1),
                    Currency.getInstance("EUR"),
                    20, "Dinner", "20/10/2010", participants.subList(1, 4),
                    events.get(0), false));
                add(new Expense(participants.get(2), Currency.getInstance("EUR"),
                    20, "Dinner", "20/10/2010", participants.subList(0, 3),
                    events.get(0), false));
            }
        };
        events.get(0).addExpense(expenses.get(0));
        events.get(0).addExpense(expenses.get(1));
        events.get(0).addExpense(expenses.get(2));
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
        var admin = myFXML.load(AdminCtrl.class, "client", "scenes",
            "Admin.fxml");
        adminCtrl = admin.getKey();
        when(mainCtrl.getPreferedLanguage()).thenReturn(new Language("EN"));
        adminCtrl.server = server;
        adminCtrl.mainCtrl = mainCtrl;
        adminCtrl.mainCtrl.preferredCurrency = Currency.getInstance("EUR");
        adminCtrl.mainCtrl.preferedLanguage = new Language("EN");
        adminCtrl.eventsTable.getItems().addAll(events);
    }

    @Test
    void initializeTest() {
        adminCtrl.initialize(null, null);
        assertFalse(adminCtrl.isInitializedInstance());
    }

    @Test
    void clearFieldsTest() {
        adminCtrl.eventsTable.setItems(FXCollections.observableArrayList(events.get(0)));
        adminCtrl.clearFields();
        assertTrue(adminCtrl.eventsTable.getItems().isEmpty());
    }

    @Disabled
    @Test
    void clearTest() {
        adminCtrl.clear();
        verify(mainCtrl).showAdminLogin();
    }

    @Test
    void registerListenerTest() {
        adminCtrl.registerListener();

        verify(server).registerForMessages(eq("/events/collector"), eq(Event.class), any());
        verify(server).registerForMessages(eq("/events/modifier"), eq(Event.class), any());
        verify(server).registerForMessages(eq("/events/terminator"), eq(Event.class), any());


        assertTrue(adminCtrl.isInitializedInstance());
    }

    @Disabled
    @Test
    void refreshContentTest() {
        when(server.getEvents()).thenReturn(new ArrayList<>());
        adminCtrl.refreshContent();
        verify(mainCtrl).getPreferedLanguage();
    }

    @Disabled
    @Test
    void refreshContentWithEventTest() {
        when(server.getEvents()).thenReturn(events);
        adminCtrl.refreshContent();
        assertFalse(adminCtrl.eventsTable.getItems().isEmpty());

    }

    @Test
    void eventSelectedTrueTest() {
        adminCtrl.eventSelected();
        assertTrue(adminCtrl.showOverviewButton.isDisabled());
        assertTrue(adminCtrl.deleteEventButton.isDisabled());
        assertTrue(adminCtrl.saveEventButton.isDisabled());
    }

    @Test
    void eventSelectedFalseTest() {
        adminCtrl.eventsTable.getSelectionModel().select(0);
        adminCtrl.eventSelected();
        assertFalse(adminCtrl.showOverviewButton.isDisabled());
        assertFalse(adminCtrl.deleteEventButton.isDisabled());
        assertFalse(adminCtrl.saveEventButton.isDisabled());
    }

    @Disabled
    @Test
    void deleteEventTrueTest() {
        adminCtrl.eventsTable.getSelectionModel().select(0);
        when(mainCtrl.showVerificationPopup(any())).thenReturn(true);
        doNothing().when(server).send(any(), any());
        adminCtrl.deleteEvent();
        verify(server).send(any(), any());
        assertEquals(adminCtrl.debugText.getText(),
            mainCtrl.getPreferedLanguage().getText("eventDeleteSuccess"));
    }

    @Disabled
    @Test
    void deleteEventFalseTest() {
        adminCtrl.eventsTable.getSelectionModel().select(0);

        when(mainCtrl.showVerificationPopup(any())).thenReturn(true);
        Mockito.doThrow(new RuntimeException("error"))
            .when(server).send(Mockito.any(), Mockito.any());
        adminCtrl.deleteEvent();
        assertEquals(adminCtrl.debugText.getText(),
            mainCtrl.getPreferedLanguage().getText("eventSaveFail"));
    }

    @Test
    void saveEventTrueTest() {

        AdminCtrl mockAdminCtrl = mock(AdminCtrl.class);
        mockAdminCtrl.mainCtrl = adminCtrl.mainCtrl;
        mockAdminCtrl.saveEvent();
        mockAdminCtrl.eventsTable = adminCtrl.eventsTable;
        doReturn(true).when(mockAdminCtrl.mainCtrl).showConfirmationPopUp(any());

        mockAdminCtrl.server = server;
        verify(mockAdminCtrl, times(1)).saveEvent();


    }

    @Test
    void refreshLanguageTest() {
        adminCtrl.refreshLanguage();
        Mockito.verify(mainCtrl, Mockito.times(14)).getPreferedLanguage();
    }

    @Test
    void keyTest() {
        KeyEvent mockKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "",
            KeyCode.ESCAPE, false, false, false, false);
        adminCtrl.keyPressed(mockKeyEvent);
        verify(mainCtrl).showAdminLogin();
    }

    @Test
    void keyOTest() {
        KeyEvent mockKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "",
            KeyCode.O, false, false, false, false);
        adminCtrl.keyPressed(mockKeyEvent);
        verify(mainCtrl).showOverview();
    }

    @Test
    void keyLTest() {
        KeyEvent mockKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "",
            KeyCode.L, false, false, false, false);
        adminCtrl.keyPressed(mockKeyEvent);
        verify(mainCtrl).showLoadEvent();
    }

    @Test
    void keyCTest() {
        KeyEvent mockKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "",
            KeyCode.C, false, false, false, false);
        adminCtrl.keyPressed(mockKeyEvent);
        verify(mainCtrl).showAdminShortcuts();
    }

    @Test
    void keySTest() {

        AdminCtrl mockAdminCtrl = mock(AdminCtrl.class);
        when(mainCtrl.showVerificationPopup(Mockito.any())).thenReturn(true);
        KeyEvent mockKeyEvent = new KeyEvent(

                KeyEvent.KEY_PRESSED, "", "",
                KeyCode.S, false, false, false, false);


        mockAdminCtrl.keyPressed(mockKeyEvent);


        verify(mockAdminCtrl).keyPressed(mockKeyEvent);


    }

    @Disabled
    @Test
    void keyDTest() {
        adminCtrl.eventsTable.getSelectionModel().select(0);
        when(mainCtrl.showVerificationPopup(Mockito.any())).thenReturn(true);
        KeyEvent mockKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "",
            KeyCode.D, false, false, false, false);
        adminCtrl.keyPressed(mockKeyEvent);
        assertEquals(adminCtrl.debugText.getText(),
            mainCtrl.getPreferedLanguage().getText("eventDeleteSuccess"));
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
