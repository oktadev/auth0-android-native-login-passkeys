package com.example.nativepasskeys

import ApiClient
import RetrofitClient
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.auth0.android.Auth0
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response


class MainActivity : ComponentActivity() {

    private lateinit var auth0: Auth0
    private val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instantiate the Auth0 Client
        auth0 = Auth0(
            getString(R.string.auth0_client_id),
            getString(R.string.auth0_domain)
        )

        initWidgets()
    }

    private fun initWidgets(){
        val signUpBtn = findViewById<View>(R.id.main_signup_btn) as Button
        val loginBtn = findViewById<View>(R.id.main_login_btn) as Button
        val logoutBtn = findViewById<View>(R.id.main_logout_btn) as Button
        val email = findViewById<EditText>(R.id.main_email_edittext)

        signUpBtn.setOnClickListener {
            if (TextUtils.isEmpty(email.text.toString())){
                Log.d(TAG,"HOLA!!!! empty " + email)
                email.error = "Email is mandatory"
            } else {
                email.error = null
                Log.d(TAG,"HOLA!!!! non empty " + email)
                signUpWithNativePasskey(email.text.toString())
            }

        }
    }

    private fun signUpWithNativePasskey(email: String){
        Log.d(TAG, "calling signUpWithNativePasskey!")
        getPasskeyChallenge(email)
    }

    private fun getPasskeyChallenge(email: String){
        val body = mapOf(
            "client_id" to auth0.clientId,
            "user_profile" to mapOf(
                "email" to email
            )
        )
        ApiClient.apiService.signUpWithPasskey(body).enqueue(object: retrofit2.Callback<SignUpResponse>{
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                // handle the response
                Log.d(TAG, "all goooooood ")
                Log.d(TAG, "errorBody: " + response.errorBody()?.string())
                Log.d(TAG, response.toString())
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                // handle the failure
                Log.d(TAG, "error!!")
                Log.d(TAG, t.message.toString())
            }
        })


    }

    private fun createCredential(){
        TODO("Call CredentialManager")
    }

    private fun callOauthTokenToCreateAccount(){
        TODO("Call POST oauth/token and retrieve tokens")
    }
}