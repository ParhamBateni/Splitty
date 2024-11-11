# OOPP instructions
# Project execution
- The server url can be initially set in the client.properties file.
- By using gradle run, client execution is performed to initialize the client side of the project.
- By using gradle bootrun, the server is then started.
# Email notifications
- Upon launching the app navigate to the settings page by clicking on the gear icon between the language flag and information icon. There you will be given the choice to input your email. This email will be used to send notifications. The system supports outlook and gmail based emails.
- For an Outlook email you just need to enter your email and your password. However, if using Gmail you would have to create a special app password that is required because of security concerns.
- To do so, go to the security page of your gmail account, then you need to enable 2-step verification, if you already have, or when you have done it, head to its page and scroll to the bottom of it. There, you will find a menu to create an app password, in that menu, you need to choose a name for the app and then you will be given the app password. Use this password instead of your standard email one.

# Communication technology
- The long-polling technology is mainly added to the OverviewCtrl.java file when the event overview is shown to update the seen expenses, participants and event title.
- WebSockets are used in the Admin page (found in AdminCtrl.java) to update the table of event contents and to propagate changes.    
- Backend configuration is dispersed among the event, participant, and expense controllers.

# HCI Features
- Keyboard shortcuts are added into the application, there are several info buttons offering information on the available buttons for interaction.
- In multi-modal fashion there are elements such as language selection buttons indicating the selection with text accompanying them.
- Confirmation messages are available; for instance, when deleting an event.
- There is informative feedback, such as tooltips, failed entry/successful action confirmations; for example, when invites are sent, expenses are added and participants are added.
- There are error messages for invalid entries. Also, the server tries to reconnect whenever it is disconnected.

