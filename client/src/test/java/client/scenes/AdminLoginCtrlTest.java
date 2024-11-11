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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLoginCtrlTest extends ApplicationSpec {

    private static AdminLoginCtrl adminLoginCtrl;

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
        var adminLogin = myFXML.load(AdminLoginCtrl.class, "client", "scenes",
                "AdminLogin.fxml");
        adminLoginCtrl = adminLogin.getKey();
        language = new Language("EN");
        adminLoginCtrl.server = server;
        commons.Event event = new Event();
        adminLoginCtrl.mainCtrl = mainCtrl;
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
        adminLoginCtrl.refreshLanguage();
        assertEquals("Admin Login", adminLoginCtrl.adminLoginTitle.getText());
        assertEquals("Password", adminLoginCtrl.password.getText());
        assertEquals("Cancel", adminLoginCtrl.cancelButton.getText());
        assertEquals("Confirm", adminLoginCtrl.confirmButton.getText());

    }
    @Test
    void clearFieldsTest() {
        adminLoginCtrl.adminPassword.setText("abcabcabcabcabc");
        adminLoginCtrl.wrongPassword.setText("nbjdkdieperr");
        adminLoginCtrl.clearFields();
        assertEquals("", adminLoginCtrl.adminPassword.getText());
        assertEquals("", adminLoginCtrl.wrongPassword.getText());
    }

    @Test
    void keyPressedTest() throws ServerException {
        Mockito.when(mainCtrl.getPreferedLanguage()).thenReturn(language);
        KeyEvent escKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false,
                false, false, false);
        KeyEvent enterKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false,
                true, false, false);
        doNothing().when(mainCtrl).showStart();
        adminLoginCtrl.adminPassword.setText("");
        adminLoginCtrl.keyPressed(enterKeyEvent);
        assertEquals("Wrong password entered!",
                adminLoginCtrl.wrongPassword.getText());

        adminLoginCtrl.keyPressed(escKeyEvent);
        verify(mainCtrl, times(1)).showStart();
    }

    // @Override
    public void start(Stage stage) throws Exception {

    }
}