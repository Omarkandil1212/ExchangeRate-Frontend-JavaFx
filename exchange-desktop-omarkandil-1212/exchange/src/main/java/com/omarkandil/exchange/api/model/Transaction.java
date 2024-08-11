package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;

//This class represents a transaction done by a user on a specific date.
public class Transaction {
    @SerializedName("usd_amount")
    public Double usdAmount;
    @SerializedName("lbp_amount")
    public Double lbpAmount;
    @SerializedName("usd_to_lbp")
    public Boolean usdToLbp;
    @SerializedName("id")
    public Integer id;
    @SerializedName("added_date")
    public String addedDate;
    public Transaction(Double usdAmount, Double lbpAmount, Boolean usdToLbp)
    {
        this.usdAmount = usdAmount;
        this.lbpAmount = lbpAmount;
        this.usdToLbp = usdToLbp;
    }

    public Double getUsdAmount() {
        return usdAmount;
    }

    public Double getLbpAmount() {
        return lbpAmount;
    }

    public Boolean getUsdToLbp() {
        return usdToLbp;
    }

    public Integer getId() {
        return id;
    }

    public String getAddedDate() {
        return addedDate;
    }
}

