package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;
//This is used to send a transaction ID to the backend, mainly for accepting transactions.
public class TransactionID {
    @SerializedName("transaction_id")
    Integer transactionID;
    public TransactionID(Integer transactionID){
        this.transactionID=transactionID;
    }
    public void setID(Integer transactionID){
        this.transactionID=transactionID;
    }
    public Integer getID(){
        return this.transactionID;
    }
}
