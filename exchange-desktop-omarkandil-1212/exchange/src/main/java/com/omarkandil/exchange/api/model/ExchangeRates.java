package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;
// A class to hold the current exchange rates.
public class ExchangeRates {
    @SerializedName("usd_to_lbp")
    public Float usdToLbp;
    @SerializedName("lbp_to_usd")
    public Float lbpToUsd;
}
