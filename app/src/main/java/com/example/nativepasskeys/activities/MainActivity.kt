package com.example.nativepasskeys.activities

import android.content.Intent
import android.os.Bundle
import android.os.CancellationSignal
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.credentials.exceptions.GetCredentialException
import com.auth0.android.Auth0
import com.auth0.android.result.Credentials
import com.example.nativepasskeys.api.data.AuthnParamsPublicKey
import com.example.nativepasskeys.R
import com.example.nativepasskeys.api.data.AuthnResponse
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.request.PublicKeyCredentials
import com.auth0.android.request.UserData
import com.auth0.android.result.PasskeyChallenge
import com.auth0.android.result.PasskeyRegistrationChallenge
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
  private val TAG: String = "MainActivity"
  private val REALM: String = "Username-Password-Authentication"

  private lateinit var auth0: Auth0
  private lateinit var cachedCredentials: Credentials
  private lateinit var credentialManager: CredentialManager
  private lateinit var apiClient: AuthenticationAPIClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Instantiate the Auth0 Client
    auth0 = Auth0.getInstance(getString(R.string.auth0_client_id), getString(R.string.auth0_domain))
    apiClient = AuthenticationAPIClient(auth0)

    credentialManager = CredentialManager.create(this@MainActivity.baseContext);

    initWidgets()
  }

  private val credentialsManager: CredentialsManager by lazy {
    val storage = SharedPreferencesStorage(this@MainActivity)
    val manager = CredentialsManager(apiClient, storage)
    manager
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

    loginBtn.setOnClickListener { loginWithNativePasskey() }
  }

  private fun loginWithNativePasskey() {
    Log.d(TAG, "Starting signin...")

    apiClient.passkeyChallenge(REALM)
      .start(object : Callback<PasskeyChallenge, AuthenticationException> {
        override fun onSuccess(result: PasskeyChallenge) {
          val passkeyChallengeResponse = result
          val request =
            GetPublicKeyCredentialOption(Gson().toJson(passkeyChallengeResponse.authParamsPublicKey))
          val getCredRequest = GetCredentialRequest(
            listOf(request)
          )

          credentialManager.getCredentialAsync(this@MainActivity,
            getCredRequest,
            CancellationSignal(),
            Executors.newSingleThreadExecutor(),
            object :
              CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
              override fun onError(e: GetCredentialException) {
              }

              override fun onResult(result: GetCredentialResponse) {
                when (val credential = result.credential) {
                  is PublicKeyCredential -> {
                    val authRequest = Gson().fromJson(
                      credential.authenticationResponseJson,
                      PublicKeyCredentials::class.java
                    )
                    apiClient.signinWithPasskey(
                      passkeyChallengeResponse.authSession,
                      authRequest,
                      REALM
                    )
                      .validateClaims()
                      .start(object :
                        Callback<Credentials, AuthenticationException> {
                        override fun onSuccess(result: Credentials) {
                          credentialsManager.saveCredentials(result)
                          Log.d(TAG, "SUCCESS: " + result.idToken)
                        }

                        override fun onFailure(error: AuthenticationException) {
                          Log.d(TAG, "error on signin: " + error.getDescription())
                        }
                      })
                  }

                  else -> {
                    Log.d(TAG, "Received unrecognized credential type ${credential.type}.This shouldn't happen")
                  }
                }
              }
            })
        }

        override fun onFailure(error: AuthenticationException) {
          Log.d(TAG, "error on signin2: " + error.getDescription())
        }
      })

  }

  private fun signUpWithNativePasskey(email: String){
    Log.d(TAG, "calling signUpWithNativePasskey!")

    val userData = UserData(email = email)

    apiClient.signupWithPasskey(
      userData, REALM
    ).start(object : Callback<PasskeyRegistrationChallenge, AuthenticationException> {

      override fun onSuccess(result: PasskeyRegistrationChallenge) {
        val passKeyRegistrationChallenge = result
        val request = CreatePublicKeyCredentialRequest(
          Gson().toJson(
            passKeyRegistrationChallenge.authParamsPublicKey
          )
        )
        var response: CreatePublicKeyCredentialResponse?

        credentialManager.createCredentialAsync(
          this@MainActivity,
          request,
          CancellationSignal(),
          Executors.newSingleThreadExecutor(),
          object :
            CredentialManagerCallback<CreateCredentialResponse, CreateCredentialException> {

            override fun onError(e: CreateCredentialException) {
              Log.e(TAG, "Error creating credential, " + e.message)
            }

            override fun onResult(result: CreateCredentialResponse) {

              response = result as CreatePublicKeyCredentialResponse
              val authRequest = Gson().fromJson(
                response?.registrationResponseJson,
                PublicKeyCredentials::class.java
              )

              Log.d(TAG, "Starting signin...")
              apiClient.signinWithPasskey(
                passKeyRegistrationChallenge.authSession,
                authRequest,
                REALM
              )
                .validateClaims()
                .start(object : Callback<Credentials, AuthenticationException> {
                  override fun onSuccess(result: Credentials) {
                    credentialsManager.saveCredentials(result)
                    Log.d(TAG, "SUCCESS" + result.idToken)
                  }

                  override fun onFailure(error: AuthenticationException) {
                    Log.d(TAG, "Failure signing in " + error.getDescription())
                  }
                })
            }
          })
      }

      override fun onFailure(error: AuthenticationException) {
        Log.d(TAG, "Failure creating passkey " + error.getDescription())
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

  fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    val keys = keys()
    while (keys.hasNext()) {
      val key = keys.next()
      val value = get(key)
      map[key] = when (value) {
        is JSONObject -> value.toMap()
        else -> value
      }
    }
    return map
  }

  private fun callOauthTokenToCreateAccount(authSession: String, authnResponse: String){

    val parsedAuthnResponse = Gson().fromJson(authnResponse, AuthnResponse::class.java)
    Log.d(TAG, "authnResponse: $authnResponse")
    val body = mapOf(
      "grant_type" to "urn:okta:params:oauth:grant-type:webauthn",
      "scope" to "openid profile email",
      "client_id" to auth0.clientId,
      "auth_session" to authSession,
      "authn_response" to parsedAuthnResponse
    )

//    ApiClient.apiService.oAuthToken(body).enqueue(object: Callback<OAuthTokenResponse>{
//      override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
//        Log.d(TAG, "getting tokens")
//        Log.d(TAG, "errorBody: " + response.errorBody()?.string())
//        Log.d(TAG, response.body().toString())
//        val body = response.body()!!
//
//        val expiresAt = body.expiresIn + Instant.now().epochSecond;
//
//        val auth0Credentials = Credentials(
//          idToken = body.idToken,
//          accessToken = body.accessToken,
//          refreshToken = body.refreshToken,
//          type = body.tokenType,
//          expiresAt = Date(expiresAt * 1000),
//          scope= body.refreshToken,
//        )
//
//        handleAuth0Credential(auth0Credentials);
//        Log.d("Success", body.toString())
//        navigateToHomeActivity()
//      }
//
//      override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
//        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
//      }
//    })
  }

  private fun handleAuth0Credential(credentials: Credentials) {
    cachedCredentials = credentials
    Log.d(TAG, "ID Token: " + credentials.idToken);
//        showSnackBar("Success: ${credentials.accessToken}")
//        updateUI()
//        showUserProfile()
  }

  fun navigateToHomeActivity(){
    startActivity(Intent(this, HomeActivity::class.java))
  }
}