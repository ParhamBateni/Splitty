package client.scenes;

import client.Language;
import client.MyFXML;
import client.MyModule;
import client.utils.CurrencyExchangeService;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.stream.Collectors;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class OverviewCtrlTest extends ApplicationSpec {
    private static OverviewCtrl overviewCtrl;

    @Mock
    public ServerUtils server;
    @Mock
    public MainCtrl mainCtrl;

    private List<Event> events;

    @BeforeEach
    void setUp() throws ClassNotFoundException, TimeoutException {
        MockitoAnnotations.openMocks(this);
        Mockito.doNothing().when(server).registerForMessages(Mockito.anyString(),
                Mockito.any(), Mockito.any());
        Mockito.doNothing().when(server).send(Mockito.any(),
                Mockito.any());
        Mockito.when(server.connect(Mockito.anyString())).thenReturn(null);
        // create test data
        events = new ArrayList<>();
        events.add(new Event("Test Event 1"));

        List<Participant> participants = new ArrayList<>() {
            {
                add(new Participant(1, "Test Participant 1", "email",
                        "iban", "bic", events.get(0)));
                add(new Participant(2, "Test Participant 2", "email",
                        "iban", "bic", events.get(0)));
                add(new Participant(3, "Test Participant 3", "email",
                        "iban", "bic", events.get(0)));
                add(new Participant(4, "Test Participant 4", "email",
                        "iban", "bic", events.get(0)));
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
        var overview = myFXML.load(OverviewCtrl.class, "client", "scenes",
                "Overview.fxml");
        var changeTitle = myFXML.load(OverviewCtrl.class, "client", "scenes", "ChangeTitle.fxml");
        overviewCtrl = overview.getKey();
        overviewCtrl.server = server;
        overviewCtrl.mainCtrl = mainCtrl;
        overviewCtrl.mainCtrl.preferredCurrency = Currency.getInstance("EUR");
        overviewCtrl.mainCtrl.preferedLanguage = new Language("EN");
        overviewCtrl.mainCtrl.currencyExchangeService = new CurrencyExchangeService();
        overviewCtrl.expenseTable.getItems().addAll(expenses);
    }

    @Test
    void initialize() {
        Mockito.doNothing().when(server).registerForUpdates(Mockito.anyString(),
                Mockito.anyLong(), Mockito.any(), Mockito.anyString());

        overviewCtrl.initialize(null, null);
        Expense expense = events.get(0).getExpenses().getFirst();
        assertEquals(expense.getDate(), overviewCtrl.expenseDate.getCellData(0));
        assertEquals(expense.getTitle(), overviewCtrl.expenseTitle.getCellData(0));
        assertEquals(String.format("%.2f", expense.getAmount() / 100.0)
                        + " " + expense.getCurrency().toString(),
                overviewCtrl.expenseAmount.getCellData(0));
        assertEquals(expense.getPayer().getName(), overviewCtrl.expensePayer.getCellData(0));
        assertEquals(expense.getParticipants().stream().map(Participant::getName)
                        .collect(Collectors.joining(", "))
                , overviewCtrl.expenseParticipants.getCellData(0));
        MouseEvent mockedMouseEvent = mock(MouseEvent.class);
        overviewCtrl.eventName.getOnMouseEntered().handle(mockedMouseEvent);
        assertEquals(Cursor.HAND, overviewCtrl.eventName.getCursor());
        overviewCtrl.eventName.getOnMouseExited().handle(mockedMouseEvent);
        assertEquals(Cursor.DEFAULT, overviewCtrl.eventName.getCursor());

    }

//    @Test
//    void filterExpensesTest() {
//        List<Expense> expenses = events.getFirst().getExpenses();
//        Participant participant = events.getFirst().getParticipants().getFirst();
//        mainCtrl.currentEvent = events.getFirst();
//        overviewCtrl.filterOnPayerCheckbox.setSelected(true);
//        overviewCtrl.filterOnPayeeCheckbox.setSelected(true);
//        overviewCtrl.filterExpenses(participant);
//        assertEquals(List.of(expenses.get(0), expenses.get(2)),
//                overviewCtrl.expenseTable.getItems());
//    }

//    @Test
//    void registerExpenseListenerTest() {
//        overviewCtrl.registerExpenseListener(10);
//        Mockito.doNothing().when(server).registerForMessages(Mockito.any(),
//                Mockito.any(), Mockito.any());
//        Mockito.verify(server, Mockito.atLeast(1))
//                .registerForMessages(Mockito.any(), Mockito.any(), Mockito.any());
//    }

//    @Test
//    void refreshTest() {
//        Mockito.doReturn(mainCtrl.preferedLanguage).when(mainCtrl).getPreferedLanguage();
//        Event event = events.getFirst();
//        Mockito.doReturn(event).when(server).getEventById(0L);
//        overviewCtrl.refreshContent(0L);
//        assertEquals(event.getTitle(), overviewCtrl.eventName.getText());
//        assertEquals(event.getParticipants().stream().map(Participant::getName)
//                .collect(Collectors.joining(", ")), overviewCtrl.participantNames.getText());
//        assertEquals(event.getParticipants(), overviewCtrl.filterMenu.getItems());
//        assertEquals(event.getExpenses(), overviewCtrl.expenseTable.getItems());
//        event.getParticipants().clear();
//        event.getExpenses().clear();
//        overviewCtrl.refreshContent(0L);
//        assertEquals(OverviewCtrl.noParticipantsText, overviewCtrl.participantNames.getText());
//        assertEquals(OverviewCtrl.noExpensesText, ((Label) overviewCtrl.expenseTable
//                .getPlaceholder()).getText());
//        Mockito.doReturn(null).when(server).getEventById(1L);
//        overviewCtrl.refreshContent(1L);
//        Mockito.verify(mainCtrl).showStart();
//    }

    @Test
    void changeEventNameTest() {
        Event event = events.getFirst();
        overviewCtrl.mainCtrl.currentEvent = event;
        Mockito.doReturn(event).when(server).getEventById(0);
        overviewCtrl.newEventName.setText("Changed title");
        overviewCtrl.changeEventName();
        assertEquals("Changed title", event.getTitle());
        Mockito.verify(server).updateEvent(event);
        Mockito.verify(mainCtrl).closePopupStage();
        overviewCtrl.newEventName.setText("");
        Mockito.doReturn(mainCtrl.preferedLanguage).when(mainCtrl).getPreferedLanguage();
        overviewCtrl.changeEventName();
        assertEquals(mainCtrl.getPreferedLanguage().getText("emptyEventFail"),
                overviewCtrl.changeEventNameErrorText.getText());
    }

    @Test
    void closeTitleChangeTest() {
        Mockito.doNothing().when(mainCtrl).closePopupStage();
        overviewCtrl.closeTitleChange();
        Mockito.verify(mainCtrl).closePopupStage();
    }

    @Test
    void changeNameTest() {
        Mockito.doNothing().when(mainCtrl).changeTitle();
        overviewCtrl.changeName();
        Mockito.verify(mainCtrl).changeTitle();
    }

    @Test
    void clearFieldsTest() {
        overviewCtrl.clearFields();
        assertEquals(overviewCtrl.participantNames.getText(), "No participants yet :)");
        assertEquals(overviewCtrl.newEventName.getText(), overviewCtrl.eventName.getText());
        assertEquals(overviewCtrl.changeEventNameErrorText.getText(), "");
        assertTrue(overviewCtrl.filterMenu.getItems().isEmpty());
        assertTrue(overviewCtrl.closeFilterImageView.isDisabled());
        assertTrue(overviewCtrl.expenseTable.getItems().isEmpty());
    }

//    @Test
//    void keyPressedTest() {
//        KeyEvent escKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false,
//                false, false, false);
//        KeyEvent ppKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.P, false,
//                false, false, false);
//        KeyEvent eeKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.E, false,
//                false, false, false);
//        KeyEvent iiKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.I, false,
//                false, false, false);
//        KeyEvent xxKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.X, false,
//                false, false, false);
//        KeyEvent ddKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.D, false,
//                false, false, false);
//        overviewCtrl.keyPressed(escKeyEvent);
//        Mockito.verify(mainCtrl).showStart();
//        overviewCtrl.keyPressed(ppKeyEvent);
//        Mockito.verify(mainCtrl).showAddParticipant();
//        overviewCtrl.keyPressed(eeKeyEvent);
//        Mockito.verify(mainCtrl).showEditParticipant();
//        overviewCtrl.keyPressed(iiKeyEvent);
//        Mockito.verify(mainCtrl).showInviteParticipant();
//        overviewCtrl.keyPressed(xxKeyEvent);
//        Mockito.verify(mainCtrl).showAddExpense(null);
//        overviewCtrl.keyPressed(ddKeyEvent);
//        Mockito.verify(mainCtrl).showDebt();
//
//    }

    @Test
    void addExpenseTest() {
        Mockito.doNothing().when(mainCtrl).showAddExpense(Mockito.any());
        overviewCtrl.addExpense(new ActionEvent());
        Mockito.verify(mainCtrl).showAddExpense(null);
    }

    @Test
    void editRemoveExpenseTest() {
        Mockito.doNothing().when(mainCtrl).showEditRemoveExpense(Mockito.any(Expense.class));
        overviewCtrl.editRemoveExpense(new Expense());
        Mockito.verify(mainCtrl).showEditRemoveExpense(Mockito.any(Expense.class));
    }

    @Test
    void settleDebtTest() {
        Mockito.doNothing().when(mainCtrl).showDebt();
        overviewCtrl.settleDebt(new ActionEvent());
        Mockito.verify(mainCtrl).showDebt();
    }

    @Test
    void sendInvitesTest() {
        Mockito.doNothing().when(mainCtrl).showInviteParticipant();
        overviewCtrl.sendInvites(new ActionEvent());
        Mockito.verify(mainCtrl).showInviteParticipant();
    }

    @Test
    void editUsersTest() {
        Mockito.doNothing().when(mainCtrl).showEditParticipant();
        overviewCtrl.editUsers(new ActionEvent());
        Mockito.verify(mainCtrl).showEditParticipant();
    }

    @Test
    void addUserTest() {
        Mockito.doNothing().when(mainCtrl).showAddParticipant();
        overviewCtrl.addUser(new ActionEvent());
        Mockito.verify(mainCtrl).showAddParticipant();
    }

    @Test
    void changeLanguageTest() {
        Mockito.doNothing().when(mainCtrl).showLanguage();
        overviewCtrl.changeLanguage(new ActionEvent());
        Mockito.verify(mainCtrl, Mockito.times(1)).showLanguage();
    }

//    @Test
//    void closeFilterTest() {
//        overviewCtrl.expenseTable.getItems().clear();
//        List<Expense> expenses = List.copyOf(events.getFirst().getExpenses());
//        overviewCtrl.closeFilter(mock(MouseEvent.class));
//        assertNull(overviewCtrl.filterMenu.getValue());
//        assertTrue(overviewCtrl.expenseTable.getItems().isEmpty());
//    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}