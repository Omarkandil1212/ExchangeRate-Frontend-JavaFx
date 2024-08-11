package com.omarkandil.exchange.statistics;

import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.LocalDate;
import java.util.Map;

//This class controls the display of the various statistics.
public class Statistics implements PageCompleter {
    private OnPageCompleteListener onPageCompleteListener;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TableView<StatisticsEntry> statsTable;
    @FXML
    private TableColumn<StatisticsEntry, String> statTypeColumn;
    @FXML
    private TableColumn<StatisticsEntry, String> valueColumn;
    @FXML
    private TableColumn<StatisticsEntry, String> dateColumn;
    @FXML
    private CheckBox checkExtremums;
    @FXML
    private CheckBox checkAverages;
    @FXML
    private CheckBox checkVolumes;
    @FXML
    private CheckBox checkPercentChanges;
    private boolean messageDisplay;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();
    @FXML
    private HBox hbox1,hbox2;
    @FXML
    private Button getData;
    @FXML
    private BorderPane mainContainer;

    //Adds event listeners and initalises the table to an empty one.
    @FXML
    public void initialize() {
        startDatePicker.setEditable(false);
        endDatePicker.setEditable(false);
        statTypeColumn.setCellValueFactory(new PropertyValueFactory<>("statisticType"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Statistics page");
        }
        mainContainer.widthProperty().addListener((obs, oldVal, newVal) -> styleFields());
        mainContainer.heightProperty().addListener((obs, oldVal, newVal) -> styleFields());
    }
    private void styleFields(){
        startDatePicker.prefWidthProperty().bind(hbox1.widthProperty().multiply(0.3));
        endDatePicker.prefWidthProperty().bind(hbox1.widthProperty().multiply(0.3));
        getData.prefWidthProperty().bind(hbox1.widthProperty().multiply(0.3));
        checkExtremums.prefWidthProperty().bind(hbox2.widthProperty().multiply(0.2));
        checkAverages.prefWidthProperty().bind(hbox2.widthProperty().multiply(0.2));
        checkVolumes.prefWidthProperty().bind(hbox2.widthProperty().multiply(0.2));
        checkPercentChanges.prefWidthProperty().bind(hbox2.widthProperty().multiply(0.2));
    }
    //This function is called on the button selection and calls the respective statistics functions based on
    // the selected checkboxes.
    @FXML
    void getStatistics(ActionEvent event) {
        Platform.runLater(() -> statsTable.getItems().clear());
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        DateInput dateInput = new DateInput(startDate, endDate);
        ObservableList<StatisticsEntry> data = FXCollections.observableArrayList();
        if (startDate!=null && endDate!=null &&  startDate.isAfter(endDate)) {
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Start date must be before end date.");
            });
            clearFields();
            return;
        }
        if ((startDate!=null && startDate.isAfter(LocalDate.now())) || (endDate!=null && endDate.isAfter(LocalDate.now()))){
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Cannot input future dates.");
            });
            clearFields();
            return;
        }
        messageDisplay=true;
        if (checkExtremums.isSelected()) {
            fetchExtremums(data, dateInput);
            if (messageDisplay){
                messageDisplay=false;
            }
        }
        if (checkAverages.isSelected()) {
            fetchAverages(data, dateInput,messageDisplay);
            if (messageDisplay){
                messageDisplay=false;
            }
        }
        if (checkVolumes.isSelected()) {
            fetchVolumes(data, dateInput,messageDisplay);
            if (messageDisplay){
                messageDisplay=false;
            }
        }
        if (checkPercentChanges.isSelected()) {
            fetchPercentChanges(data, dateInput,messageDisplay);
        }
        clearFields();
    }
    private void clearFields(){
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        checkPercentChanges.setSelected(false);
        checkExtremums.setSelected(false);
        checkVolumes.setSelected(false);
        checkAverages.setSelected(false);
    }

    //This function gets and displays the extremums between the specified dates.
    private void fetchExtremums(ObservableList<StatisticsEntry> data, DateInput dateInput) {
        ExchangeService.exchangeApi().getExtremums(
                dateInput.start_year != null ? dateInput.start_year.toString() : "",
                dateInput.start_month != null ? dateInput.start_month.toString() : "",
                dateInput.start_day != null ? dateInput.start_day.toString() : "",
                dateInput.end_year != null ? dateInput.end_year.toString() : "",
                dateInput.end_month != null ? dateInput.end_month.toString() : "",
                dateInput.end_day != null ? dateInput.end_day.toString() : ""
        ).enqueue(new
              Callback<Object>() {
                  @Override
                  public void onResponse(Call<Object> call, Response<Object> response) {
                      Object body = response.body();
                      if (body != null) {
                          Platform.runLater(() -> {
                              Object maxLbpToUsdObj = ((Map<String, Object>) body).get("max_lbp_to_usd");
                              Object maxUsdToLbpObj = ((Map<String, Object>) body).get("max_usd_to_lbp");
                              Object minLbpToUsdObj = ((Map<String, Object>) body).get("min_lbp_to_usd");
                              Object minUsdToLbpObj = ((Map<String, Object>) body).get("min_usd_to_lbp");
                              if (maxLbpToUsdObj != null) {
                                  updateTable(data, "Max LBP to USD", String.format("%.2f", maxLbpToUsdObj), ((Map<String, Object>) body).get("max_lbp_to_usd_date"));
                                  updateTable(data, "Min LBP to USD", String.format("%.2f", minLbpToUsdObj), ((Map<String, Object>) body).get("min_lbp_to_usd_date"));
                                  updateTable(data, "Max USD to LBP", String.format("%.2f", maxUsdToLbpObj), ((Map<String, Object>) body).get("max_usd_to_lbp_date"));
                                  updateTable(data, "Min USD to LBP", String.format("%.2f", minUsdToLbpObj), ((Map<String, Object>) body).get("min_usd_to_lbp_date"));
                                  statsTable.setItems(data);
                              } else {
                                  updateTable(data, "Max LBP to USD", "null", "null");
                                  updateTable(data, "Min LBP to USD", "null", "null");
                                  updateTable(data, "Max USD to LBP", "null", "null");
                                  updateTable(data, "Min USD to LBP", "null", "null");
                                  statsTable.setItems(data);
                              }


                          });
                      } else {
                          Platform.runLater(() -> {
                              Alerts.displayErrorMessage("Invalid date");
                          });
                          clearChartData(data);
                          statsTable.setItems(data);
                      }
                  }
                  @Override
                  public void onFailure(Call<Object> call, Throwable throwable) {
                      Platform.runLater(() -> {
                          Alerts.displayErrorMessage("Request could not be completed");
                      });
                  }
              });
    }
    //This function gets and displays the averages between the specified dates.
    private void fetchAverages(ObservableList<StatisticsEntry> data, DateInput dateInput, boolean messageDisplay) {
        ExchangeService.exchangeApi().getAverageRates(
                dateInput.start_year != null ? dateInput.start_year.toString() : "",
                dateInput.start_month != null ? dateInput.start_month.toString() : "",
                dateInput.start_day != null ? dateInput.start_day.toString() : "",
                dateInput.end_year != null ? dateInput.end_year.toString() : "",
                dateInput.end_month != null ? dateInput.end_month.toString() : "",
                dateInput.end_day != null ? dateInput.end_day.toString() : ""
        ).enqueue(new
             Callback<Object>() {
                 @Override
                 public void onResponse(Call<Object> call, Response<Object> response) {
                     Object body = response.body();
                     if (body != null) {
                         Platform.runLater(() -> {
                             Object avgLbpToUsdObj = ((Map<String, Object>) body).get("avg_lbp_to_usd");
                             Object avgUsdToLbpObj = ((Map<String, Object>) body).get("avg_usd_to_lbp");

                             if (avgLbpToUsdObj != null && avgUsdToLbpObj != null) {
                                 double avgLbpToUsd = ((Number) avgLbpToUsdObj).doubleValue();
                                 double avgUsdToLbp = ((Number) avgUsdToLbpObj).doubleValue();
                                 updateTable(data, "Average LBP to USD", String.format("%.2f", avgLbpToUsd), null);
                                 updateTable(data, "Average USD to LBP", String.format("%.2f", avgUsdToLbp), null);
                                 statsTable.setItems(data);
                             } else {
                                 updateTable(data, "Average LBP to USD", "null", null);
                                 updateTable(data, "Average LBP to USD", "null", null);
                                 statsTable.setItems(data);
                             }
                         });
                     } else {
                         if (messageDisplay) {
                             Platform.runLater(() -> {
                                 Alerts.displayErrorMessage("Invalid date");
                             });
                             clearChartData(data);
                             statsTable.setItems(data);
                         }
                     }
                 }
                 @Override
                 public void onFailure(Call<Object> call, Throwable throwable) {
                     if (messageDisplay) {
                         Platform.runLater(() -> {
                             Alerts.displayErrorMessage("Request could not be completed");
                         });
                         clearChartData(data);
                         statsTable.setItems(data);
                     }
                 }
             });
    }
    //This function gets and displays the percent changes between the specified dates.
    private void fetchPercentChanges(ObservableList<StatisticsEntry> data, DateInput dateInput,boolean messageDisplay) {
        ExchangeService.exchangeApi().getPercentChange(
                dateInput.start_year != null ? dateInput.start_year.toString() : "",
                dateInput.start_month != null ? dateInput.start_month.toString() : "",
                dateInput.start_day != null ? dateInput.start_day.toString() : "",
                dateInput.end_year != null ? dateInput.end_year.toString() : "",
                dateInput.end_month != null ? dateInput.end_month.toString() : "",
                dateInput.end_day != null ? dateInput.end_day.toString() : ""
        ).enqueue(new
      Callback<Object>() {
          @Override
          public void onResponse(Call<Object> call, Response<Object> response) {
              Object body = response.body();
              if (body != null) {
                  Platform.runLater(() -> {
                      Object percentLbpToUsdObj = ((Map<String, Object>) body).get("percent_lbp_to_usd");
                      Object percentUsdToLbpObj = ((Map<String, Object>) body).get("percent_usd_to_lbp");

                      if (percentLbpToUsdObj != null && percentUsdToLbpObj != null) {
                          double changeLbpToUsd = ((Number) percentLbpToUsdObj).doubleValue();
                          double changeUsdToLbp = ((Number) percentUsdToLbpObj).doubleValue();

                          updateTable(data, "Percent Change LBP to USD", String.format("%.2f", changeLbpToUsd), null);
                          updateTable(data, "Percent Change USD to LBP", String.format("%.2f", changeUsdToLbp), null);
                          statsTable.setItems(data);
                      } else {
                          updateTable(data, "Percent Change LBP to USD", "null", null);
                          updateTable(data, "Percent Change USD to LBP", "null", null);
                          statsTable.setItems(data);
                      }
                  });
              } else {
                  if (messageDisplay) {
                      Platform.runLater(() -> {
                          Alerts.displayErrorMessage("Invalid date");
                      });
                      clearChartData(data);
                      statsTable.setItems(data);
                  }
              }
          }
          @Override
          public void onFailure(Call<Object> call, Throwable throwable) {
              if (messageDisplay) {
                  Platform.runLater(() -> {
                      Alerts.displayErrorMessage("Request could not be completed");
                  });
              }
          }
      });
    }
    //This function gets and displays the volume of transactions between the specified dates.
    private void fetchVolumes(ObservableList<StatisticsEntry> data, DateInput dateInput,boolean messageDisplay) {
        ExchangeService.exchangeApi().getVolumes(
                dateInput.start_year != null ? dateInput.start_year.toString() : "",
                dateInput.start_month != null ? dateInput.start_month.toString() : "",
                dateInput.start_day != null ? dateInput.start_day.toString() : "",
                dateInput.end_year != null ? dateInput.end_year.toString() : "",
                dateInput.end_month != null ? dateInput.end_month.toString() : "",
                dateInput.end_day != null ? dateInput.end_day.toString() : ""
        ).enqueue(new
            Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Object body = response.body();
                    if (body != null) {
                        Platform.runLater(() -> {
                            updateTable(data, "Volume of LBP to USD", ((Map<String, Object>) body).get("lbp_to_usd_volume"), null);
                            updateTable(data, "Volume of USD to LBP", ((Map<String, Object>) body).get("usd_to_lbp_volume"), null);
                            statsTable.setItems(data);
                        });
                    }
                    else{
                        if (messageDisplay) {
                            Platform.runLater(() -> {
                                Alerts.displayErrorMessage("Invalid date");
                            });
                            clearChartData(data);
                            statsTable.setItems(data);
                        }
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable throwable) {
                    if (messageDisplay) {
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Request could not be completed");
                        });
                    }
                }
            });
    }
    private void updateTable(ObservableList<StatisticsEntry> data, String type, Object value, Object date) {
        if (value != null && date != null) {
            data.add(new StatisticsEntry(type, value.toString(), date.toString()));
        } else if (value != null) {
            data.add(new StatisticsEntry(type, value.toString(), ""));
        }
    }
    @FXML
    public void clearChartData(ObservableList<StatisticsEntry> data) {
        Platform.runLater(() -> {
            data.clear();
        });
    }
    public void setOnPageCompleteListener(OnPageCompleteListener
                                                  onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }
}
