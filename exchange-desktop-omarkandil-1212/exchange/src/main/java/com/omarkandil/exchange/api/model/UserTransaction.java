package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
// This class represents an inter-user transaction between 2 users.

public class UserTransaction {
    @SerializedName("accepted")
    private boolean accepted;

    @SerializedName("added_date")
    private String addedDate;

    @SerializedName("date_accepted")
    private String dateAccepted;

    @SerializedName("id")
    private Integer id;

    @SerializedName("lbp_amount")
    private Double lbpAmount;

    @SerializedName("usd_amount")
    private Double usdAmount;

    @SerializedName("usd_to_lbp")
    private Boolean usdToLbp;

    @SerializedName("user1")
    private User user1;

    @SerializedName("user1_id")
    private Integer user1Id;

    @SerializedName("user2")
    private User user2;

    @SerializedName("user2_id")
    private Integer user2Id;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(String dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLbpAmount() {
        return lbpAmount;
    }

    public void setLbpAmount(Double lbpAmount) {
        this.lbpAmount = lbpAmount;
    }

    public Double getUsdAmount() {
        return usdAmount;
    }

    public void setUsdAmount(Double usdAmount) {
        this.usdAmount = usdAmount;
    }

    public Boolean getUsdToLbp() {
        return usdToLbp;
    }
    public String getUser1UserName() {
        return user1 != null ? user1.getUsername() : null;
    }
    public String getUser2UserName() {
        return user2 != null ? user2.getUsername() : null;
    }
    public void setUsdToLbp(Boolean usdToLbp) {
        this.usdToLbp = usdToLbp;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public Integer getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Integer user1Id) {
        this.user1Id = user1Id;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Integer getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Integer user2Id) {
        this.user2Id = user2Id;
    }
}
