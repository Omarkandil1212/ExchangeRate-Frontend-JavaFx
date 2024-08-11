package com.omarkandil.exchange.api.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//This class is designed to hold all different types of statistics from the backend. Some of which have a date and some dont.
public class StatisticsEntry {
    private StringProperty statisticType;
    private StringProperty value;
    private StringProperty date;

    public StatisticsEntry(String statisticType, String value, String date) {
        this.statisticType = new SimpleStringProperty(statisticType);
        this.value = new SimpleStringProperty(value);
        this.date = new SimpleStringProperty(date);
    }
    public String getStatisticType() {
        return statisticType.get();
    }

    public String getValue() {
        return value.get();
    }

    public String getDate() {
        return date.get();
    }
}