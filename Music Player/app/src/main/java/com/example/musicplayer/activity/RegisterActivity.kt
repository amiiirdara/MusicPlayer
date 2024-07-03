package com.example.musicplayer.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import com.airbnb.lottie.LottieAnimationView

import com.example.musicplayer.R
import com.example.musicplayer.framework.MToast
import com.example.musicplayer.framework.XActivity
import com.example.musicplayer.online.API
import com.example.musicplayer.online.ApiInterface
import com.example.musicplayer.online.RegisterResponse
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthCalculator
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthLevel
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterActivity : XActivity() {
    private lateinit var edtPassword: EditText
    private lateinit var edtPhoneNumber: EditText
    private lateinit var edtUsername: EditText
    private lateinit var btnRegister: Button
    private lateinit var txtGoToLogin: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rootCardView: ConstraintLayout


    private var phoneNumber = ""
    private var username = ""
    private var password = ""
    private var isPasswordOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        assignViews()
        setAnimation()
        setPasswordChecker()
        handleRegister()

    }


    private fun assignViews() {
        edtPassword = findViewById(R.id.registerPassword)
        edtPhoneNumber = findViewById(R.id.registerPhoneNumber)
        edtUsername = findViewById(R.id.registerUsername)
        btnRegister = findViewById(R.id.register)
        txtGoToLogin = findViewById(R.id.registerLogin)
        progressBar = findViewById(R.id.registerProgressBar)
        rootCardView = findViewById(R.id.registerRootCardView)
    }

    private fun setAnimation() {
        val animView: LottieAnimationView = findViewById(R.id.registerAnimation)
        animView.setAnimationFromUrl("http://androidyad.ir/api/musicPlayer/anim2.json")
    }

    private fun setPasswordChecker() {

        val passwordStrengthMeter: PasswordStrengthMeter =
            findViewById(R.id.registerPasswordInputMeter)
        passwordStrengthMeter.setEditText(edtPassword)

        passwordStrengthMeter.setPasswordStrengthCalculator(object : PasswordStrengthCalculator {
            override fun calculatePasswordSecurityLevel(password: String): Int {
                var passwordLevel = 0
                //check length
                if (password.length > 6) {
                    passwordLevel++
                }
                //check for special character
                val p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
                val m = p.matcher(password)
                val isSpecialLetter = m.find()
                if (isSpecialLetter) {
                    passwordLevel++
                }

                //check for a number or uppercase letter
                val passwordChar = password.toCharArray()
                var uppercaseFlag = false
                var numericFlag = false

                for (i in password.indices) {
                    if (Character.isDigit(passwordChar[i])) {
                        numericFlag = true
                    }

                    if (Character.isUpperCase(passwordChar[i])) {
                        uppercaseFlag = true
                    }
                }

                if (uppercaseFlag) {
                    passwordLevel++
                }
                if (numericFlag and !password.isDigitsOnly()) {
                    passwordLevel++
                }


                //finished
                return passwordLevel
            }

            override fun getMinimumLength(): Int {
                return 1
            }


            override fun passwordAccepted(level: Int): Boolean {
                return level > 3
            }

            override fun onPasswordAccepted(password: String?) {
                isPasswordOk = true
            }
        })

        passwordStrengthMeter.setStrengthLevels(
            arrayOf(
                PasswordStrengthLevel("Too weak", android.R.color.background_dark),
                PasswordStrengthLevel("Week", android.R.color.holo_red_dark),
                PasswordStrengthLevel("Not bad", android.R.color.holo_red_light),
                PasswordStrengthLevel("Strong", android.R.color.holo_green_light),
                PasswordStrengthLevel("Very Strong", android.R.color.holo_green_dark),

                )
        )
    }


    private fun handleRegister() {
        progressBar.visibility = View.INVISIBLE
        rootCardView.alpha = 1f
        btnRegister.setOnClickListener {

            if (edtUsername.text.isEmpty()) {
                edtUsername.error = "Email cannot be empty!"
                return@setOnClickListener
            }

            if (edtPhoneNumber.text.isEmpty()) {
                edtPhoneNumber.error = "PhoneNumber cannot be empty!"
                return@setOnClickListener
            }

            if (!isPasswordOk) {
                edtPassword.error = "Password is not strong enough!"
                return@setOnClickListener
            }


            progressBar.visibility = View.VISIBLE
            rootCardView.alpha = 0.3f

            password = edtPassword.text.toString()
            phoneNumber = edtPhoneNumber.text.toString()
            username = edtUsername.text.toString()

            val apiInterface = API.getApi().create(ApiInterface::class.java)
            val logInCall: Call<ArrayList<RegisterResponse>> =
                apiInterface.registerCall(phoneNumber, password, username)

            logInCall.enqueue(object : Callback<ArrayList<RegisterResponse>> {

                override fun onResponse(
                    call: Call<ArrayList<RegisterResponse>>,
                    response: Response<ArrayList<RegisterResponse>>
                ) {
                    progressBar.visibility = View.INVISIBLE
                    rootCardView.alpha = 1f

                    if (response.body() != null) {
                        progressBar.visibility = View.INVISIBLE
                        rootCardView.alpha = 1f

                        for (item in response.body()!!) {
                            if (item.response == "SUCCESS") {
                                MToast("registered successfully!")
                                saveInfo()
                            }

                            if (item.response == "REGISTERED") {
                                MToast("This phone number is already registered!")
                            }

                            if (item.response == "ERROR") {
                                MToast("A problem network problem occurred!")
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ArrayList<RegisterResponse>>, t: Throwable) {
                    progressBar.visibility = View.INVISIBLE
                    rootCardView.alpha = 1f
                    Log.i("Amir", "Register Call  : " + t.message.toString())
                    MToast("Please Check Your Internet Connection!")
                }

            })
        }

        txtGoToLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

    }

    private fun saveInfo() {
        val sharedPreferences = getSharedPreferences("logInInfo", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("password", password)
        editor.putString("phoneNumber", phoneNumber)
        editor.putString("username", username)
        editor.apply()
    }
}