package com.omarkandil.exchange.api.model;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

// All API calls with the backend.
public interface Exchange {
    @POST("/user")
    Call<User> addUser(@Body User user);
    @POST("/authentication")
    Call<Token> authenticate(@Body User user);
    @GET("/exchangeRate")
    Call<ExchangeRates> getExchangeRates();
    @POST("/transaction")
    Call<Object> addTransaction(@Body Transaction transaction,
                                @Header("Authorization") String authorization);
    @GET("/transaction")
    Call<List<Transaction>> getTransactions(@Header("Authorization")
                                            String authorization);
    @GET("/statistics/getExtremums")
    Call<Object> getExtremums(
            @Query(value="START_YEAR",encoded = true) String startYear,
            @Query(value="START_MONTH",encoded = true) String  startMonth,
            @Query(value = "START_DAY",encoded = true) String startDay,
            @Query(value = "END_YEAR",encoded = true) String endYear,
            @Query(value = "END_MONTH",encoded = true) String endMonth,
            @Query(value = "END_DAY",encoded = true) String  endDay
    );
    @GET("/statistics/getAverageRates")
    Call<Object> getAverageRates(@Query(value="START_YEAR",encoded = true) String startYear,
                                 @Query(value="START_MONTH",encoded = true) String  startMonth,
                                 @Query(value = "START_DAY",encoded = true) String startDay,
                                 @Query(value = "END_YEAR",encoded = true) String endYear,
                                 @Query(value = "END_MONTH",encoded = true) String endMonth,
                                 @Query(value = "END_DAY",encoded = true) String  endDay);
    @GET("/statistics/getPercentChange")
    Call<Object> getPercentChange(@Query(value="START_YEAR",encoded = true) String startYear,
                                  @Query(value="START_MONTH",encoded = true) String  startMonth,
                                  @Query(value = "START_DAY",encoded = true) String startDay,
                                  @Query(value = "END_YEAR",encoded = true) String endYear,
                                  @Query(value = "END_MONTH",encoded = true) String endMonth,
                                  @Query(value = "END_DAY",encoded = true) String  endDay);
    @GET("/statistics/getVolumes")
    Call<Object> getVolumes(@Query(value="START_YEAR",encoded = true) String startYear,
                            @Query(value="START_MONTH",encoded = true) String  startMonth,
                            @Query(value = "START_DAY",encoded = true) String startDay,
                            @Query(value = "END_YEAR",encoded = true) String endYear,
                            @Query(value = "END_MONTH",encoded = true) String endMonth,
                            @Query(value = "END_DAY",encoded = true) String  endDay);

    @GET("/exchangeRate/withDates")
    Call<List<ExchangeRateDate>> getExchangeRatesWithDates(@Query(value="START_YEAR",encoded = true) String startYear,
                                                           @Query(value="START_MONTH",encoded = true) String  startMonth,
                                                           @Query(value = "START_DAY",encoded = true) String startDay,
                                                           @Query(value = "END_YEAR",encoded = true) String endYear,
                                                           @Query(value = "END_MONTH",encoded = true) String endMonth,
                                                           @Query(value = "END_DAY",encoded = true) String  endDay);
    @GET("/interUserTransactions")
    Call<List<UserTransaction>> getInterUserTransactions(@Header("Authorization")
                                                         String authorization);
    @POST("/interUserTransactions")
    Call<UserTransaction> addInterUserTransaction(@Body Transaction transaction,
                                @Header("Authorization") String authorization);
    @POST("/interUserTransactions/acceptTransaction")
    Call<UserTransaction> acceptInterUserTransaction(@Body TransactionID transactionID,
                                                  @Header("Authorization") String authorization);
    @GET("/interUserTransactions/userSpecific")
    Call<List<UserTransaction>> getUsersTransactions(@Header("Authorization")
                                                         String authorization);
    @POST("/user/updateUserPreferences")
    Call<Object> changePreferences(@Body UserPreferences userPreferences,
                                                     @Header("Authorization") String authorization);
    @GET("/predictions")
    Call<RatePrediction> getPredictions(@Query(value="YEAR",encoded = true) String startYear,
                                        @Query(value="MONTH",encoded = true) String  startMonth,
                                        @Query(value = "DAY",encoded = true) String startDay);
    @GET("/wallet")
    Call<BalanceResponse> getUserBalance(@Header("Authorization") String authorization);
    @GET("/user/getUserPreferencesFromId")
    Call<UserPreferences> getPreferences(@Header("Authorization") String authorization);
}
