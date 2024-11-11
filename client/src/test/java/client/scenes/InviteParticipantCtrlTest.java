package client.scenes;

import client.Language;
import client.MyFXML;
import client.MyModule;
import client.utils.ServerException;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Event;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxToolkit;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InviteParticipantCtrlTest {
    // class InviteParticipantCtrlTest extends ApplicationSpec {
    private static InviteParticipantCtrl inviteParticipantCtrl;

    @Mock
    private ServerUtils server;

    @Mock
    private MainCtrl mainCtrl;

    private Language language = new Language("EN");

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
        var inviteParticipant = myFXML.load(InviteParticipantCtrl.class, "client", "scenes",
                "InviteParticipant.fxml");
        inviteParticipantCtrl = inviteParticipant.getKey();
        inviteParticipantCtrl.server = server;
        Event event = new Event();
        inviteParticipantCtrl.mainCtrl = mainCtrl;
        mainCtrl.currentEvent = event;
    }

    @AfterEach
    public void clean() throws TimeoutException {
        FxToolkit.hideStage();
    }

    @Test
    void sendInviteToEmailsTest() throws ServerException {
        Mockito.doNothing().when(server).sendInviteToEmails(
                Mockito.anyList(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.when(mainCtrl.getPreferedLanguage()).thenReturn(language);
        inviteParticipantCtrl.emailTextArea.setText("abc@gmail.com\nbca@gmail.com");
        inviteParticipantCtrl.sendInviteToEmails(new ActionEvent());
        System.out.println(inviteParticipantCtrl.inviteResultText.getText());
        assertEquals("Invite link is successfully sent to the emails!",
                inviteParticipantCtrl.inviteResultText.getText());
        assertEquals(inviteParticipantCtrl.inviteResultText.getFill(), Color.GREEN);

        inviteParticipantCtrl.emailTextArea.setText("asdfsdfd.com\nwerewre.com");
        inviteParticipantCtrl.sendInviteToEmails(new ActionEvent());
        assertEquals("Emails should be seperated by a line and should\n follow pattern:" +
                Participant.emailPattern, inviteParticipantCtrl.inviteResultText.getText());
        assertEquals(inviteParticipantCtrl.inviteResultText.getFill(), Color.RED);
    }

    @Test
    void refreshTest() {
        Event e1 = new Event(0, "Party", List.of(), List.of());
        Event e2 = new Event(1, "Dinner", List.of(), List.of());

        doAnswer(invocation -> {
            long id = invocation.getArgument(0);
            return id == 0 ? e1 : id == 1 ? e2 : null;
        }).when(server).getEventById(anyLong());
        doNothing().when(mainCtrl).showStart();
        Mockito.when(mainCtrl.getPreferedLanguage()).thenReturn(language);
        inviteParticipantCtrl.refreshContent((long) 0, "", "");
        assertEquals(e1.getTitle(), inviteParticipantCtrl.eventNameText.getText());
        assertEquals(e1.getLink(), inviteParticipantCtrl.inviteCodeText.getText());

        inviteParticipantCtrl.refreshContent((long) 1, "", "");
        assertEquals(e2.getTitle(), inviteParticipantCtrl.eventNameText.getText());
        assertEquals(e2.getLink(), inviteParticipantCtrl.inviteCodeText.getText());

        inviteParticipantCtrl.refreshContent((long) 3, "", "");
        verify(mainCtrl, times(1)).showStart();

    }

    @Test
    void clearFieldsTest() {
        inviteParticipantCtrl.emailTextArea.setText("abcabcabcabcabc");
        inviteParticipantCtrl.inviteResultText.setText("nbjdkdieperr");
        inviteParticipantCtrl.clearFields();
        assertEquals("", inviteParticipantCtrl.emailTextArea.getText());
        assertEquals("", inviteParticipantCtrl.inviteResultText.getText());
    }

    @Test
    void keyPressedTest() throws ServerException {
        KeyEvent escKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false,
                false, false, false);
        KeyEvent enterKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false,
                true, false, false);
        doNothing().when(mainCtrl).closePopupStage();
        doNothing().when(server).sendInviteToEmails(Mockito.anyList(),
                Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.when(mainCtrl.getPreferedLanguage()).thenReturn(language);
        inviteParticipantCtrl.emailTextArea.setText("abc@gmail.com");
        inviteParticipantCtrl.keyPressed(enterKeyEvent);
        assertEquals("Invite link is successfully sent to the emails!",
                inviteParticipantCtrl.inviteResultText.getText());
        inviteParticipantCtrl.keyPressed(escKeyEvent);
        Mockito.verify(mainCtrl, times(1)).closePopupStage();
    }

    // @Override
    public void start(Stage stage) throws Exception {

    }
}