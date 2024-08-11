package com.omarkandil.exchange.fluctuations;

import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.DateInput;
import com.omarkandil.exchange.api.model.ExchangeRateDate;
import com.omarkandil.exchange.api.model.ExchangeService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
//This class is for the fluctuations graph.
public class Fluctuations implements PageCompleter {
    private OnPageCompleteListener onPageCompleteListener;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private LineChart<String, Number> exchangeRateChartUSDtoLBP;
    @FXML
    private LineChart<String, Number> exchangeRateChartLBPtoUSD;

    @FXML
    private CategoryAxis xAxis1;
    @FXML
    private CategoryAxis xAxis2;
    @FXML
    private VBox mainContainer;
    @FXML
    private Button getData;
    @FXML
    HBox hbox;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();
    // Initialise the page to display the graph across the whole period of recording. Add event listeners to respond
    // to size changes.
    public void initialize() {
        startDatePicker.setEditable(false);
        endDatePicker.setEditable(false);
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Fluctuations page");
        }
        setupChart();
        loadChartData();
        mainContainer.widthProperty().addListener((obs, oldVal, newVal) -> adjustChartSize((Double) newVal, mainContainer.getHeight()));
        mainContainer.heightProperty().addListener((obs, oldVal, newVal) -> adjustChartSize(mainContainer.getWidth(), (Double) newVal));
    }
    // This function is to dynamically change the chart size as the page size varies.
    private void adjustChartSize(double width, double height) {
        double newFontSize = Math.max(10, width / 100);
        xAxis1.setStyle("-fx-font-size: " + newFontSize + "px;");
        xAxis2.setStyle("-fx-font-size: " + newFontSize + "px;");
        exchangeRateChartUSDtoLBP.setPrefWidth(width / 2);
        exchangeRateChartLBPtoUSD.setPrefWidth(width / 2);
        exchangeRateChartUSDtoLBP.setPrefHeight(height / 2);
        exchangeRateChartLBPtoUSD.setPrefHeight(height / 2);
        startDatePicker.prefWidthProperty().bind(hbox.widthProperty().multiply(0.3));
        endDatePicker.prefWidthProperty().bind(hbox.widthProperty().multiply(0.3));
        getData.prefWidthProperty().bind(hbox.widthProperty().multiply(0.3));
    }
    private void setupChart() {
        exchangeRateChartUSDtoLBP.setTitle("Fluctuations of USD to LBP rate");
        exchangeRateChartLBPtoUSD.setTitle("Fluctuations of LBP to USD rate");
    }

    // Populate the chart with data from the API call between start date and end date.
    @FXML
    public void loadChartData() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        DateInput dateInput = new DateInput(startDate, endDate);
        if (startDate!=null && endDate!=null &&  startDate.isAfter(endDate)) {
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Start date must be after end date");
                exchangeRateChartUSDtoLBP.getData().clear();
                exchangeRateChartLBPtoUSD.getData().clear();
            });
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            return;
        }
        if ((startDate!=null && startDate.isAfter(LocalDate.now())) || (endDate!=null && endDate.isAfter(LocalDate.now()))){
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Dates cannot be in the future.");
                exchangeRateChartUSDtoLBP.getData().clear();
                exchangeRateChartLBPtoUSD.getData().clear();
            });
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            return;
        }

        ExchangeService.exchangeApi().getExchangeRatesWithDates(
                dateInput.start_year != null ? dateInput.start_year.toString() : "",
                dateInput.start_month != null ? dateInput.start_month.toString() : "",
                dateInput.start_day != null ? dateInput.start_day.toString() : "",
                dateInput.end_year != null ? dateInput.end_year.toString() : "",
                dateInput.end_month != null ? dateInput.end_month.toString() : "",
                dateInput.end_day != null ? dateInput.end_day.toString() : ""
        ).enqueue(new Callback<List<ExchangeRateDate>>() {
            @Override
            public void onResponse(Call<List<ExchangeRateDate>> call, Response<List<ExchangeRateDate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Platform.runLater(() -> updateChart(response.body()));
                } else {
                    Platform.runLater(() -> {
                        Alerts.displayErrorMessage("Invalid Input");
                        exchangeRateChartUSDtoLBP.getData().clear();
                        exchangeRateChartLBPtoUSD.getData().clear();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ExchangeRateDate>> call, Throwable throwable) {
                Platform.runLater(() -> {
                    Alerts.displayErrorMessage("Request could not be completed");
                });
            }
        });
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    //This function puts the given list of rows on the graph.
    private void updateChart(List<ExchangeRateDate> rates) {
        xAxis1.setAnimated(false);
        xAxis2.setAnimated(false);
        XYChart.Series<String, Number> seriesUSDToLBP = new XYChart.Series<>();
        XYChart.Series<String, Number> seriesLBPToUSD = new XYChart.Series<>();

        for (ExchangeRateDate rate : rates) {
            if (rate.getUsdToLbp() != null) {
                seriesUSDToLBP.getData().add(new XYChart.Data<>(rate.getDate(), rate.getUsdToLbp()));
            }
            if (rate.getLbpToUsd() != null) {
                seriesLBPToUSD.getData().add(new XYChart.Data<>(rate.getDate(), rate.getLbpToUsd()));
            }
        }
        exchangeRateChartUSDtoLBP.getData().clear();
        exchangeRateChartUSDtoLBP.getData().addAll(seriesUSDToLBP);
        exchangeRateChartUSDtoLBP.requestLayout();
        exchangeRateChartLBPtoUSD.getData().clear();
        exchangeRateChartLBPtoUSD.getData().addAll(seriesLBPToUSD);
        exchangeRateChartLBPtoUSD.requestLayout();
        seriesUSDToLBP.getNode().setStyle("-fx-stroke: orange;");
        seriesLBPToUSD.getNode().setStyle("-fx-stroke: orange;");

    }

    public void setOnPageCompleteListener(OnPageCompleteListener
                                                  onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }
}

