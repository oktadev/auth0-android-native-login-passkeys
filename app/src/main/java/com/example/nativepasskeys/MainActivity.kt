package com.example.nativepasskeys

import ApiClient
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import com.auth0.android.Auth0
import com.auth0.android.result.Credentials
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.time.Instant
import java.util.Date


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

        // Set up progress before call
        val progressBar = findViewById<ProgressBar>(R.id.main_progress_bar)
        progressBar.visibility = View.VISIBLE

        ApiClient.apiService.signUpWithPasskey(body).enqueue(object: retrofit2.Callback<SignUpResponse>{
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                // handle the response
                progressBar.visibility = View.GONE
                Log.d(TAG, "all goooooood ")
                Log.d(TAG, "errorBody: " + response.errorBody()?.string())
                Log.d(TAG, response.body().toString())
                val body = response.body()!!
                createCredential(body.authnParamsPublicKey, body.authSession)
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                // handle the failure
                progressBar.visibility = View.GONE
                Log.d(TAG, "error!!")
                Log.d(TAG, t.message.toString())

                TODO("error handling")
            }
        })


    }

    private fun  createCredential(authnParamsPublicKey: AuthnParamsPublicKey,
                                 authSession: String){
        Log.d(TAG, "CreateCredential with params $authnParamsPublicKey")

        val passkeyOption = CreatePublicKeyCredentialRequest(Gson().toJson(authnParamsPublicKey))

        val credentialManager = CredentialManager.create(this@MainActivity.baseContext);
        val coroutineScope = MainScope()

        // runs concurrently
        coroutineScope.launch {
            try {
                Log.d(TAG, "inside coroutine")
                Log.d(TAG, passkeyOption.toString())
                val creds = credentialManager.createCredential(
                    context = this@MainActivity.baseContext,
                    request = passkeyOption
                ) as CreatePublicKeyCredentialResponse
                Log.wtf(TAG, "IM SCREAMING")
                Log.d(TAG, creds.registrationResponseJson)
                val jsonObject = JSONObject(creds.registrationResponseJson)
//                val dict = jsonObject.toMap()
//                val oauthTokenResponse = callOAuthToken(dict, authSession)
//                val expiresAt = oauthTokenResponse.getInt("expires_in") + Instant.now().epochSecond;
//
//                val a0Creds = Credentials(
//                    idToken = oauthTokenResponse.getString("id_token"),
//                    accessToken = oauthTokenResponse.getString("access_token"),
//                    refreshToken = if (oauthTokenResponse.has("refresh_token")) oauthTokenResponse.getString(
//                        "refresh_token"
//                    ) else null,
//                    type = oauthTokenResponse.getString("token_type"),
//                    expiresAt = Date(expiresAt * 1000),
//                    scope = if (oauthTokenResponse.has("refresh_token")) oauthTokenResponse.getString(
//                        "refresh_token"
//                    ) else null,
//                )
//
//                handleAuth0Credential(a0Creds);
//                Log.d("Success", oauthTokenResponse.toString())
            } catch (e: CreateCredentialException) {
                Log.e("Failure", e.toString());
            } catch (e: CreateCredentialNoCreateOptionException){
                Log.e(TAG, "errorrrr: $e");
            }
        }
    }

    private fun callOauthTokenToCreateAccount(){
        TODO("Call POST oauth/token and retrieve tokens")
    }
}