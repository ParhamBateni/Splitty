package client.scenes;

import client.Language;
import commons.Event;
import commons.Expense;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxToolkit;
import org.testfx.framework.spock.ApplicationSpec;

import java.awt.*;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MainCtrlTest
 */
public class MainCtrlTest extends ApplicationSpec {

    private MainCtrl mainCtrl;

    @Mock
    private MainCtrl mockedMainCtrl;

    @Mock
    private Stage primaryStage;
    @Mock
    private Stage secondaryStage;
    @Mock
    private StartCtrl startCtrl;
    @Mock
    private Scene startScene;
    @Mock
    private AdminLoginCtrl adminLoginCtrl;
    @Mock
    private Scene adminLoginScene;
    @Mock
    private Scene saveEventScene;
    @Mock
    private LanguageCtrl languageCtrl;
    @Mock
    private Scene languageScene;
    @Mock
    private OverviewCtrl overviewCtrl;
    @Mock
    private Scene overviewScene;
    @Mock
    private AddEditParticipantCtrl addEditParticipantCtrl;
    @Mock
    private Scene addEditParticipantScene;
    @Mock
    private AddExpenseCtrl addExpenseCtrl;
    @Mock
    private Scene addExpenseScene;
    @Mock
    private InviteParticipantCtrl inviteParticipantCtrl;
    @Mock
    private Scene inviteParticipantScene;
    @Mock
    private DebtCtrl debtCtrl;
    @Mock
    private Scene debtScene;
    @Mock
    public Event currentEvent;
    @Mock
    private LoadEventCtrl loadEventCtrl;
    @Mock
    private Scene deleteEvent;
    @Mock
    private Scene deleteEventConfirmation;
    @Mock
    private Scene adminEventsView;
    @Mock
    private AdminCtrl adminCtrl;
    @Mock
    private Scene loadEvent;
    @Mock
    private Scene serverError;
    @Mock
    private Scene emailScene;
    @Mock
    private InfoCtrl infoCtrl;
    @Mock
    private Scene infoScene;
    @Mock
    private OverviewInfoCtrl overviewInfoCtrl;

    @Mock
    private Scene clientInfoScene;

    @Mock
    private SettingsInfoCtrl settingsInfoCtrl;

    @Mock
    private Scene settingsInfoScene;
    @Mock
    private Scene changeTitleScene;

    @Mock
    public List<Long> recentEventIds;
    @Mock
    private Scene expenseSummaryScene;
    @Mock
    private ExpenseSummaryCtrl expenseSummaryCtrl;
    @Mock
    public Language preferedLanguage;
    @Mock
    private Scene adminShortcut;
    @Mock
    private AdminShortcutsCtrl adminShortcutsCtrl;

    @Mock
    private SettingsCtrl settingsCtrl;

    @Mock
    private Scene settingScene;

