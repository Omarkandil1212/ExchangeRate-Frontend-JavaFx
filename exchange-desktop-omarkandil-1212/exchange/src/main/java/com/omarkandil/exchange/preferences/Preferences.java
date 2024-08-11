package com.omarkandil.exchange.preferences;

import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.BalanceResponse;
import com.omarkandil.exchange.api.model.ExchangeService;
import com.omarkandil.exchange.api.model.UserPreferences;
import com.omarkandil.exchange.api.model.UserTransaction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class is to save the users preferences.
public class Preferences implements PageCompleter {
    @FXML private  Label bound1;
    @FXML private  Label bound2;
    @FXML private  Label bound3;
    @FXML private  Label bound4;
    private OnPageCompleteListener onPageCompleteListener;
    @FXML private GridPane gridPane;
    @FXML private Label titleLabel;
    @FXML private Label sectionHeader1;
    @FXML private Label sectionHeader3;
    @FXML private TextField upperBoundUsdToLbp;
    @FXML private TextField lowerBoundUsdToLbp;
    @FXML private TextField upperBoundLbpToUsd;
    @FXML private TextField lowerBoundLbpToUsd;
    @FXML private CheckBox sendEmail;
    @FXML private TextField email;
    @FXML private CheckBox sendSms;
    @FXML private TextField phoneNumber;
    @FXML private Button saveButton;
    @FXML private CheckBox updateBoundsCheckbox;
    @FXML private CheckBox updateEmailCheckbox;
    @FXML private CheckBox updateSmsCheckbox;
    @FXML private CheckBox updateAccessibility;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();

