package client.scenes;

import client.Language;
import client.SplittyConfig;
import client.utils.CurrencyExchangeService;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.inject.Inject;
import java.util.Currency;
import java.util.List;
import java.util.Optional;


/**
 * MainCtrl class
 */
public class MainCtrl {
    private ServerUtils server;

    @Inject
    private SplittyConfig splittyConfig;

    public Stage primaryStage;
    private Stage secondaryStage;
    private StartCtrl startCtrl;
    public Scene startScene;
    private AdminLoginCtrl adminLoginCtrl;
    private Scene adminLoginScene;

    private Scene saveEventScene;

    private LanguageCtrl languageCtrl;
    private Scene languageScene;
    private OverviewCtrl overviewCtrl;
    private Scene overviewScene;

    private AddEditParticipantCtrl addEditParticipantCtrl;
    private Scene addEditParticipantScene;

    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpenseScene;


    private InviteParticipantCtrl inviteParticipantCtrl;
    private Scene inviteParticipantScene;

    private InfoCtrl infoCtrl;
    private OverviewInfoCtrl overviewInfoCtrl;
    private Scene infoScene;
    private Scene clientInfoScene;

    private SettingsInfoCtrl settingsInfoCtrl;
    private Scene settingInfoScene;
    private DebtCtrl debtCtrl;
    private Scene debtScene;
    public Event currentEvent;

    private LoadEventCtrl loadEventCtrl;

    private Scene adminEventsView;
    private AdminCtrl adminCtrl;
    private Scene loadEvent;
    private Scene adminShortcut;
    private Scene serverError;
    private AdminShortcutsCtrl adminShortcutsCtrl;
    private Scene changeTitleScene;
    private ExpenseSummaryCtrl expenseSummaryCtrl;
    private Scene expenseSummaryScene;
    public boolean adminIsParent = false;

    public List<Long> recentEventIds;
    public Language preferedLanguage;

    public Currency preferredCurrency;

    public String email;
    public String password;

    public SettingsCtrl settingsCtrl;
    public Scene settingsScene;
    private int serverStatus;
    public Boolean emailValidity = false;
    public PageController currentPrimaryController;
    public CurrencyExchangeService currencyExchangeService;


    @Inject
    public MainCtrl(ServerUtils server, SplittyConfig splittyConfig) {
        this.server = server;
        this.splittyConfig = splittyConfig;
    }

    public MainCtrl() {

    }

