package com.example.musicplayer.online;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {

    public static final String BASE_URL = "http:androidyad.ir/api/musicPlayer/";
    public static Retrofit myRetrofit = null;

    public static Retrofit getApi() {
        if (myRetrofit == null) {
            myRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return myRetrofit;
    }

}
