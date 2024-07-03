package com.example.musicplayer.online
import com.google.gson.annotations.SerializedName

class RegisterResponse {

    @SerializedName("response")
    var response: String  = ""

    @SerializedName("phoneNumber")
    var phoneNumber: String  = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("password")
    var password: String = ""

}