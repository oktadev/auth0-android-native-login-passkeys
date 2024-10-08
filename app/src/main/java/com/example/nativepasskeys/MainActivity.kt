package com.example.nativepasskeys

import ApiClient
import android.content.Intent
import android.credentials.GetCredentialException
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.credentials.exceptions.NoCredentialException
import com.auth0.android.Auth0
import com.auth0.android.result.Credentials
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.time.Instant
import java.util.Date


class MainActivity : ComponentActivity() {

  private lateinit var auth0: Auth0
  private lateinit var cachedCredentials: Credentials
  private val TAG: String = "MainActivity"
  private lateinit var credentialManager: CredentialManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Instantiate the Auth0 Client
    auth0 = Auth0(
      getString(R.string.auth0_client_id),
      getString(R.string.auth0_domain)
    )

    credentialManager = CredentialManager.create(this@MainActivity.baseContext);

    initWidgets()
  }

  private fun initWidgets(){
    val signUpBtn: Button = findViewById(R.id.main_signup_btn)
    val loginBtn: Button =  findViewById(R.id.main_login_btn)
    val email: EditText = findViewById(R.id.main_email_edittext)

    signUpBtn.setOnClickListener {
      if (TextUtils.isEmpty(email.text.toString())){
        email.error = "Email is mandatory"
      } else {
        email.error = null
        signUpWithNativePasskey(email.text.toString())
      }
    }

    loginBtn.setOnClickListener {
        loginWithNativePasskey()
    }
  }

  private fun loginWithNativePasskey(): String {
    val body = mapOf(
      "client_id" to auth0.clientId
    )

    Log.d(TAG, "calling /passkey/challenge...")
    ApiClient.apiService.startChallenge(body).enqueue(object: retrofit2.Callback<ChallengeResponse>{
      @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
      override fun onResponse(call: Call<ChallengeResponse>, response: Response<ChallengeResponse>) {
        // handle the response
        if (response.code() >= 400) {
          Log.d(TAG, "ERROR -- errorBody: " + response.errorBody()?.string())
        } else {
          Log.d(TAG, "SUCCESS -- " + response.body().toString())
          val body = response.body()!!

          val passkeyOption = GetPublicKeyCredentialOption(requestJson = Gson().toJson(body))
          val credRequest = GetCredentialRequest(listOf(
            passkeyOption
          ))

          Log.d(TAG, "About to call getCredential...")
          val coroutineScope = MainScope()
          val context = this@MainActivity
          coroutineScope.launch {
            try {
              val result = credentialManager.getCredential(
                // Use an activity-based context to avoid undefined system UI
                // launching behavior.
                context = context,
                request = credRequest
              )
              handleSignIn(result, body.authSession)
            } catch (e : GetCredentialException) {
              Log.d(TAG, "GetCredentialException: " + e.message)
            } catch (e: NoCredentialException){
              Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
          }
        }
      }

      override fun onFailure(call: Call<ChallengeResponse>, t: Throwable) {
        // handle the failure
        Log.d(TAG, "error!!")
        Log.d(TAG, t.message.toString())

        TODO("error handling")
      }
    })
    return ""
  }

  fun handleSignIn(result: GetCredentialResponse, authSession: String) {
    // Handle the successfully returned credential.
    when (val credential = result.credential) {
      is PublicKeyCredential -> {
        Log.d(TAG, "PublicKeyCredential!")
        // Share responseJson i.e. a GetCredentialResponse on your server to
        // validate and  authenticate
        callOauthTokenToCreateAccount(authSession, credential.authenticationResponseJson)
      } else -> {
      // Catch any unrecognized credential type here.
      Log.e(TAG, "Unexpected type of credential")
    }
    }
  }

  private fun signUpWithNativePasskey(email: String){
    Log.d(TAG, "calling signUpWithNativePasskey!")
    val body = mapOf(
      "client_id" to auth0.clientId,
      "user_profile" to mapOf(
        "email" to email
      )
    )

    ApiClient
      .apiService
      .signUpWithPasskey(body)
      .enqueue(object: retrofit2.Callback<ChallengeResponse>{

        override fun onResponse(call: Call<ChallengeResponse>, response: Response<ChallengeResponse>) {

          if (response.code() == 400) {
            Log.d(TAG, "errorBody: " + response.errorBody()?.string())
            Toast.makeText(applicationContext, response.errorBody().toString(), Toast.LENGTH_LONG).show()
          } else {
            Log.d(TAG, response.body().toString())
            val body = response.body()!!
            createCredential(body.authnParamsPublicKey, body.authSession)
          }
        }

        override fun onFailure(call: Call<ChallengeResponse>, t: Throwable) {
          Log.d(TAG, "error!!")
          Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
        }
      })
  }

  private fun createCredential(authnParamsPublicKey: AuthnParamsPublicKey,
                               authSession: String){
    Log.d(TAG, "CreateCredential with params $authnParamsPublicKey")

    val passkeyOption = CreatePublicKeyCredentialRequest(Gson().toJson(authnParamsPublicKey))

    val coroutineScope = MainScope()

    // runs concurrently
    coroutineScope.launch {
      try {
        val credential = credentialManager.createCredential(
          context = this@MainActivity.baseContext,
          request = passkeyOption
        ) as CreatePublicKeyCredentialResponse

        callOauthTokenToCreateAccount(authSession, credential.registrationResponseJson)
      } catch (e: CreateCredentialException) {
        Log.e("Failure", e.toString());
      } catch (e: CreateCredentialNoCreateOptionException){
        Log.e(TAG, "errorrrr: $e");
      }
    }
  }

  private fun callOauthTokenToCreateAccount(authSession: String, authnResponse: String){
    val body = mapOf<String, Any>(
      "grant_type" to "urn:okta:params:oauth:grant-type:webauthn",
      "scope" to "openid profile email",
      "client_id" to auth0.clientId,
      "auth_session" to authSession,
      "authn_response" to Gson().fromJson(authnResponse, AuthnResponse::class.java)
    );
    Log.d(TAG, body.toString())

    ApiClient.apiService.oAuthToken(body).enqueue(object: retrofit2.Callback<OAuthTokenResponse>{
      override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
        // handle the response
        Log.d(TAG, "getting tokens")
        Log.d(TAG, "errorBody: " + response.errorBody()?.string())
        Log.d(TAG, response.body().toString())
        val body = response.body()!!

        val expiresAt = body.expiresIn + Instant.now().epochSecond;

        val auth0Credentials = Credentials(
          idToken = body.idToken,
          accessToken = body.accessToken,
          refreshToken = body.refreshToken,
          type = body.tokenType,
          expiresAt = Date(expiresAt * 1000),
          scope= body.refreshToken,
        )
        handleAuth0Credential(auth0Credentials);
        Log.d("Success", body.toString())
        navigateToHomeActivity()
      }

      override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
        // handle the failure
        Log.d(TAG, "error getting tokens!!")
        Log.d(TAG, t.message.toString())

        TODO("error handling")
      }
    })
  }

  private fun handleAuth0Credential(credentials: Credentials) {
    cachedCredentials = credentials
    Log.d("Auth0 Id Token:", credentials.idToken);

//        showSnackBar("Success: ${credentials.accessToken}")
//        updateUI()
//        showUserProfile()
  }

  fun navigateToHomeActivity(){
    startActivity(Intent(this, HomeActivity::class.java))
  }
}