package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
// This class is used when only a single date is needed to be sent to/from the backend.
public class SingleDate {
    @SerializedName("YEAR")
    public Integer year;
    @SerializedName("MONTH")
    public Integer month;
    @SerializedName("DAY")
    public Integer day;

    public SingleDate(LocalDate date) {
        if (date != null) {
            this.year = date.getYear();
            this.month = date.getMonthValue();
            this.day = date.getDayOfMonth();
        }
        else{
            this.year=null;
            this.month=null;
            this.day=null;
        }
    }
}
