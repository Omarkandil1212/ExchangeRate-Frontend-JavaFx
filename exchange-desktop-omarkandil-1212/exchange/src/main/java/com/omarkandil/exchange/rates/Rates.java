package com.omarkandil.exchange.rates;

import com.omarkandil.exchange.Alerts;
import com.omarkandil.exchange.Authentication;
import com.omarkandil.exchange.SettingsManager;
import com.omarkandil.exchange.VoiceOverService;
import com.omarkandil.exchange.api.model.ExchangeRates;
import com.omarkandil.exchange.api.model.ExchangeService;
import com.omarkandil.exchange.api.model.Transaction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This class represents the page to add transactions and view the rates. Also supports the conversion calculator.
public class Rates {
    public Label buyUsdRateLabel;
    public Label sellUsdRateLabel;
    public TextField lbpTextField;
    public TextField usdTextField;
    public TextField Input;
    public ToggleGroup transactionType;
    public ToggleGroup tType;
    public Label convAmount;
    public double buyusd;
    public double sellusd;
    @FXML
    private GridPane gridPane;
    @FXML private Label usdAmountLabel;
    @FXML private Label lbpAmountLabel;
    @FXML private Button convertButton;
    @FXML private Button addTransactionButton;
    @FXML private Label exchangeRateHeader;
    @FXML private Label buyUsdRateLabelHeader;
    @FXML private Label sellUsdRateLabelHeader;
    @FXML private RadioButton buyUsdRadioButton;
    @FXML private RadioButton sellUsdRadioButton;
    @FXML private Label rateCalculatorHeader;
    @FXML private Label inputAmountLabel;
    @FXML private RadioButton usdRadioButton;
    @FXML private RadioButton lbpRadioButton;
    @FXML private Label convertedAmountLabel;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();
    public void initialize() {
        fetchRates();
        setupDynamicFontResizing();
    }
    // Used for dynamic font styling.
    private void setupDynamicFontResizing() {
        gridPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = newValue.doubleValue();
            updateFontSizes(newWidth);
        });

        gridPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = newValue.doubleValue();
            updateFontSizes(newHeight);
        });
    }

    private void updateFontSizes(double size) {
        double baseSize = Math.min(12, size / 50);
        String fontSizeStyle = "-fx-font-size: " + baseSize + "pt;";
        String fontSizeStyle2 = "-fx-font-size: " + (baseSize+2) + "pt;";

        exchangeRateHeader.setStyle(fontSizeStyle2);
        buyUsdRateLabelHeader.setStyle(fontSizeStyle2);
        buyUsdRateLabel.setStyle(fontSizeStyle);
        sellUsdRateLabelHeader.setStyle(fontSizeStyle2);
        sellUsdRateLabel.setStyle(fontSizeStyle);
        usdAmountLabel.setStyle(fontSizeStyle);
        lbpAmountLabel.setStyle(fontSizeStyle);
        usdTextField.setStyle(fontSizeStyle);
        lbpTextField.setStyle(fontSizeStyle);
        buyUsdRadioButton.setStyle(fontSizeStyle);
        sellUsdRadioButton.setStyle(fontSizeStyle);
        addTransactionButton.setStyle(fontSizeStyle);
        rateCalculatorHeader.setStyle(fontSizeStyle2);
        inputAmountLabel.setStyle(fontSizeStyle);
        Input.setStyle(fontSizeStyle);
        usdRadioButton.setStyle(fontSizeStyle);
        lbpRadioButton.setStyle(fontSizeStyle);
        convertButton.setStyle(fontSizeStyle);
        convertedAmountLabel.setStyle(fontSizeStyle);
        convAmount.setStyle(fontSizeStyle);

    }
    //This function gets the current rates and displays them on the page.

    private void fetchRates() {
        ExchangeService.exchangeApi().getExchangeRates().enqueue(new
         Callback<ExchangeRates>() {
             @Override
             public void onResponse(Call<ExchangeRates> call,
                                    Response<ExchangeRates> response) {
                 ExchangeRates exchangeRates = response.body();
                 Platform.runLater(() -> {
                     if (exchangeRates != null) {
                         buyUsdRateLabel.setText(exchangeRates.lbpToUsd != null ? String.format("%.2f",exchangeRates.lbpToUsd) : "No data yet");
                         sellUsdRateLabel.setText(exchangeRates.usdToLbp != null ? String.format("%.2f",exchangeRates.usdToLbp) : "No data yet");
                         buyusd = (exchangeRates.lbpToUsd != null ? exchangeRates.lbpToUsd : 0);
                         sellusd = (exchangeRates.usdToLbp != null ? exchangeRates.usdToLbp : 0);
                     } else {
                         buyusd=0;
                         sellusd=0;
                         buyUsdRateLabel.setText("No data yet");
                         sellUsdRateLabel.setText("No data yet");

                     }
                 });
                 if (SettingsManager.isAccessibilityEnabled()){
                     voiceOverService.speak("buy U S D rate is "+(exchangeRates.lbpToUsd!=0? String.format("%.2f",exchangeRates.lbpToUsd): "No data yet") +
                             ".  Sell U S D rate is "+ (exchangeRates.usdToLbp!=0? String.format("%.2f",exchangeRates.usdToLbp): "No data yet"));
                 }
             }
             @Override
             public void onFailure(Call<ExchangeRates> call, Throwable throwable) {
                 Platform.runLater(() -> {
                     Alerts.displayErrorMessage("Failed to fetch rates");
                     if (SettingsManager.isAccessibilityEnabled()) {
                         voiceOverService.speak("No data yet");
                     }
                 });
             }
         });
    }

    //This function takes in the user inputted transaction and sends it to the database.
    public void addTransaction(ActionEvent actionEvent) {
        try {
            if (Double.parseDouble(usdTextField.getText())<0.0001 ||Double.parseDouble(lbpTextField.getText())<100
            || Double.parseDouble(usdTextField.getText())>10000000 ||Double.parseDouble(lbpTextField.getText())>1000000000){
                throw new IllegalArgumentException("Invalid amounts");
            }
            Transaction transaction = new Transaction(
                    Double.parseDouble(usdTextField.getText()),
                    Double.parseDouble(lbpTextField.getText()),
                    ((RadioButton) transactionType.getSelectedToggle()).getText().equals("Sell USD")
            );
            String userToken = Authentication.getInstance().getToken();
            String authHeader = userToken != null ? "Bearer " + userToken : null;
            ExchangeService.exchangeApi().addTransaction(transaction, authHeader).enqueue(new
          Callback<Object>() {
              @Override
              public void onResponse(Call<Object> call, Response<Object>
                      response) {

                  Platform.runLater(() -> {
                      fetchRates();
                      usdTextField.setText("");
                      lbpTextField.setText("");
                      transactionType.selectToggle(null);
                  });
              }

              @Override
              public void onFailure(Call<Object> call, Throwable throwable) {
                  Platform.runLater(() -> {
                      Alerts.displayErrorMessage("Failed to add Transaction");
                  });
                  usdTextField.setText("");
                  lbpTextField.setText("");
                  transactionType.selectToggle(null);
              }
          });
        } catch(Exception e){
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Failed to add Transaction. Invalid input amounts.");
            });
            usdTextField.setText("");
            lbpTextField.setText("");
            transactionType.selectToggle(null);
        }
    }
    // This function converts an ammount in a selected rate to the other rate.
    public void Convert(ActionEvent actionEvent) {
        try{

            if (((RadioButton) tType.getSelectedToggle()).getText().equals("USD")){
                if (Double.parseDouble(Input.getText())<0.0001 || Double.parseDouble(Input.getText())>10000000){
                    throw new IllegalArgumentException("amount must be positive and not too large");
                }
                if (sellusd==0){
                    convAmount.setText("No conversion yet");
                }
                else {
                    Double val = Double.parseDouble(Input.getText()) * sellusd;
                    convAmount.setText(String.format("%.2f",val) + " LBP");
                }
            }
            else{
                if (Double.parseDouble(Input.getText())<10 || Double.parseDouble(Input.getText())>1000000000){
                    throw new IllegalArgumentException("amount must be positive and not too large");
                }
                if (buyusd==0){
                    convAmount.setText("No conversion yet");
                }
                else {
                    Double val = Double.parseDouble(Input.getText()) / buyusd;
                    convAmount.setText(String.format("%.2f",val) + " USD");
                }
            }
            Input.setText("");
            tType.selectToggle(null);

        }catch(Exception e){
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Conversion Failed. Invalid input amounts.");
            });
            Input.setText("");
            tType.selectToggle(null);
        }
    }

}