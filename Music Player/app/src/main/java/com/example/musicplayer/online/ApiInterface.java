package com.example.musicplayer.online;


import com.example.musicplayer.SongItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

  @GET("getMusics.php")
  Call<ArrayList<MusicResponse>> getMusicsCall();

  @POST("login.php")
  Call<ArrayList<LoginResponse>> loginCall(@Query("username") String phoneNumber , @Query("password") String password);
  @POST("register.php")
  Call<ArrayList<RegisterResponse>> registerCall(@Query("phoneNumber") String phoneNumber , @Query("password") String password , @Query("username") String email);

}
