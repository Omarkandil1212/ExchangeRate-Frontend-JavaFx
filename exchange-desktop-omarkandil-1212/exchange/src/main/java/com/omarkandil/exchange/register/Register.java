package com.omarkandil.exchange.register;

import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// This class is for the register page.
public class Register implements PageCompleter {

    private OnPageCompleteListener onPageCompleteListener;
    @FXML private FlowPane mainContainer;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private TextField usernameTextField;
    @FXML private Label passwordLabel;
    @FXML
    private PasswordField passwordTextField;
    @FXML private Button createAccountButton;

    public void initialize() {
        setupDynamicFontResizing();
    }
    // Dynamic font restyling.
    private void setupDynamicFontResizing() {
        mainContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = newValue.doubleValue();
            updateFontSizes(newWidth);
        });

        mainContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = newValue.doubleValue();
            updateFontSizes(newHeight);
        });
    }

    private void updateFontSizes(double size) {
        double baseSize = Math.min(16, size / 50);
        double titleSize = baseSize + 4;
        String baseFontSizeStyle = "-fx-font-size: " + baseSize + "px;";
        String titleFontSizeStyle = "-fx-font-size: " + titleSize + "px;";

        titleLabel.setStyle(titleFontSizeStyle);
        usernameLabel.setStyle(baseFontSizeStyle);
        passwordLabel.setStyle(baseFontSizeStyle);
        usernameTextField.setStyle(baseFontSizeStyle);
        passwordTextField.setStyle(baseFontSizeStyle);
        createAccountButton.setStyle(baseFontSizeStyle);
    }

    // This function registers the user with their given username and password if they are valid.
    public void register(ActionEvent actionEvent) {
        User user = new User(usernameTextField.getText(),
                passwordTextField.getText());
        ExchangeService.exchangeApi().addUser(user).enqueue(new
            Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User>
                        response) {
                    if (response.isSuccessful()) {
                        ExchangeService.exchangeApi().authenticate(user).enqueue(new
                         Callback<Token>() {
                             @Override
                             public void onResponse(Call<Token> call,
                                                    Response<Token> response) {
                                 if (response.isSuccessful() && response.body() != null) {
                                     Authentication.getInstance().saveToken(response.body().getToken());
                                     Platform.runLater(() -> {
                                         onPageCompleteListener.onPageCompleted();
                                     });
                                     SocketManager.emitEvent("send_token_java",Authentication.getInstance().getToken());
                                 } else {
                                     Platform.runLater(() -> {
                                         Alerts.displayErrorMessage("Authentication failed. Username or password are invalid.");
                                     });
                                 }
                             }

                             @Override
                             public void onFailure(Call<Token> call, Throwable
                                     throwable) {
                                 Platform.runLater(() -> {
                                     Alerts.displayErrorMessage("Authentication failed. Please try again later.");
                                 });
                             }
                         });
                    } else {
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Registration failed. Username is invalid or password is weak.");
                        });
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    Platform.runLater(() -> {
                        Alerts.displayErrorMessage("Registration failed. Please try again later.");
                    });
                }
            });
    }

    public void setOnPageCompleteListener(OnPageCompleteListener
                                                  onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }

}

