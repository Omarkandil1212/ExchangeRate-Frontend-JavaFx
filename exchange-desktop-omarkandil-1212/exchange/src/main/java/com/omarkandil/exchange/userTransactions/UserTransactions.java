package com.omarkandil.exchange.userTransactions;
import com.omarkandil.exchange.*;
import com.omarkandil.exchange.api.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// This class represents the user transactions between each other.
public class UserTransactions implements PageCompleter {
    private OnPageCompleteListener onPageCompleteListener;
    @FXML
    private TextField usdAmountInput;
    @FXML
    private TextField lbpAmountInput;
    @FXML
    private CheckBox usdToLbpInput;
    @FXML
    private ListView<UserTransaction> offersListView;

    @FXML
    private Label usdBalanceLabel;
    @FXML
    private Label lbpBalanceLabel;
    @FXML
    private TextField lowAmountInput;
    @FXML
    private TextField highAmountInput;
    @FXML
    private ComboBox<String> transactionTypeInput;
    private VoiceOverService voiceOverService = VoiceOverService.getInstance();
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private HBox filterHBox;
    @FXML
    private HBox filterHBox2;
    @FXML
    private Button applyFiltersButton;
    @FXML
    private Button addOfferButton;
    // This function gets the balance of that specific user.
    private void fetchUserBalance() {
        ExchangeService.exchangeApi().getUserBalance("Bearer " + Authentication.getInstance().getToken())
                .enqueue(new Callback<BalanceResponse>() {
                    @Override
                    public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BalanceResponse balances = response.body();
                            Platform.runLater(() -> {
                                usdBalanceLabel.setText("USD Balance: $" + String.format("%.2f", balances.usd_balance));
                                lbpBalanceLabel.setText("LBP Balance: LL" + String.format("%.2f", balances.lbp_balance));
                            });
                        } else {
                            Platform.runLater(() -> {
                                Alerts.displayErrorMessage("Failed to fetch balance");
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<BalanceResponse> call, Throwable throwable) {
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Failed to fetch balance: " + throwable.getMessage());
                        });
                    }
                });
    }
    // This function updates the fonts of the page.
    private void updateStyles(double size) {
        double fontSize = Math.min(16, size / 50);

        usdAmountInput.setStyle("-fx-font-size: " + fontSize + "px;");
        lbpAmountInput.setStyle("-fx-font-size: " + fontSize + "px;");
        usdToLbpInput.setStyle("-fx-font-size: " + fontSize + "px;");
        offersListView.setStyle("-fx-font-size: " + fontSize + "px;");
        usdBalanceLabel.setStyle("-fx-font-size: " + fontSize + "px;");
        lbpBalanceLabel.setStyle("-fx-font-size: " + fontSize + "px;");
        lowAmountInput.setStyle("-fx-font-size: " + fontSize + "px;");
        highAmountInput.setStyle("-fx-font-size: " + fontSize + "px;");
        transactionTypeInput.setStyle("-fx-font-size: " + fontSize + "px;");

        lowAmountInput.prefWidthProperty().bind(filterHBox.widthProperty().multiply(0.1));
        highAmountInput.prefWidthProperty().bind(filterHBox.widthProperty().multiply(0.1));
        transactionTypeInput.prefWidthProperty().bind(filterHBox.widthProperty().multiply(0.2));
        applyFiltersButton.prefWidthProperty().bind(filterHBox.widthProperty().multiply(0.2));
        usdAmountInput.prefWidthProperty().bind(filterHBox2.widthProperty().multiply(0.1));
        lbpAmountInput.prefWidthProperty().bind(filterHBox2.widthProperty().multiply(0.1));
        addOfferButton.prefWidthProperty().bind(filterHBox2.widthProperty().multiply(0.2));
    }

    public void initialize() {
        if (SettingsManager.isAccessibilityEnabled()) {
            voiceOverService.speak("Inter User Transactions");
        }
        mainBorderPane.widthProperty().addListener((obs, oldVal, newVal) -> updateStyles(newVal.doubleValue()));
        mainBorderPane.heightProperty().addListener((obs, oldVal, newVal) -> updateStyles(newVal.doubleValue()));
        offersListView.setCellFactory(param -> new ListCell<UserTransaction>() {
            @Override
            protected void updateItem(UserTransaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(5);
                    Label details = new Label("Offered by: " + item.getUser1UserName() +
                            ", USD: " + item.getUsdAmount() + " to LBP: " + item.getLbpAmount());
                    Button acceptButton = new Button("Accept");
                    acceptButton.setOnAction(event -> acceptTransaction(item));
                    vbox.getChildren().addAll(details, acceptButton);
                    setGraphic(vbox);
                }
            }
        });
        fetchUserBalance();
        loadTransactions();
    }
    // This function is triggered when the user accepts a transaction. It then refetches all the transactions and the balances.
    private void acceptTransaction(UserTransaction transaction) {
        TransactionID transactionID= new TransactionID(transaction.getId());

        ExchangeService.exchangeApi().acceptInterUserTransaction(transactionID,"Bearer " +
                Authentication.getInstance().getToken())
                        .enqueue(new Callback<UserTransaction>() {
                            @Override
                            public void onResponse(Call<UserTransaction> call, Response<UserTransaction> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    loadTransactions();
                                    fetchUserBalance();
                                }
                                else{
                                    Platform.runLater(() -> {
                                        Alerts.displayErrorMessage("Cant accept this offer");
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<UserTransaction> call, Throwable throwable) {
                                Alerts.displayErrorMessage("Failed to accept offer");
                            }
                        });

    }

    // This function displays in a list all the unaccepted transactions till now.
    private void loadTransactions() {

        ExchangeService.exchangeApi().getInterUserTransactions("Bearer " +
                        Authentication.getInstance().getToken())
                .enqueue(new Callback<List<UserTransaction>>() {
                    @Override
                    public void onResponse(Call<List<UserTransaction>> call,
                                           Response<List<UserTransaction>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Platform.runLater(() -> {
                                offersListView.setItems(FXCollections.observableArrayList(response.body()));
                                offersListView.setCellFactory(lv -> new ListCell<UserTransaction>() {
                                    @Override
                                    protected void updateItem(UserTransaction item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty || item == null) {
                                            setText(null);
                                            setGraphic(null);
                                        } else {
                                            VBox vbox = new VBox(5);
                                            String transactionType = item.getUsdToLbp() ? "Selling USD" : "Buying USD";
                                            Label details = new Label("Offered by: " + item.getUser1UserName() +
                                                    ",  USD: " + item.getUsdAmount() +
                                                    "  LBP: " + item.getLbpAmount() +
                                                    "  " + transactionType);
                                            Button acceptButton = new Button("Accept");
                                            acceptButton.setOnAction(event -> acceptTransaction(item));
                                            vbox.getChildren().addAll(details, acceptButton);
                                            setGraphic(vbox);
                                        }
                                    }
                                });
                            });

                        } else {
                            Platform.runLater(() -> {
                                Alerts.displayErrorMessage("Failed to get transactions");
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<List<UserTransaction>> call,
                                          Throwable throwable) {
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Failed to get transactions");
                        });
                    }
                });
    }

    // This function adds a user transaction with the user specified inputs.
    @FXML
    private void addUserTransaction() {
        try {
            String usdAmountStr = usdAmountInput.getText().trim();
            String lbpAmountStr = lbpAmountInput.getText().trim();
            if (usdAmountStr.isEmpty() || lbpAmountStr.isEmpty()) {
                Platform.runLater(() -> {
                    Alerts.displayErrorMessage("Error. Please enter both USD and LBP amounts.");
                    usdAmountInput.setText("");
                    lbpAmountInput.setText("");
                    usdToLbpInput.setSelected(false);
                });
                return;
            }
            Double usdAmount = Double.parseDouble(usdAmountStr);
            Double lbpAmount = Double.parseDouble(lbpAmountStr);
            if (usdAmount < 0.0001 || lbpAmount < 100 || lbpAmount>1000000000 || usdAmount>10000000) {
                Platform.runLater(() -> {
                    Alerts.displayErrorMessage("Error. Invalid amounts.");
                    usdAmountInput.setText("");
                    lbpAmountInput.setText("");
                    usdToLbpInput.setSelected(false);
                });
                return;
            }

            boolean usdToLbp = usdToLbpInput.isSelected();
            Transaction transaction = new Transaction(
                    usdAmount,
                    lbpAmount,
                    usdToLbp
            );
            ExchangeService.exchangeApi().addInterUserTransaction(transaction, "Bearer " +
                    Authentication.getInstance().getToken()).enqueue(new Callback<UserTransaction>() {
                @Override
                public void onResponse(Call<UserTransaction> call, Response<UserTransaction> response) {
                    loadTransactions();
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            usdAmountInput.setText("");
                            lbpAmountInput.setText("");
                            usdToLbpInput.setSelected(false);
                        });
                    }
                    else{
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Cant add this transaction.");
                            usdAmountInput.setText("");
                            lbpAmountInput.setText("");
                            usdToLbpInput.setSelected(false);
                        });
                    }
                }

                @Override
                public void onFailure(Call<UserTransaction> call, Throwable throwable) {
                    Platform.runLater(() -> {
                        Alerts.displayErrorMessage("Error. Insertion failed.");
                    });
                }
            });
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Error. Please enter Integer values.");
                usdAmountInput.setText("");
                lbpAmountInput.setText("");
                usdToLbpInput.setSelected(false);
            });
        }
    }

    // This function filters the transactions based on the type and lbp range specified by the user.
    @FXML
    private void applyFilters() {
        String lowAmountStr = lowAmountInput.getText().trim();
        String highAmountStr = highAmountInput.getText().trim();
        Double lowAmount = null;
        Double highAmount = null;

        try {
            if (!lowAmountStr.isEmpty()) {
                lowAmount = Double.parseDouble(lowAmountStr);
                if (lowAmount < 100 || lowAmount>1000000000) {
                    throw new IllegalArgumentException("Low amount invalid.");
                }
            }
            if (!highAmountStr.isEmpty()) {
                highAmount = Double.parseDouble(highAmountStr);
                if (highAmount < 100 || highAmount>1000000000) {
                    throw new IllegalArgumentException("High amount invalid.");
                }
            }
            if (lowAmount != null && highAmount != null && lowAmount > highAmount) {
                throw new IllegalArgumentException("Low amount cannot be greater than high amount.");
            }
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                Alerts.displayErrorMessage("Please enter valid numbers for amounts.");
            });
            lowAmountInput.setText("");
            highAmountInput.setText("");
            return;
        } catch (IllegalArgumentException e) {
            Platform.runLater(() -> {
                Alerts.displayErrorMessage(e.getMessage());
            });
            lowAmountInput.setText("");
            highAmountInput.setText("");
            return;
        }

        String type = transactionTypeInput.getValue();

        Double finalLowAmount = lowAmount;
        Double finalHighAmount = highAmount;
        ExchangeService.exchangeApi().getInterUserTransactions("Bearer " + Authentication.getInstance().getToken())
                .enqueue(new Callback<List<UserTransaction>>() {
                    @Override
                    public void onResponse(Call<List<UserTransaction>> call, Response<List<UserTransaction>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<UserTransaction> filteredTransactions = response.body().stream()
                                    .filter(tx -> (finalLowAmount == null || tx.getLbpAmount() >= finalLowAmount) &&
                                            (finalHighAmount == null || tx.getLbpAmount() <= finalHighAmount) &&
                                            ("All".equals(type) || (tx.getUsdToLbp() && "Selling USD".equals(type)) || (!tx.getUsdToLbp() && "Buying USD".equals(type))))
                                    .collect(Collectors.toList());

                            Platform.runLater(() -> {
                                offersListView.setItems(FXCollections.observableArrayList(filteredTransactions));
                                lowAmountInput.setText("");
                                highAmountInput.setText("");
                            });
                        } else {
                            Platform.runLater(() -> {
                                Alerts.displayErrorMessage("Failed to get transactions");
                                lowAmountInput.setText("");
                                highAmountInput.setText("");
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserTransaction>> call, Throwable throwable) {
                        Platform.runLater(() -> {
                            Alerts.displayErrorMessage("Failed to get transactions: " + throwable.getMessage());
                            lowAmountInput.setText("");
                            highAmountInput.setText("");
                        });
                    }
                });
    }

    public void setOnPageCompleteListener(OnPageCompleteListener
                                                  onPageCompleteListener) {
        this.onPageCompleteListener = onPageCompleteListener;
    }
}