    /**
     * initialize method to create scenes and their controllers
     */
    public void initialize(Stage primaryStage, Stage secondaryStage,
                           Pair<StartCtrl, Scene> start,
                           Pair<OverviewCtrl, Scene> overview,
                           Pair<AddEditParticipantCtrl, Scene> addEditParticipant,
                           Pair<InviteParticipantCtrl, Scene> inviteParticipant,
                           Pair<DebtCtrl, Scene> debt,
                           Pair<LanguageCtrl, Scene> language,
                           Pair<OverviewCtrl, Scene> changeTitle,
                           Pair<ExpenseSummaryCtrl, Scene> expenseSummary,
                           Pair<AdminLoginCtrl, Scene> adminLogin,
                           Pair<LoadEventCtrl, Scene> loadEvent,
                           Pair<AddExpenseCtrl, Scene> addExpense,
                           Pair<AdminCtrl, Scene> adminEventsView,
                           Pair<AdminShortcutsCtrl, Scene> adminShortcut,
                           Pair<StartCtrl, Scene> serverError,
                           Pair<InfoCtrl, Scene> infoPage,
                           Pair<OverviewInfoCtrl, Scene> clientInfoPage,
                           Pair<SettingsInfoCtrl, Scene> settingsInfoPage,
                           List<Long> recentEventIds,
                           Language preferedLanguage, Currency preferredCurrency,
                           Pair<SettingsCtrl, Scene> settings,
                           String username, String password,
                           CurrencyExchangeService currencyExchangeService) {

        this.primaryStage = primaryStage;
        this.secondaryStage = secondaryStage;

        this.startCtrl = start.getKey();
        this.startScene = start.getValue();
        startScene.setOnKeyPressed(e -> startCtrl.keyPressed(e));

        this.overviewCtrl = overview.getKey();
        this.overviewScene = overview.getValue();
        overviewScene.setOnKeyPressed(e -> overviewCtrl.keyPressed(e));

        this.addEditParticipantCtrl = addEditParticipant.getKey();
        this.addEditParticipantScene = addEditParticipant.getValue();
        addEditParticipantScene.setOnKeyPressed(e -> addEditParticipantCtrl.keyPressed(e));
        this.adminShortcut = adminShortcut.getValue();
        this.adminShortcutsCtrl = adminShortcut.getKey();
        this.adminShortcut.setOnKeyPressed(e -> adminShortcutsCtrl.close());
        this.adminLoginCtrl = adminLogin.getKey();
        this.adminLoginScene = adminLogin.getValue();
        this.adminLoginScene.setOnKeyPressed(e -> adminLoginCtrl.keyPressed(e));

        this.changeTitleScene = changeTitle.getValue();
        this.changeTitleScene.setOnKeyPressed(e -> overviewCtrl.keyPressed(e));

        this.expenseSummaryCtrl = expenseSummary.getKey();
        this.expenseSummaryScene = expenseSummary.getValue();
        expenseSummaryScene.setOnKeyPressed(e -> expenseSummaryCtrl.keyPressed(e));

        this.inviteParticipantCtrl = inviteParticipant.getKey();
        this.inviteParticipantScene = inviteParticipant.getValue();
        inviteParticipantScene.setOnKeyPressed(e -> inviteParticipantCtrl.keyPressed(e));

        this.debtCtrl = debt.getKey();
        this.debtScene = debt.getValue();
        debtScene.setOnKeyPressed(e -> debtCtrl.keyPressed(e));

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpenseScene = addExpense.getValue();
        addExpenseScene.setOnKeyPressed(e -> addExpenseCtrl.keyPressed(e));

        this.serverError = serverError.getValue();
        this.serverError.setOnKeyPressed(e -> startCtrl.keyPressed(e));

        this.loadEvent = loadEvent.getValue();
        this.loadEventCtrl = loadEvent.getKey();
        this.loadEvent.setOnKeyPressed(e -> loadEventCtrl.keyPressed(e));

        this.adminEventsView = adminEventsView.getValue();
        this.adminCtrl = adminEventsView.getKey();
        this.adminEventsView.setOnKeyPressed(e -> adminCtrl.keyPressed(e));

        this.languageCtrl = language.getKey();
        this.languageScene = language.getValue();
        languageScene.setOnKeyPressed(e -> languageCtrl.keyPressed(e));

        this.infoCtrl = infoPage.getKey();
        this.infoScene = infoPage.getValue();
        this.overviewInfoCtrl = clientInfoPage.getKey();
        this.clientInfoScene = clientInfoPage.getValue();
        infoScene.setOnKeyPressed(e -> infoCtrl.keyPressed(e));
        clientInfoScene.setOnKeyPressed(e -> overviewInfoCtrl.keyPressed(e));

        this.settingsInfoCtrl = settingsInfoPage.getKey();
        this.settingInfoScene = settingsInfoPage.getValue();
        settingInfoScene.setOnKeyPressed(e -> settingsInfoCtrl.keyPressed(e));

        this.settingsCtrl = settings.getKey();
        this.settingsScene = settings.getValue();
        settingsScene.setOnKeyPressed(e -> settingsCtrl.keyPressed(e));

        this.recentEventIds = recentEventIds;
        this.preferedLanguage = preferedLanguage;

        this.preferredCurrency = preferredCurrency;

        this.email = username;
        this.password = password;

        this.currencyExchangeService = currencyExchangeService;

        setDefaultStageSetting();

        checkServer();
        if (startCtrl.getServerStatus() == 200) {
            showStart();
        } else {
            showStartWithoutRefresh();
            showError();
        }

    }

