package com.omarkandil.exchange.previousTransactions;

import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.ExchangeService;
import com.omarkandil.exchange.api.model.UserTransaction;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

// This class displays the previous transactions a user made with other users.
public class PreviousTransactions implements PageCompleter {
    private OnPageCompleteListener onPageCompleteListener;

    @FXML
    private TableView<UserTransaction> transactionsTable;
    @FXML
    private TableColumn<UserTransaction, String> user1Column;
    @FXML
    private TableColumn<UserTransaction, String> user2Column;
    @FXML
    private TableColumn<UserTransaction, String> dateColumn;
    @FXML
    private TableColumn<UserTransaction, Double> usdAmountColumn;
    @FXML
    private TableColumn<UserTransaction, Double> lbpAmountColumn;
    @FXML
    private TableColumn<UserTransaction, String> transactionTypeColumn;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();

    //Initialize the table that displays the transactions.
    @FXML
    public void initialize() {
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Your previous transactions with others");
        }
        user1Column.setCellValueFactory(new PropertyValueFactory<>("user1UserName"));
        user2Column.setCellValueFactory(new PropertyValueFactory<>("user2UserName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        usdAmountColumn.setCellValueFactory(new PropertyValueFactory<>("usdAmount"));
        lbpAmountColumn.setCellValueFactory(new PropertyValueFactory<>("lbpAmount"));
        transactionTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsdToLbp() ? "Selling USD" : "Buying USD"));
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        load_data();
    }

    //This function gets the transactions from the backend and loads them into the table.
    @FXML
    void load_data(){
        ExchangeService.exchangeApi().getUsersTransactions("Bearer " +
                Authentication.getInstance().getToken())
                .enqueue(new Callback<List<UserTransaction>>() {
                    @Override
                    public void onResponse(Call<List<UserTransaction>> call, Response<List<UserTransaction>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Platform.runLater(() -> {
                                transactionsTable.setItems(FXCollections.observableArrayList(response.body()));
                            });
                        }
                        else{
                            Platform.runLater(() -> {
                                Alerts.displayErrorMessage("Failed to get transactions");
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<List<UserTransaction>> call, Throwable throwable) {
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Failed to get transactions");
                        });
                    }
                });
    }

    public void setOnPageCompleteListener(OnPageCompleteListener onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }
}