    //Add event listeners for the page size and initialise the fields with the users existing preferences.
    public void initialize() {
        setFields();
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Settings page");
        }
        gridPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = newValue.doubleValue();
            updateFontSizes(newWidth);
        });

        gridPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = newValue.doubleValue();
            updateFontSizes(newHeight);
        });
        updateAccessibility.setSelected(SettingsManager.isAccessibilityEnabled());
    }

    //Update the fonts dynamically.
    private void updateFontSizes(double size) {
        double baseSize = Math.min(14, size / 50);
        double headerSize = baseSize +4;
        double sectionHeaderSize = baseSize +2;
        String baseFontSizeStyle = "-fx-font-size: " + baseSize + "pt;";
        String headerFontSizeStyle = "-fx-font-size: " + headerSize + "pt;";
        String sectionHeaderFontSizeStyle = "-fx-font-size: " + sectionHeaderSize + "pt;";

        titleLabel.setStyle(headerFontSizeStyle);
        sectionHeader1.setStyle(sectionHeaderFontSizeStyle);
        sectionHeader3.setStyle(sectionHeaderFontSizeStyle);
        applyStyleToTextFields(baseFontSizeStyle);
        sendEmail.setStyle(baseFontSizeStyle);
        sendSms.setStyle(baseFontSizeStyle);
        updateEmailCheckbox.setStyle(baseFontSizeStyle);
        updateSmsCheckbox.setStyle(baseFontSizeStyle);
        updateBoundsCheckbox.setStyle(baseFontSizeStyle);
        updateAccessibility.setStyle(baseFontSizeStyle);
        saveButton.setStyle(baseFontSizeStyle);
        lowerBoundLbpToUsd.setStyle(baseFontSizeStyle);
        lowerBoundUsdToLbp.setStyle(baseFontSizeStyle);
        upperBoundLbpToUsd.setStyle(baseFontSizeStyle);
        upperBoundUsdToLbp.setStyle(baseFontSizeStyle);
        bound1.setStyle(baseFontSizeStyle);
        bound2.setStyle(baseFontSizeStyle);
        bound3.setStyle(baseFontSizeStyle);
        bound4.setStyle(baseFontSizeStyle);
    }
    private void applyStyleToTextFields(String style) {
        upperBoundUsdToLbp.setStyle(style);
        lowerBoundUsdToLbp.setStyle(style);
        upperBoundLbpToUsd.setStyle(style);
        lowerBoundLbpToUsd.setStyle(style);
        email.setStyle(style);
        phoneNumber.setStyle(style);
    }
    // Function to parse double or return null if failed.
    private Double safelyParseDouble(String input) {
        try {
            return input.isEmpty() ? null : Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }
    //This function sets the fields to the users current preference choices.
    private void setFields() {
        ExchangeService.exchangeApi().getPreferences("Bearer " + Authentication.getInstance().getToken())
                .enqueue(new Callback<UserPreferences>() {
                    @Override
                    public void onResponse(Call<UserPreferences> call, Response<UserPreferences> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserPreferences prefs = response.body();
                            Platform.runLater(() -> updateFields(prefs));
                        } else {
                            Platform.runLater(() -> Alerts.displayErrorMessage("Failed to fetch preferences"));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserPreferences> call, Throwable throwable) {
                        Platform.runLater(() -> Alerts.displayErrorMessage("Failed to fetch preferences: " + throwable.getMessage()));
                    }
                });
    }

    private void updateFields(UserPreferences prefs) {
        upperBoundUsdToLbp.setText(doubleToString(prefs.upperBoundUsdToLbp));
        lowerBoundUsdToLbp.setText(doubleToString(prefs.lowerBoundUsdToLbp));
        upperBoundLbpToUsd.setText(doubleToString(prefs.upperBoundLbpToUsd));
        lowerBoundLbpToUsd.setText(doubleToString(prefs.lowerBoundLbpToUsd));
        email.setText(prefs.email != null ? prefs.email : "");
        phoneNumber.setText(prefs.phoneNumber != null ? prefs.phoneNumber : "");
        sendEmail.setSelected(prefs.sendEmail != null && prefs.sendEmail);
        sendSms.setSelected(prefs.sendSms != null && prefs.sendSms);
        updateEmailCheckbox.setSelected(false);
        updateSmsCheckbox.setSelected(false);
        updateBoundsCheckbox.setSelected(false);
    }

    private String doubleToString(Double value) {
        return value != null ? String.format("%.2f", value) : "";
    }

    // This user saves the entered preferences when the user clicks save.
    @FXML
    private void savePreferences(ActionEvent actionEvent) {
        try {
            if (sendEmail.isSelected() && (email.getText() == null || email.getText().trim().isEmpty())) {
                Platform.runLater(() -> {
                    Alerts.displayErrorMessage("Email address must be provided when email notifications are enabled.");
                });
                setFields();
                return;
            }
            if (sendSms.isSelected() && (phoneNumber.getText() == null || phoneNumber.getText().trim().isEmpty())) {
                Platform.runLater(() -> {
                    Alerts.displayErrorMessage("Phone number must be provided when SMS notifications are enabled.");
                });
                setFields();
                return;
            }
            if (email.getText()!=null && !email.getText().trim().isEmpty()){
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher(email.getText());
                if (!matcher.matches()){
                    Platform.runLater(() -> {
                        Alerts.displayErrorMessage("Invalid email.");
                    });
                    setFields();
                    return;
                }
            }

            try {
                Double upperUSD = safelyParseDouble(upperBoundUsdToLbp.getText());
                Double lowerUSD = safelyParseDouble(lowerBoundUsdToLbp.getText());
                Double upperLBP = safelyParseDouble(upperBoundLbpToUsd.getText());
                Double lowerLBP = safelyParseDouble(lowerBoundLbpToUsd.getText());

                if ((upperLBP != null && (upperLBP > 1000000000 || upperLBP < 100)) ||
                        (lowerLBP != null && (lowerLBP > 1000000000 || lowerLBP < 100)) ||
                        (upperUSD != null && (upperUSD > 1000000000 || upperUSD < 100)) ||
                        (lowerUSD != null && (lowerUSD > 1000000000 || lowerUSD < 100))) {
                    Platform.runLater(() -> {
                        Alerts.displayErrorMessage("Invalid amounts.");
                    });
                    setFields();
                    return;
                }
                if ((upperLBP != null && lowerLBP != null && upperLBP < lowerLBP) ||
                        (upperUSD != null && lowerUSD != null && upperUSD < lowerUSD)) {
                    Platform.runLater(() -> {
                        Alerts.displayErrorMessage("Lower bound can't be higher than upper bound.");
                    });
                    setFields();
                    return;
                }

                UserPreferences up=new UserPreferences(upperUSD,lowerUSD,upperLBP,lowerLBP,sendEmail.isSelected(),email.getText(),sendSms.isSelected(),phoneNumber.getText(),
                        updateSmsCheckbox.isSelected(),updateEmailCheckbox.isSelected(),updateBoundsCheckbox.isSelected());
                ExchangeService.exchangeApi().changePreferences(up,"Bearer " +
                                Authentication.getInstance().getToken())
                        .enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                Platform.runLater(() -> {
                                    Alerts.displayInfoMessage("Preferences saved successfully!");
                                });
                                SettingsManager.setAccessibilityEnabled(updateAccessibility.isSelected());
                                if (!SettingsManager.isAccessibilityEnabled()){
                                    voiceOverService.stopSpeaking();
                                }
                                setFields();
                            }
                            @Override
                            public void onFailure(Call<Object> call, Throwable throwable) {
                                Platform.runLater(() -> {
                                    Alerts.displayErrorMessage("Failed to make updates.");
                                });
                                setFields();
                            }
                        });

            } catch (Exception e) {
                Platform.runLater(() -> Alerts.displayErrorMessage("Invalid input. Bounds must be positive floats"));
                setFields();
            }



        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Invalid inputs.");
            });
        }
        setFields();
    }


    public void setOnPageCompleteListener(OnPageCompleteListener
                                                  onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }
}
