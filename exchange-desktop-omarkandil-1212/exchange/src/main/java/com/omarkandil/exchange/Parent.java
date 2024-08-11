package com.omarkandil.exchange;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
//This parent class navigates through all pages and transfers functionality to that page.
public class Parent implements Initializable, OnPageCompleteListener{
    public BorderPane borderPane;
    public Button transactionButton;
    public Button loginButton;
    public Button registerButton;
    public Button logoutButton;
    public Button userTransactionButton;
    public Button previousTransactionButton;
    public Button preferencesButton;
    @FXML
    private VBox navigationPanel;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navigationPanel.prefWidthProperty().bind(borderPane.widthProperty().multiply(0.20));
        navigationPanel.minWidthProperty().bind(borderPane.widthProperty().multiply(0.1));
        navigationPanel.prefHeightProperty().bind(borderPane.heightProperty());
        navigationPanel.minHeightProperty().bind(borderPane.heightProperty().multiply(0.1));
        borderPane.centerProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal instanceof Region center) {
                center.prefWidthProperty().bind(borderPane.widthProperty().multiply(0.80));
                center.prefHeightProperty().bind(borderPane.heightProperty());
                center.minHeightProperty().bind(borderPane.heightProperty().multiply(0.1));
            }
        });
        updateNavigation();
    }
    public void ratesSelected() {
        swapContent(Section.RATES);
    }
    public void transactionsSelected() {
        swapContent(Section.TRANSACTIONS);
    }
    public void loginSelected() {
        swapContent(Section.LOGIN);
    }
    public void registerSelected() {
        swapContent(Section.REGISTER);
    }
    public void statisticsSelected(){swapContent(Section.STATISTICS);}
    public void fluctuationsSelected(){swapContent(Section.FLUCTUATIONS);}
    public void userTransactionSelected(){swapContent(Section.USERTRANSACTIONS);}
    public void previousTransactionSelected(){swapContent(Section.PREVIOUSTRANSACTIONS);}
    public void preferencesSelected(){swapContent(Section.PREFERENCES);}
    public void machineLearningSelected(){swapContent(Section.MACHINELEARNING);}
    //This function logs out, clears the token and the accessibility boolean.
    public void logoutSelected() {
        SocketManager.emitEvent("remove_sid_java",Authentication.getInstance().getToken());
        Authentication.getInstance().deleteToken();
        SettingsManager.setAccessibilityEnabled(false);
        swapContent(Section.RATES);
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Logout successful");
        }
    }
    private void swapContent(Section section) {
        try {
            URL url = getClass().getResource(section.getResource());
            FXMLLoader loader = new FXMLLoader(url);
            borderPane.setCenter(loader.load());
            if (section.doesComplete()) {
                ((PageCompleter)
                        loader.getController()).setOnPageCompleteListener(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateNavigation();
    }
    private enum Section {
        RATES,
        TRANSACTIONS,
        LOGIN,
        REGISTER,
        STATISTICS,
        FLUCTUATIONS,
        USERTRANSACTIONS,
        PREVIOUSTRANSACTIONS,
        PREFERENCES,
        MACHINELEARNING;
        // This transfers to the page represented by the enum selected.
        public String getResource() {
            return switch (this) {
                case RATES ->
                        "/com/omarkandil/exchange/rates/rates.fxml";
                case TRANSACTIONS ->
                        "/com/omarkandil/exchange/transactions/transactions.fxml";
                case LOGIN ->
                        "/com/omarkandil/exchange/login/login.fxml";
                case REGISTER ->
                        "/com/omarkandil/exchange/register/register.fxml";
                case STATISTICS ->
                        "/com/omarkandil/exchange/statistics/statistics.fxml";
                case FLUCTUATIONS ->
                        "/com/omarkandil/exchange/fluctuations/Fluctuations.fxml";
                case USERTRANSACTIONS ->
                    "/com/omarkandil/exchange/userTransactions/userTransactions.fxml";
                case PREVIOUSTRANSACTIONS ->
                        "/com/omarkandil/exchange/previousTransactions/previousTransactions.fxml";
                case PREFERENCES ->
                        "/com/omarkandil/exchange/preferences/preferences.fxml";
                case MACHINELEARNING ->
                        "/com/omarkandil/exchange/machineLearning/machineLearning.fxml";
                default -> null;
            };
        }
        public boolean doesComplete() {
            return switch (this) {
                case LOGIN, REGISTER -> true;
                default -> false;
            };
        }
    }
    @Override
    public void onPageCompleted() {
        swapContent(Section.RATES);
    }
    private void updateNavigation() {
        boolean authenticated = Authentication.getInstance().getToken() !=
                null;
        transactionButton.setManaged(authenticated);
        transactionButton.setVisible(authenticated);
        loginButton.setManaged(!authenticated);
        loginButton.setVisible(!authenticated);
        registerButton.setManaged(!authenticated);
        registerButton.setVisible(!authenticated);
        logoutButton.setManaged(authenticated);
        logoutButton.setVisible(authenticated);
        userTransactionButton.setVisible(authenticated);
        userTransactionButton.setManaged(authenticated);
        previousTransactionButton.setVisible(authenticated);
        previousTransactionButton.setManaged(authenticated);
        preferencesButton.setVisible(authenticated);
        preferencesButton.setManaged(authenticated);

    }


}
