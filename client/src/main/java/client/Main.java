package client;

import client.scenes.*;
import client.utils.CurrencyExchangeService;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Currency;
import java.util.List;

import static com.google.inject.Guice.createInjector;

/**
 * Main Client class
 */
@SpringBootApplication
@EntityScan(basePackages = { "client" })
public class Main extends Application {
    private static final Injector injector = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(injector);

    public static void main(String[] args) {
        launch();
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) {
        // TODO this is temporary!!! when the database will be set to "update"
        // events for a client will persist.
        // Now every time a client is opened they will be reset.
//        SplittyConfig.resetRecentEventIds();
        SplittyConfig sc = new SplittyConfig();

        Language preferredLanguage = sc.getPreferredLanguage();
        System.out.println("LANGUAGE" + preferredLanguage.toString());

        Currency preferredCurrency = sc.getPrefferedCurrency();

        List<Long> recentEventIds = sc.getRecentEventIds();

        String email = sc.getEmail();
        String password = sc.getPassword();

        var start = FXML.load(StartCtrl.class, "client", "scenes", "Start.fxml");
        var overview = FXML.load(OverviewCtrl.class, "client", "scenes", "Overview.fxml");
        var addEditParticipant = FXML.load(AddEditParticipantCtrl.class, "client", "scenes",
                "AddEditParticipant.fxml");
        var expenseSummary = FXML.load(ExpenseSummaryCtrl.class, "client", "scenes",
                "ExpenseSummary.fxml");
        var inviteParticipant = FXML.load(InviteParticipantCtrl.class, "client", "scenes",
                "InviteParticipant.fxml");
        var debt = FXML.load(DebtCtrl.class, "client", "scenes", "Debt.fxml");
        var language = FXML.load(LanguageCtrl.class, "client", "scenes", "Language.fxml");

        var login = FXML.load(AdminLoginCtrl.class, "client", "scenes", "AdminLogin.fxml");
        var changeTitle = FXML.load(OverviewCtrl.class, "client", "scenes", "ChangeTitle.fxml");
        var adminShortcuts = FXML.load(AdminShortcutsCtrl.class,
                "client", "scenes", "AdminShortcuts.fxml");

        var mainCtrl = injector.getInstance(MainCtrl.class);
        var adminEventView = FXML.load(AdminCtrl.class, "client", "scenes",
                "Admin.fxml");
        var loadEvent = FXML.load(LoadEventCtrl.class, "client", "scenes", "LoadEvent.fxml");

        var serverError = FXML.load(StartCtrl.class, "client", "scenes", "ServerError.fxml");
        var addExpense = FXML.load(AddExpenseCtrl.class, "client", "scenes", "AddExpense.fxml");
        var infoPage = FXML.load(InfoCtrl.class, "client", "scenes", "InfoPage.fxml");
        var clientInfoPage = FXML.load(OverviewInfoCtrl.class, "client", "scenes",
                "ClientInfoPage.fxml");
        var settingInfoPage = FXML.load(SettingsInfoCtrl.class, "client", "scenes",
                "SettingInfoPage.fxml");

        var settings = FXML.load(SettingsCtrl.class, "client", "scenes",
                "Settings.fxml");


        mainCtrl.initialize(primaryStage, new Stage(), start, overview, addEditParticipant,
                inviteParticipant, debt, language, changeTitle, expenseSummary, login,
                loadEvent, addExpense, adminEventView, adminShortcuts, serverError,
                infoPage, clientInfoPage, settingInfoPage, recentEventIds,
                preferredLanguage, preferredCurrency, settings, email, password,
                injector.getInstance(CurrencyExchangeService.class));

        primaryStage.setOnCloseRequest(e -> {
            mainCtrl.closeProgram();
        });
    }
}
