package com.example.nativepasskeys.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.nativepasskeys.R

class HomeActivity : ComponentActivity() {
    private val TAG = "HomeActivity"

    private val auth0: Auth0 by lazy {
        val account = Auth0.getInstance(
            getString(R.string.auth0_client_id),
            getString(R.string.auth0_domain)
        )
        account
    }

    private val apiClient: AuthenticationAPIClient by lazy {
        AuthenticationAPIClient(auth0)
    }
    private val credentialsManager: CredentialsManager by lazy {
        val storage = SharedPreferencesStorage(this@HomeActivity)
        val manager = CredentialsManager(apiClient, storage)
        manager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        Log.d(TAG, credentialsManager.hasValidCredentials().toString())
        val greeting = findViewById<TextView>(R.id.home_greeting_txt)
        val idToken = findViewById<TextView>(R.id.home_token)

        credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
            override fun onSuccess(credentials: Credentials) {
                val jwt = JWT(credentials.idToken)
                greeting.text = (getString(R.string.home_welcome, jwt.claims["name"]?.asString()))
                idToken.text = credentials.idToken
            }

            override fun onFailure(error: CredentialsManagerException) {
                // No credentials were previously saved or they couldn't be refreshed
                Log.d(TAG, "")
            }
        })

        val logoutBtn = findViewById<Button>(R.id.home_logout_btn)
        logoutBtn.setOnClickListener {
            logout()
        }

    }

    private fun logout(){
        credentialsManager.clearCredentials()
        finish()
    }
}