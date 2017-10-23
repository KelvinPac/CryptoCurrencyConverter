package com.homeautogroup.cryptocurrencyconverter.api;


import com.homeautogroup.cryptocurrencyconverter.model.CryptoCompare;

import retrofit2.Call;
import retrofit2.http.GET;



public interface APIService {


   /* //getting messages
    @GET("messages/{id}")
    Call<Messages> getMessages(@Path("id") int id);*/

    //getting latest currency exchange rates
    @GET("pricemulti?fsyms=ETH,BTC&tsyms=NGN,KES,USD,GBP,CNY,CHF,EUR,MXN,GLD,JOD,OMR,BHD,KWD,JPY,CAD,AED,CLP,BRL,INR,ILS")
    Call<CryptoCompare> getLatestExchangeRates();


}
