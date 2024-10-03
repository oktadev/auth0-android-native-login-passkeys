package com.example.nativepasskeys

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.auth0.android.Auth0


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


    }

    private fun getPasskeyChallenge(email: String){
        val url = "https://${R.string.auth0_domain}/passkey/register";
        val payload = mapOf(
            "client_id" to auth0.clientId,
            "user_profile" to mapOf(
                "email" to email
            )
        )


    }

    private fun createCredential(){
        TODO("Call CredentialManager")
    }

    private fun callOauthTokenToCreateAccount(){
        TODO("Call POST oauth/token and retrieve tokens")
    }
}