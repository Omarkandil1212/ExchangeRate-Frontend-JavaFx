package com.omarkandil.exchange.transactions;

import com.omarkandil.exchange.Alerts;
import com.omarkandil.exchange.Authentication;
import com.omarkandil.exchange.SettingsManager;
import com.omarkandil.exchange.VoiceOverService;
import com.omarkandil.exchange.api.model.ExchangeService;
import com.omarkandil.exchange.api.model.Transaction;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
// This class displays the previous transactions a user entered to the website.
public class Transactions implements Initializable {
    public TableColumn lbpAmount;
    public TableColumn usdAmount;
    public TableColumn transactionDate;
    public TableColumn usdToLbp;
    public TableColumn id;
    public TableView tableView;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();
    // This function initializes the table then populates it with the data from the backend for that specific user.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Previous Transactions");
        }
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        id.setCellValueFactory(new
                PropertyValueFactory<Transaction, Long>("id"));
        lbpAmount.setCellValueFactory(new
                PropertyValueFactory<Transaction, Long>("lbpAmount"));
        usdAmount.setCellValueFactory(new
                PropertyValueFactory<Transaction, Long>("usdAmount"));
        usdToLbp.setCellValueFactory(new
                PropertyValueFactory<Transaction, Boolean>("usdToLbp"));
        transactionDate.setCellValueFactory(new
                PropertyValueFactory<Transaction, String>("addedDate"));
        ExchangeService.exchangeApi().getTransactions("Bearer " +
                        Authentication.getInstance().getToken())
                .enqueue(new Callback<List<Transaction>>() {
                    @Override
                    public void onResponse(Call<List<Transaction>> call,
                                           Response<List<Transaction>> response) {
                        tableView.getItems().setAll(response.body());
                    }
                    @Override
                    public void onFailure(Call<List<Transaction>> call, Throwable throwable) {
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Failed to fetch transactions");
                        });
                    }
                });
    }
}

