package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName

class RegisterResponse {
  @SerializedName("authn_params_public_key") var authnParamsPublicKey : AuthnParamsPublicKeyLogin = AuthnParamsPublicKeyLogin()
  @SerializedName("auth_session")
  lateinit var authSession : String
}