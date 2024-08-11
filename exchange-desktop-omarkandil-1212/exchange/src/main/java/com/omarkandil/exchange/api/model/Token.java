package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;

//This is used to represent the token which is used to identify a user.
public class Token {
    @SerializedName("token")
    String token;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
