module com.omarkandil.exchange {
    requires javafx.controls;
    requires javafx.fxml;
    requires retrofit2;
    requires java.sql;
    requires retrofit2.converter.gson;
    requires java.prefs;
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires socket.io.client;
    requires json;
    requires engine.io.client;
    requires freetts;
    opens com.omarkandil.exchange to javafx.fxml;
    opens com.omarkandil.exchange.api.model to javafx.base,com.google.gson;
    exports com.omarkandil.exchange;
    exports com.omarkandil.exchange.rates;
    exports com.omarkandil.exchange.statistics;
    exports com.omarkandil.exchange.fluctuations;
    exports com.omarkandil.exchange.userTransactions;
    exports com.omarkandil.exchange.previousTransactions;
    exports com.omarkandil.exchange.preferences;
    opens com.omarkandil.exchange.rates to javafx.fxml;
    opens com.omarkandil.exchange.login to javafx.fxml;
    opens com.omarkandil.exchange.register to javafx.fxml;
    opens com.omarkandil.exchange.transactions to javafx.fxml;
    opens com.omarkandil.exchange.statistics to javafx.fxml;
    opens com.omarkandil.exchange.fluctuations to javafx.fxml;
    opens com.omarkandil.exchange.userTransactions to javafx.fxml;
    opens com.omarkandil.exchange.previousTransactions to javafx.fxml;
    opens com.omarkandil.exchange.preferences to javafx.fxml;
    opens com.omarkandil.exchange.machineLearning to javafx.fxml;

        }