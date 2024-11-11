package client.scenes;

import client.Language;
import client.MyFXML;
import client.MyModule;
import client.utils.ServerException;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxToolkit;
import org.testfx.framework.spock.ApplicationSpec;

import java.awt.*;
import java.util.concurrent.TimeoutException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminShortcutsCtrlTest extends ApplicationSpec {

    private static AdminShortcutsCtrl adminShortcutsCtrl;

    @Mock
    private ServerUtils server;

    @Mock
    private MainCtrl mainCtrl;

    private Language language;

    @BeforeEach
    public void setUp() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            Class.forName("com.sun.glass.ui.monocle.MonoclePlatformFactory");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
        FxToolkit.registerPrimaryStage();
        MockitoAnnotations.openMocks(this);
        Injector injector = createInjector(new MyModule());
        MyFXML myFXML = new MyFXML(injector);
        var adminShortcuts = myFXML.load(AdminShortcutsCtrl.class, "client", "scenes",
                "AdminShortcuts.fxml");
        adminShortcutsCtrl = adminShortcuts.getKey();
        language = new Language("EN");
        adminShortcutsCtrl.server = server;
        Event event = new Event();
        adminShortcutsCtrl.mainCtrl = mainCtrl;
        mainCtrl.currentEvent = event;

    }

    @AfterEach
    public void clean() throws TimeoutException {
        FxToolkit.hideStage();
    }



    @Test
    public void refreshLanguageTest() {
        Language language1 = new Language("EN");
        Mockito.when(mainCtrl.getPreferedLanguage()).thenReturn(language);
        adminShortcutsCtrl.refreshLanguage();
        assertEquals("D - deletes the selected event",
                adminShortcutsCtrl.shortcutD.getText());
        assertEquals("S - saves the selected event",
                adminShortcutsCtrl.shortcutS.getText());
        assertEquals("Cancel", adminShortcutsCtrl.cancelButton.getText());
        assertEquals("L - Opens the event loading menu",
                adminShortcutsCtrl.shortcutL.getText());
        assertEquals("O - opens event overview from selected event",
                adminShortcutsCtrl.shortcutO.getText());

    }
    @Test
    void clearFieldsTest() {

    }

    @Test
    void keyPressedTest() throws ServerException {
        Mockito.when(mainCtrl.getPreferedLanguage()).thenReturn(language);
        KeyEvent escKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false,
                false, false, false);

        adminShortcutsCtrl.keyPressed(escKeyEvent);
        verify(mainCtrl, times(1)).closePopupStageAdmin();
    }

    // @Override
    public void start(Stage stage) throws Exception {

    }
}