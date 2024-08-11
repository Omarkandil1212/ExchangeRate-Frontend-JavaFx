package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;

//A class for exchange rates on a specific date. Will be used in a list to hold the rates over a period of time.
public class ExchangeRateDate {
    @SerializedName("usd_to_lbp")
    private Double usdToLbp;
    @SerializedName("lbp_to_usd")
    private Double lbpToUsd;
    @SerializedName("date")
    private String date;

    public Double getUsdToLbp() {
        return usdToLbp;
    }

    public void setUsdToLbp(Double usdToLbp) {
        this.usdToLbp = usdToLbp;
    }

    public Double getLbpToUsd() {
        return lbpToUsd;
    }

    public void setLbpToUsd(Double lbpToUsd) {
        this.lbpToUsd = lbpToUsd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
