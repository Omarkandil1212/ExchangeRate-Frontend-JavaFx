package com.omarkandil.exchange.api.model;

import com.google.gson.annotations.SerializedName;

// This class represents the user preferences for bounds, notifications, and communications.
public class UserPreferences {
    @SerializedName("upper_bound_usd_to_lbp")
    public Double upperBoundUsdToLbp;

    @SerializedName("lower_bound_usd_to_lbp")
    public Double lowerBoundUsdToLbp;

    @SerializedName("upper_bound_lbp_to_usd")
    public Double upperBoundLbpToUsd;

    @SerializedName("lower_bound_lbp_to_usd")
    public Double lowerBoundLbpToUsd;

    @SerializedName("send_email")
    public Boolean sendEmail;

    @SerializedName("email")
    public String email;

    @SerializedName("send_sms")
    public Boolean sendSms;

    @SerializedName("phone_number")
    public String phoneNumber;
    @SerializedName("update_sms_preference")
    public Boolean updateSms;
    @SerializedName("update_email_preference")
    public Boolean updateEmail;
    @SerializedName("update_rates")
    public Boolean updateRates;
    @SerializedName("update_bounds")
    public Boolean updateBounds;

    public UserPreferences(Double upperBoundUsdToLbp, Double lowerBoundUsdToLbp, Double upperBoundLbpToUsd, Double lowerBoundLbpToUsd,
                           Boolean sendEmail, String email, Boolean sendSms, String phoneNumber,
                           Boolean updateSmsPreference, Boolean updateEmailPreference, Boolean updateBounds) {
        this.upperBoundUsdToLbp = upperBoundUsdToLbp;
        this.lowerBoundUsdToLbp = lowerBoundUsdToLbp;
        this.upperBoundLbpToUsd = upperBoundLbpToUsd;
        this.lowerBoundLbpToUsd = lowerBoundLbpToUsd;
        this.sendEmail = sendEmail;
        this.email = email;
        this.sendSms = sendSms;
        this.phoneNumber = phoneNumber;
        this.updateSms = updateSmsPreference;
        this.updateEmail = updateEmailPreference;
        this.updateBounds = updateBounds;
    }
}