    /***
     * Apply some default settings to the stages in the application
     */
    public void setDefaultStageSetting() {
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("client/images/splittyIcon.png"));
        secondaryStage.initOwner(primaryStage);
        secondaryStage.initStyle(StageStyle.UNDECORATED);
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
    }

    public Language getPreferedLanguage() {
        return preferedLanguage;
    }

    public void showLoadEvent() {
        showPopupStage("Choose Event to Load", loadEvent, loadEventCtrl);
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }


    public void showError() {
        showPopupStageNoRefresh("Server not found", serverError, startCtrl);
    }


    /***
     * Show start page
     */
    public void showStart() {
        adminIsParent = false;
        showPrimaryStage("Start", startScene, startCtrl);
    }

    public void showStartWithoutRefresh() {
        adminIsParent = false;
        showPrimaryStageNoRefresh("Start", startScene, startCtrl);
    }

    public void showEventsView() {
        showPrimaryStage("Events list", adminEventsView, adminCtrl);
    }


    /***
     * Show overview page of event with id equal to eventID
     */
    public void showOverview() {
        showPrimaryStage("Event Overview", overviewScene, overviewCtrl, currentEvent.getId());
    }

    public void showLanguage() {
        showPopupStage("Language Selection", languageScene, languageCtrl);
    }

    public void showSummary() {
        showPopupStage("Expense Summary", expenseSummaryScene,
                expenseSummaryCtrl, currentEvent.getId());
        expenseSummaryCtrl.refreshPage(currentEvent.getId());
    }

    /***
     * Show page for an admin to login
     */
    public void showAdminLogin() {
        showPrimaryStage("Admin login", adminLoginScene, adminLoginCtrl);
    }

    public void showAdminShortcuts() {
        showPopupStage("Shortcuts for admin functionalities", adminShortcut, adminShortcutsCtrl);
    }


    /***
     * Show page for admin
     */
    public void showAdminPage() {
        adminIsParent = true;
        showPrimaryStage("Admin Page", adminEventsView, adminCtrl);
    }

    /***
     * Show debt page of event with id equal to eventID
     */
    public void showDebt() {
        showPopupStage("Event Debt Report", debtScene, debtCtrl, currentEvent.getId());
    }

    public void changeTitle() {
        showPopupStage("Edit Title", changeTitleScene, overviewCtrl, currentEvent.getId());
    }

    /***
     * Show add participant page of event with id equal to eventID
     */
    public void showAddParticipant() {
        showPopupStage("Add Participant", addEditParticipantScene,
                addEditParticipantCtrl, currentEvent.getId(), true);
    }

    /***
     * Show edit participant page of event with id equal to eventID
     */
    public void showEditParticipant() {
        showPopupStage("Edit Participant", addEditParticipantScene,
                addEditParticipantCtrl, currentEvent.getId(), false);
    }

    /***
     * Show invite participant page of event with id equal to eventID
     */
    public void showInviteParticipant() {
        showPopupStage("Invite Participant", inviteParticipantScene,
                inviteParticipantCtrl, currentEvent.getId(), email, password);
    }

    public void showInfoPage() {
        showPopupStage("Info", infoScene, infoCtrl);
    }


    public void showSettings() {
        showPrimaryStage("Settings", settingsScene, settingsCtrl, email, password);
    }

    public void showAddExpense(Expense expense) {
        showPopupStage("Add Expense", addExpenseScene, addExpenseCtrl,
                currentEvent.getId(), expense, true);
    }

    public void showEditRemoveExpense(Expense expense) {
        showPopupStage("Edit Expense", addExpenseScene, addExpenseCtrl,
                currentEvent.getId(), expense, false);
    }

    public void showClientInfoPage() {
        showPopupStage("Client Info", clientInfoScene, infoCtrl);
    }

    public void showOverviewInfoPage() {
        showPopupStage("Overview Info", clientInfoScene, overviewInfoCtrl);
    }

    public void showSettingsInfo() {
        showPopupStage("Settings Info", settingInfoScene, settingsInfoCtrl);
    }

    public void showPrimaryStage(String title, Scene scene, PageController controller,
                                 Object... args) {
        currentPrimaryController = controller;
        controller.refreshPage(args);
        if (Platform.isFxApplicationThread()) {
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }
    }

    public void showPrimaryStageNoRefresh(String title, Scene scene, PageController controller,
                                          Object... args) {
        if (Platform.isFxApplicationThread()) {
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }
    }

    /***
     * Create a new popup stage with a title and a scene
     * @param title
     * @param scene
     */
    public void showPopupStage(String title, Scene scene, PageController controller,
                               Object... args) {
        controller.refreshPage(args);
        if (Platform.isFxApplicationThread()) {
            secondaryStage.setTitle(title);
            secondaryStage.setScene(scene);
            secondaryStage.showAndWait();
        }
    }

    public void showPopupStageNoRefresh(String title, Scene scene, PageController controller,
                                        Object... args) {
        if (Platform.isFxApplicationThread()) {
            secondaryStage.setTitle(title);
            secondaryStage.setScene(scene);
            secondaryStage.show();
        }
    }

    /**
     * Shows a verification popup with yes/no buttons and a specified message
     *
     * @param message the question that the user sees
     * @return true if yes was clicked, false otherwise
     */
    public boolean showVerificationPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Customize the buttons
        ButtonType buttonTypeYes = new ButtonType(preferedLanguage.getText("yes"));
        ButtonType buttonTypeNo = new ButtonType(preferedLanguage.getText("no"));
        alert.getButtonTypes().setAll(buttonTypeNo, buttonTypeYes);

        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alert.getDialogPane().getScene().getStylesheets()
                .add(primaryStage.getScene().getRoot().getStylesheets().getFirst());
        alert.getDialogPane().getScene().getRoot().getStyleClass().add("popup-background");