    @BeforeEach
    public void setup() throws ClassNotFoundException, TimeoutException {
        MockitoAnnotations.openMocks(this);
        if (GraphicsEnvironment.isHeadless()) {
            Class.forName("com.sun.glass.ui.monocle.MonoclePlatformFactory");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        mainCtrl = new MainCtrl();
        mainCtrl.currentEvent = new Event();
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupStage(stage -> {
            primaryStage = stage;
        });
        FxToolkit.registerStage(() -> secondaryStage = new Stage());
        mainCtrl.initialize(primaryStage, secondaryStage,
                new Pair<>(startCtrl, startScene),
                new Pair<>(overviewCtrl, overviewScene),
                new Pair<>(addEditParticipantCtrl, addEditParticipantScene),
                new Pair<>(inviteParticipantCtrl, inviteParticipantScene),
                new Pair<>(debtCtrl, debtScene),
                new Pair<>(languageCtrl, languageScene),
                new Pair<>(overviewCtrl, changeTitleScene),
                new Pair<>(expenseSummaryCtrl, expenseSummaryScene),
                new Pair<>(adminLoginCtrl, adminLoginScene),
                new Pair<>(loadEventCtrl, loadEvent),
                new Pair<>(addExpenseCtrl, addExpenseScene),
                new Pair<>(adminCtrl, adminEventsView),
                new Pair<>(adminShortcutsCtrl, adminShortcut),
                new Pair<>(startCtrl, serverError),
                new Pair<>(infoCtrl, infoScene),
                new Pair<>(overviewInfoCtrl, clientInfoScene),
                new Pair<>(settingsInfoCtrl, settingsInfoScene), recentEventIds,
                preferedLanguage, Currency.getInstance("EUR"),
                new Pair<>(settingsCtrl, settingScene), "username", "password", null);
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    @Test
    public void getPreferredLanguageTest() {
        assertEquals(mainCtrl.preferedLanguage, mainCtrl.getPreferedLanguage());
    }

    @Test
    public void showLoadEventTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Choose Event to Load", loadEvent, loadEventCtrl);
            return null;
        }).when(mockedMainCtrl).showLoadEvent();
        mockedMainCtrl.showLoadEvent();
        mainCtrl.showLoadEvent();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class));
    }

    @Test
    public void showErrorTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Server not found", serverError, startCtrl);
            return null;
        }).when(mockedMainCtrl).showError();
        mockedMainCtrl.showError();
        mainCtrl.showError();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class));
    }

    @Test
    public void showStartTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.adminIsParent = false;
            mockedMainCtrl.showPrimaryStage("Start", startScene, startCtrl);
            return null;
        }).when(mockedMainCtrl).showStart();
        mockedMainCtrl.showStart();
        mainCtrl.showStart();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPrimaryStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class));
        assertFalse(mockedMainCtrl.adminIsParent);
    }

    @Test
    public void showEventsViewTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPrimaryStage("Events list", adminEventsView, adminCtrl);
            return null;
        }).when(mockedMainCtrl).showEventsView();
        mockedMainCtrl.showEventsView();
        mainCtrl.showEventsView();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPrimaryStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class));
    }

    @Test
    public void showOverviewTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPrimaryStage("Event Overview", overviewScene, overviewCtrl, 0);
            return null;
        }).when(mockedMainCtrl).showOverview();
        mockedMainCtrl.showOverview();
        mainCtrl.showOverview();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPrimaryStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class), Mockito.any());
    }

    @Test
    public void showLanguageTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Language Selection", languageScene, languageCtrl);
            return null;
        }).when(mockedMainCtrl).showLanguage();
        mockedMainCtrl.showLanguage();
        mainCtrl.showLanguage();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class));
    }

    @Test
    public void showAdminLoginTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPrimaryStage("Admin Login", adminLoginScene, adminLoginCtrl);
            return null;
        }).when(mockedMainCtrl).showAdminLogin();
        mockedMainCtrl.showAdminLogin();
        mainCtrl.showAdminLogin();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPrimaryStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class));
    }

    @Test
    public void showAdminPageTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.adminIsParent = true;
            mockedMainCtrl.showPrimaryStage("Admin Page", adminEventsView, adminCtrl);
            return null;
        }).when(mockedMainCtrl).showAdminPage();
        mockedMainCtrl.showAdminPage();
        mainCtrl.showAdminPage();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPrimaryStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class));
        assertTrue(mockedMainCtrl.adminIsParent);
    }

    @Test
    public void showDebtTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Event Debt Report", debtScene, debtCtrl, 0);
            return null;
        }).when(mockedMainCtrl).showDebt();
        mockedMainCtrl.showDebt();
        mainCtrl.showDebt();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class), Mockito.any());
    }

    @Test
    public void changeTitleTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Edit Title", changeTitleScene, overviewCtrl, 0);
            return null;
        }).when(mockedMainCtrl).changeTitle();
        mockedMainCtrl.changeTitle();
        mainCtrl.changeTitle();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class), Mockito.any());
    }

    @Test
    public void showAddParticipantTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Add Participant", addEditParticipantScene,
                    addEditParticipantCtrl, 0, true);
            return null;
        }).when(mockedMainCtrl).showAddParticipant();
        mockedMainCtrl.showAddParticipant();
        mainCtrl.showAddParticipant();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class), Mockito.any(), Mockito.anyBoolean());
    }

    @Test
    public void showEditParticipant() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Edit Participant", addEditParticipantScene,
                    addEditParticipantCtrl, 0, false);
            return null;
        }).when(mockedMainCtrl).showAddParticipant();
        mockedMainCtrl.showAddParticipant();
        mainCtrl.showAddParticipant();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class),
                        Mockito.any(), Mockito.anyBoolean());
    }

    @Test
    public void showInviteParticipantTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Invite Participant",
                    inviteParticipantScene, inviteParticipantCtrl, 0);
            return null;
        }).when(mockedMainCtrl).showInviteParticipant();
        mockedMainCtrl.showInviteParticipant();
        mainCtrl.showInviteParticipant();
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class), Mockito.any());
    }

    @Test
    public void showAddExpenseTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Add Expense", addExpenseScene,
                    addExpenseCtrl, 0, new Expense(), true);
            return null;
        }).when(mockedMainCtrl).showAddExpense(Mockito.any());
        mockedMainCtrl.showAddExpense(new Expense());
        mainCtrl.showAddExpense(new Expense());
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class), Mockito.any(),
                        Mockito.any(Expense.class), Mockito.anyBoolean());
    }

    @Test
    public void showEditRemoveExpenseTest() {
        Mockito.doAnswer(invocation -> {
            mockedMainCtrl.showPopupStage("Edit Expense",
                    addExpenseScene, addExpenseCtrl, 0, new Expense(), false);
            return null;
        }).when(mockedMainCtrl).showEditRemoveExpense(Mockito.any());
        mockedMainCtrl.showEditRemoveExpense(new Expense());
        mainCtrl.showEditRemoveExpense(new Expense());
        Mockito.verify(mockedMainCtrl, Mockito.times(1))
                .showPopupStage(Mockito.anyString(), Mockito.any(Scene.class),
                        Mockito.any(PageController.class), Mockito.any(),
                        Mockito.any(Expense.class), Mockito.anyBoolean());
    }

    @Test
    public void showPrimaryStageTest() {
        Mockito.doNothing().when(debtCtrl).refreshPage(Mockito.any());
        mainCtrl.showPrimaryStage("Debt Page", debtScene, debtCtrl);
        Mockito.verify(debtCtrl, Mockito.times(1)).refreshPage(Mockito.any());
    }

    @Test
    public void showPopupStageTest() {
        Mockito.doNothing().when(addEditParticipantCtrl).refreshPage(Mockito.any());
        mainCtrl.showPrimaryStage("Add Edit Participant",
                addEditParticipantScene, addEditParticipantCtrl);
        Mockito.verify(addEditParticipantCtrl, Mockito.times(1))
                .refreshPage(Mockito.any());
    }

    @Test
    public void closePopupStageTest() {
        mainCtrl.currentPrimaryController = overviewCtrl;
        mainCtrl.closePopupStage();
        Mockito.verify(overviewCtrl, Mockito.times(1)).refreshPage(currentEvent.getId());
    }


    @Test
    public void closePopupStageAdminTest() {
        mainCtrl.closePopupStageAdmin();
    }

    @Test
    public void closePopupStageStartTest() {
        mainCtrl.currentPrimaryController = startCtrl;
        mainCtrl.closePopupStage();
        Mockito.verify(startCtrl, Mockito.times(1)).refreshPage(Mockito.any());
    }
}