package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;
// class to hold the current balance for a specific user.
public class BalanceResponse {
    @SerializedName("id")
    public Integer id;
    @SerializedName("user_id")
    public Integer user_id;
    @SerializedName("usd_amount")
    public Double usd_balance;
    @SerializedName("lbp_amount")
    public Double lbp_balance;
}
