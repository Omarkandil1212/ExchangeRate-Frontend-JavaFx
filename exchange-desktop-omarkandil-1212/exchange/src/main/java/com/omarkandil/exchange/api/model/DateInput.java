package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

// Class for API interactions which take in a start date and an end date.
public class DateInput {
    @SerializedName("START_YEAR")
    public Integer start_year;
    @SerializedName("START_MONTH")
    public Integer start_month;
    @SerializedName("START_DAY")
    public Integer start_day;
    @SerializedName("END_YEAR")
    public Integer end_year;
    @SerializedName("END_MONTH")
    public Integer end_month;
    @SerializedName("END_DAY")
    public Integer end_day;

    public DateInput(LocalDate startDate, LocalDate endDate) {
        if (startDate != null) {
            this.start_year = startDate.getYear();
            this.start_month = startDate.getMonthValue();
            this.start_day = startDate.getDayOfMonth();
        }
        else{
            this.start_year=null;
            this.start_month=null;
            this.start_day=null;
        }
        if (endDate != null) {
            this.end_year = endDate.getYear();
            this.end_month = endDate.getMonthValue();
            this.end_day = endDate.getDayOfMonth();
        }
        else{
            this.end_year = null;
            this.end_month = null;
            this.end_day = null;
        }
    }
}
