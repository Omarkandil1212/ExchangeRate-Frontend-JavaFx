package com.omarkandil.exchange;

import io.socket.client.IO;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;
import org.json.JSONObject;
import io.socket.client.Socket;

// This class manages the socket communication with the backend.
public class SocketManager {
    private static Socket socket;
    private static final String SERVER_URL = "http://127.0.0.1:5000";
    private static VoiceOverService voiceOverService = VoiceOverService.getInstance();

    // This function establishes the connection with the backend socket.
    public static void connect() {
        try {
            socket = IO.socket(SERVER_URL);
            socket.on(Socket.EVENT_CONNECT, args -> System.out.println("Connected to server!"));
            socket.on("notification", args -> handleNotification(args));
            socket.connect();
        } catch (Exception e) {
            Alerts.displayErrorMessage("Failed to connect to server socket");
        }
    }

    //This function displays a notification.
    private static void handleNotification(Object[] args) {
        try {
            JSONObject data = (JSONObject) args[0];
            String title = data.optString("Title", "Default Title");
            String message = data.optString("Message", "No message provided.");
            Platform.runLater(() -> Notifications.create()
                    .title(title)
                    .text(message)
                    .hideAfter(javafx.util.Duration.seconds(10))
                    .showInformation());
            if (SettingsManager.isAccessibilityEnabled()) {
                voiceOverService.speak(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This function is used to emit an event to the backend for token communication.
    public static void emitEvent(String event, String token) {
        if (socket != null) {
            socket.emit(event, token);
        }
    }

    public static void disconnect() {
        if (socket != null) {
            socket.disconnect();
        }
    }
}
