package com.example.musicplayer.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.musicplayer.R
import com.example.musicplayer.framework.Debug
import com.example.musicplayer.framework.MToast
import com.example.musicplayer.framework.PermissionHandler
import com.example.musicplayer.framework.XActivity
import com.example.musicplayer.online.API
import com.example.musicplayer.online.ApiInterface
import com.example.musicplayer.online.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : XActivity() {
    private lateinit var edtPassword: EditText
    private lateinit var edtUsername: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtGoToRegister: TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var rootCardView : ConstraintLayout


    private var username = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        PermissionHandler(listOf("android.permission.READ_MEDIA_AUDIO"))
        assignViews()
        setAnimation()
        loadInfo()
        handleLogin()

    }

    private fun handleLogin() {
        btnLogin.setOnClickListener {

            password = edtPassword.text.toString()
            username = edtUsername.text.toString()

            progressBar.visibility = View.VISIBLE
            rootCardView.alpha = 0.2f

            val apiInterface = API.getApi().create(ApiInterface::class.java)
            val logInCall: Call<ArrayList<LoginResponse>> =
                apiInterface.loginCall(username, password)

            logInCall.enqueue(object : Callback<ArrayList<LoginResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<LoginResponse>>,
                    response: Response<ArrayList<LoginResponse>>
                ) {
                    if (response.body() != null) {
                        progressBar.visibility = View.INVISIBLE
                        rootCardView.alpha = 1f

                        for (item in response.body()!!) {
                            if (item.response == "SUCCESS") {
                                password = item.password
                                email = item.email
                                username = item.phoneNumber
                                saveInfo()
                            }

                            if (item.response == "FAILED") {
                                MToast("username or password incorrect!")
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ArrayList<LoginResponse>>, t: Throwable) {
                    progressBar.visibility = View.INVISIBLE
                    rootCardView.alpha = 1f
                    Debug.logInfo("Login Call  : " + t.message.toString())
                    MToast("check your connection!")
                }

            })
        }

        txtGoToRegister.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
        }

    }

    private fun setAnimation() {
        val animationView: LottieAnimationView = findViewById(R.id.loginAnimation)
        try {
            animationView.setAnimationFromUrl("http://androidyad.ir/api/musicPlayer/anim.json")

        } catch (e: Exception) {
            Log.e("Amir", "Error loading animation: ${e.message}")
        }
    }

    private fun assignViews() {
        edtUsername = findViewById(R.id.loginUsername)
        edtPassword = findViewById(R.id.loginPassword)
        btnLogin = findViewById(R.id.loginButton)
        txtGoToRegister = findViewById(R.id.loginRegister)
        progressBar = findViewById(R.id.loginProgressbar)
        rootCardView =findViewById(R.id.loginCardView)

    }
    private fun loadInfo() {
        val sharedPreferences = getSharedPreferences("logInInfo", MODE_PRIVATE)
        if (sharedPreferences.contains("phoneNumber")) {
            password = sharedPreferences.getString("password", null)!!
            username = sharedPreferences.getString("phoneNumber", null)!!
            email = sharedPreferences.getString("password", null)!!
            startHomeActivity()
        }
    }
    private fun saveInfo() {
        val sharedPreferences = getSharedPreferences("logInInfo", MODE_PRIVATE)
        val editor = sharedPreferences.edit().apply {
            putString("password", password)
            putString("phoneNumber", username)
            putString("email", email)
            apply()

        }
        startHomeActivity()
    }
    private fun startHomeActivity() {
        val intent  = Intent(this@LoginActivity , MainActivity::class.java)
        intent.putExtra("email" , email)
        intent.putExtra("phoneNumber" , username)
        intent.putExtra("password" , password)
        startActivity(intent)
        finish()
    }

}