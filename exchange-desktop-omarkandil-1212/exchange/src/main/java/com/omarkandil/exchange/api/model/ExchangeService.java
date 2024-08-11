package com.omarkandil.exchange.api.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
// This is to establish the connections with the backend which all API calls will use.
public class ExchangeService {
    static String API_URL = "http://localhost:5000";
    public static Exchange exchangeApi() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(Exchange.class);
    }
}
