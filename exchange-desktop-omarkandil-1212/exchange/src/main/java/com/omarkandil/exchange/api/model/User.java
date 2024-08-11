package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;
// This class represents a user with a name, password, and ID.
public class User {
    @SerializedName("id")
    Integer id;
    @SerializedName("user_name")
    String username;
    @SerializedName("password")
    String password;
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return this.username;
    }
}

