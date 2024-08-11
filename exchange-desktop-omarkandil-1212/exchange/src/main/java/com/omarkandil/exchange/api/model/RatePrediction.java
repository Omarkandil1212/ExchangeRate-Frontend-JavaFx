package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;
//Holds the predicted rate returned from the backend.

public class RatePrediction {
    @SerializedName("rate_prediction")
    public Double rate;
}
