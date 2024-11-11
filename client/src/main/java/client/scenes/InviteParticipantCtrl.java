package client.scenes;

import client.utils.ServerUtils;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class InviteParticipantCtrl extends PageController {
    @FXML
    public TextField inviteCodeText;
    @FXML
    public TextArea emailTextArea;
    @FXML
    public Text inviteResultText;
    @FXML
    public Text eventNameText;

    @FXML
    public Text inviteTitle;

    @FXML
    public Text offerCodeText;

    @FXML
    public Text inviteEmailText;

    @FXML
    public Button okButton;

    @FXML
    private Button cancelButton;

    private String email;
    private String password;

    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public InviteParticipantCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }


    /***
     * read the list of emails and send an invite to the event to the emails
     * @param actionEvent
     */
    public void sendInviteToEmails(ActionEvent actionEvent) {
        List<String> emails = Arrays.stream(emailTextArea.getText().trim().split("\n"))
                .map(String::trim).toList();
        if (emails.stream().allMatch(Participant::isEmailValid)) {
            try {
                server.sendInviteToEmails(emails, mainCtrl.currentEvent, email, password);
                addParticipants(emails);
                inviteResultText.setFill(Color.GREEN);
                inviteResultText.setText(
                        mainCtrl.getPreferedLanguage().getText("inviteSentSuccess"));
            } catch (Exception e) {
                inviteResultText.setFill(Color.RED);
                inviteResultText.setText(mainCtrl.getPreferedLanguage().getText("cantSendInvite")
                        + "!\n" +
                        mainCtrl.getPreferedLanguage().getText("setupEmailDetails"));
            }

        } else {
            inviteResultText.setFill(Color.RED);
            inviteResultText.setText(
                    mainCtrl.getPreferedLanguage().getText("emailPattern") +
                            Participant.emailPattern);
        }
    }

    /**
     * adds all emails as participants of the event
     *
     * @param emails list of emails to be addedg
     */
    public void addParticipants(List<String> emails) {
        String last = "";
        for (String email : emails) {
            if (email != last) {
                Participant participant = new Participant(email, mainCtrl.currentEvent);
                System.out.println(participant.toString());
                server.addParticipant(participant);
                mainCtrl.currentEvent.addParticipant(participant);
                last = email;
            }
        }
    }


    /***
     * Refresh the page and reload the content
     * @param args
     */
    @Override
    public void refreshContent(Object... args) {
        long eventId = (long) args[0];
        email = (String) args[1];
        password = (String) args[2];
        mainCtrl.currentEvent = server.getEventById(eventId);
        if (mainCtrl.currentEvent != null) {
            eventNameText.setText(mainCtrl.currentEvent.getTitle());
            inviteCodeText.setText(mainCtrl.currentEvent.getLink());
        } else {
            System.out.println("The event is not found in the database! " +
                    "It may have just been removed by other user!");
            mainCtrl.showStart();
        }
        okButton.setDisable(false);
//        if (Participant.isEmailValid(email)) {
//            okButton.setDisable(false);
//        } else {
//            okButton.setDisable(true);
//        }
    }

    @Override
    public void refreshLanguage() {
        inviteTitle.setText(mainCtrl.getPreferedLanguage().getText("inviteTitle"));
        offerCodeText.setText(mainCtrl.getPreferedLanguage().getText("offerCode"));
        inviteEmailText.setText(mainCtrl.getPreferedLanguage().getText("inviteEmail"));
        okButton.setText(mainCtrl.getPreferedLanguage().getText("invite"));
        cancelButton.setText(mainCtrl.getPreferedLanguage().getText("cancel"));
    }

    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    @Override
    public void clearFields() {
        emailTextArea.clear();
        inviteResultText.setText("");
        emailTextArea.requestFocus();
    }

    /***
     * Event handler for handling when keys are entered on page
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ESCAPE:
                mainCtrl.closePopupStage();
                break;
            case ENTER:
                if (keyEvent.isControlDown()) {
                    sendInviteToEmails(new ActionEvent());
                }
                break;
        }
    }
}
