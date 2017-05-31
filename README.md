# TestClientServer

There are four projects here. 
The TestServer is a multithreaded server for the Hospital Broadcast Server.
The TestGroupClient is the group client application for Hospital room status change emulation.
The TestClient is a smart phone application for receive the broadcasting message from the Hospital Broadcast Server.
The TestClient2 is a smart phone application for receive the broadcasting message from the Hospital Broadcast Server, and send the message to notification status bar line.

TestServer is a pure java project run on PC. Run on a console by using java -jar TestServer.jar [port]

TestGroupClient is a pure java project run on PC. Run on a console by using java -jar TestGroupClient.jar serverIP[:serverPort] groupId

TestClient/TestClient2 are android application created by Android Studio IDE. Run on the smart phone with android version 5.1 or above.

The TestServer and TestGroupClient executable jar files has been put to Outputs folder.