//        alert.getDialogPane().getScene().getRoot().getStyleClass().add("dialog");
//        alert.getDialogPane().setMaxWidth(secondaryStage.getWidth());
        dialogStage.initOwner(primaryStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);

        // Set the modality to APPLICATION_MODAL to block input events to other windows
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        // Show the confirmation dialog and wait for user input
        Optional<ButtonType> result = alert.showAndWait();

        // Return true if the user clicks "Yes", false otherwise
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    public boolean showConfirmationPopUp(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Customize the buttons
        ButtonType buttonTypeYes = new ButtonType("Ok");
        alert.getButtonTypes().setAll(buttonTypeYes);

        alert.getDialogPane().getScene().getStylesheets()
                .add(primaryStage.getScene().getRoot().getStylesheets().getFirst());
        alert.getDialogPane().getScene().getRoot().getStyleClass().add("popup-background");
        alert.getDialogPane().getScene().getRoot().getStyleClass().add("dialog");
        alert.getDialogPane().setMaxWidth(secondaryStage.getWidth());
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.initOwner(primaryStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.centerOnScreen();

        // Set the modality to APPLICATION_MODAL to block input events to other windows
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        // Show the confirmation dialog and wait for user input
        Optional<ButtonType> result = alert.showAndWait();

        // Return true if the user clicks "Yes", false otherwise
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    public void setPreferredLanguage(Language language) {
        this.preferedLanguage = language;
    }

    /***
     * Close the popup stage and refreshContent overview page
     */
    public void closePopupStage() {
        if (Platform.isFxApplicationThread()) {
            secondaryStage.close();
        }
        if (currentPrimaryController != null) {
            if (currentPrimaryController.getClass().equals(settingsCtrl.getClass())) {
                currentPrimaryController.refreshPage(email, password);
            } else {
                currentPrimaryController.refreshPage(
                        currentEvent != null ? currentEvent.getId() : null);
            }
        }
    }

    /**
     * Closes popup in the admin part of the application
     */
    public void closePopupStageAdmin() {
        if (Platform.isFxApplicationThread()) {
            secondaryStage.close();
        }
    }

    /**
     * Closes popup and refreshes the start page
     */
    public void closePopupStageStart() {
        if (Platform.isFxApplicationThread()) {
            secondaryStage.close();
        }
        startCtrl.refreshPage();
    }


    public void closeProgram() {
        // Backup and store the config before closing the program
        for (int i = 0; i < recentEventIds.size(); i++) {
            splittyConfig.setRecentEventId(i + 1, recentEventIds.get(i));
        }
        splittyConfig.setPreferredLanguage(preferedLanguage.getCurrentLanguage());
        splittyConfig.setPreferredCurrency(preferredCurrency.toString());
        splittyConfig.setEmail(email);
        splittyConfig.setPassword(password);
        splittyConfig.writeProperties();
        System.exit(0);
    }

    public void checkServer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            try {
                serverStatus = server.check();
                if (serverStatus != 200) {
                    System.out.println("TEST=" + primaryStage.toString());
                    showStartWithoutRefresh();
                    showError();
                }
            } catch (NullPointerException e) {
                ;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
