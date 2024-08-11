package com.omarkandil.exchange.login;

import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.ExchangeService;
import com.omarkandil.exchange.api.model.Token;
import com.omarkandil.exchange.api.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
//Class for logging in.
public class Login implements PageCompleter {
    public TextField usernameTextField;
    public PasswordField passwordTextField;
    private OnPageCompleteListener onPageCompleteListener;
    @FXML
    private Label  usernameLabel, passwordLabel;

    @FXML
    private Label titleLabel;
    @FXML
    private Button loginButton;
    @FXML
    private FlowPane mainContainer;

    public void initialize() {
        setupDynamicStyling();
    }

    private void setupDynamicStyling() {
        mainContainer.widthProperty().addListener((observable, oldValue, newValue) -> updateStyling(newValue.doubleValue()));
        mainContainer.heightProperty().addListener((observable, oldValue, newValue) -> updateStyling(newValue.doubleValue()));
    }
    // Dynamically updating the styling.
    private void updateStyling(double size) {
        double baseFontSize = Math.min(16, size / 50);
        titleLabel.setStyle("-fx-font-size: " + (baseFontSize + 2) + "pt;");
        usernameLabel.setStyle("-fx-font-size: " + baseFontSize + "pt;");
        passwordLabel.setStyle("-fx-font-size: " + baseFontSize + "pt;");
        usernameTextField.setStyle("-fx-font-size: " + baseFontSize + "pt;");
        passwordTextField.setStyle("-fx-font-size: " + baseFontSize + "pt;");
        loginButton.setStyle("-fx-font-size: " + baseFontSize + "pt;");
    }
    //Function to login with username and password. Als saves token for this user.
    public void login(ActionEvent actionEvent) {
        User user = new User(usernameTextField.getText(),
                passwordTextField.getText());
        ExchangeService.exchangeApi().authenticate(user).enqueue(new
             Callback<Token>() {
                 @Override
                 public void onResponse(Call<Token> call, Response<Token>
                         response) {

                     if (response.isSuccessful() && response.body() != null) {
                         Authentication.getInstance().saveToken(response.body().getToken());
                         Platform.runLater(() -> {
                             onPageCompleteListener.onPageCompleted();
                         });
                         SocketManager.emitEvent("send_token_java",Authentication.getInstance().getToken());
                     } else {
                         Platform.runLater(() -> {
                             Alerts.displayErrorMessage("Authentication failed. Please check your username and password.");
                         });
                     }

                 }
                 @Override
                 public void onFailure(Call<Token> call, Throwable throwable) {
                     Platform.runLater(() -> {
                         Alerts.displayErrorMessage("Authentication failed. Please try again later.");
                     });
                 }
             });
    }
    public void setOnPageCompleteListener(OnPageCompleteListener
                                                  onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }

}
