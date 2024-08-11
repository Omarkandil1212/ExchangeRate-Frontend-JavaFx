package com.omarkandil.exchange;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import java.util.TimeZone;

// This is the function which launches the app.
public class Main extends Application {
    //Here, I set the page sizes and limits
    @Override
    public void start(Stage stage) throws IOException {
        SocketManager.connect();
        SocketManager.emitEvent("send_token_java",Authentication.getInstance().getToken());
        FXMLLoader fxmlLoader = new
                FXMLLoader(Main.class.getResource("parent.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 525);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("notification-styles.css")).toExternalForm());
        stage.setTitle("Currency Exchange");
        stage.setMinWidth(450);
        stage.setMinHeight(450);
        stage.setScene(scene);
        stage.show();

    }
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Beirut"));
        launch();
    }
    @Override
    public void stop() throws Exception {
        SocketManager.disconnect();
        super.stop();
    }
}