package com.omarkandil.exchange.machineLearning;

import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.ExchangeRateDate;
import com.omarkandil.exchange.api.model.ExchangeService;
import com.omarkandil.exchange.api.model.RatePrediction;
import com.omarkandil.exchange.api.model.SingleDate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.LocalDate;
import java.util.List;
// Function to predict the rate on a specific date.
public class MachineLearning implements PageCompleter {
    private OnPageCompleteListener onPageCompleteListener;
    @FXML
    private DatePicker datePicker;

    @FXML
    private VBox mainContainer;
    @FXML
    private Label predictedRateLabel;
    @FXML private Label titleLabel;
    @FXML private Label disclaimerLabel;
    @FXML private Label selectDateLabel;
    @FXML private Label predictedRateLabelHeader;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();

    public void initialize() {
        datePicker.setEditable(false);
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Predictions page");
        }
        mainContainer.widthProperty().addListener((obs, oldVal, newVal) -> updateFontSizes(mainContainer.getHeight()));
        mainContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateFontSizes(mainContainer.getWidth()));
    }
    // Update the font sizes dynamically as a response to an event listener.
    private void updateFontSizes(double size) {
        double baseSize = Math.min(16, size / 50);
        double headerSize = baseSize +4;
        double sectionHeaderSize = baseSize +2;
        String baseFontSizeStyle = "-fx-font-size: " + baseSize + "pt;";
        String headerFontSizeStyle = "-fx-font-size: " + headerSize + "pt;";
        String sectionHeaderFontSizeStyle = "-fx-font-size: " + sectionHeaderSize + "pt;";

        titleLabel.setStyle(sectionHeaderFontSizeStyle);
        disclaimerLabel.setStyle(headerFontSizeStyle);
        selectDateLabel.setStyle(baseFontSizeStyle);
        predictedRateLabel.setStyle(baseFontSizeStyle);
        predictedRateLabelHeader.setStyle(headerFontSizeStyle);
    }
    // Takes in the date and predicts the rate on that date.
    @FXML
    private void getPrediction(ActionEvent actionEvent) {
        LocalDate date = datePicker.getValue();
        if (date==null){
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Must enter valid date");
            });
            return;
        }
        if (date.isBefore(LocalDate.now())){
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Must enter a future date");
            });
            return;
        }
        SingleDate dateInput = new SingleDate(date);
        ExchangeService.exchangeApi().getPredictions(
                dateInput.year.toString(),
                dateInput.month.toString(),
                dateInput.day.toString()
        ).enqueue(new Callback<RatePrediction>() {
            @Override
            public void onResponse(Call<RatePrediction> call, Response<RatePrediction> response) {
                RatePrediction ratePrediction= response.body();
                if (ratePrediction!=null) {
                    Double predictedRate = ratePrediction.rate;
                    Platform.runLater(() -> {
                        predictedRateLabel.setText(predictedRate != null ? String.format("%.2f", predictedRate) : "No data yet");
                    });
                    if (SettingsManager.isAccessibilityEnabled()) {
                        voiceOverService.speak(predictedRate != null ? "predicted rate is " + String.format("%.2f", predictedRate) : "No data yet");
                    }
                }
                else{
                    Platform.runLater(() -> {
                        predictedRateLabel.setText("Unavailable");
                    });
                }
            }
            @Override
            public void onFailure(Call<RatePrediction> call, Throwable throwable) {
                Platform.runLater(() -> {
                    Alerts.displayErrorMessage("Request could not be completed");
                });
            }
        });
    }

    public void setOnPageCompleteListener(OnPageCompleteListener
                                                  onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }
}
