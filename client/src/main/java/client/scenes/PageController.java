package client.scenes;

import client.SplittyConfig;
import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;


public abstract class PageController {

    protected ServerUtils server;
    protected MainCtrl mainCtrl;
    protected SplittyConfig splittyConfig;


    /***
     * Constructor for page controller used by injector
     * @param server
     * @param mainCtrl
     */
    @Inject
    public PageController(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;

//        serverAvailabilityChecker = new ServerAvailabilityChecker(server, mainCtrl);
//        serverAvailabilityChecker.startChecking();
    }

    @Inject
    public PageController(ServerUtils server, MainCtrl mainCtrl, SplittyConfig splittyConfig) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.splittyConfig = splittyConfig;

//        serverAvailabilityChecker = new ServerAvailabilityChecker(server, mainCtrl);
//        serverAvailabilityChecker.startChecking();
    }

    /***
     * Refresh the page both contents and language
     * @param args
     */
    public void refreshPage(Object... args) {
        clearFields();
        refreshContent(args);
        refreshLanguage();
    }

    /***
     * Refresh the page content and reload the content
     * @param args
     */
    public abstract void refreshContent(Object... args);

    public abstract void refreshLanguage();

    /***
     * Clear all the fields of a page used specially when refreshing a page
     */
    public abstract void clearFields();

    /***
     * Event handler for handling going back from a page to the previous page
     * @param actionEvent
     */
    public void goBack(ActionEvent actionEvent) {
        keyPressed(new KeyEvent(actionEvent.getSource(), actionEvent.getTarget(),
                null, null, null, KeyCode.ESCAPE, false,
                false, false, false));
    }

    /***
     * Event handler for handling when keys are entered on page
     * @param keyEvent
     */
    public abstract void keyPressed(KeyEvent keyEvent);
}
