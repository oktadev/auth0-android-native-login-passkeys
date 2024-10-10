package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName


data class AuthnParamsPublicKey (

  @SerializedName("challenge"              ) var challenge              : String?                     = null,
  @SerializedName("timeout"                ) var timeout                : Int?                        = null,
  @SerializedName("rp"                     ) var rp                     : Rp?                         = Rp(),
  @SerializedName("pubKeyCredParams"       ) var pubKeyCredParams       : ArrayList<PubKeyCredParams> = arrayListOf(),
  @SerializedName("authenticatorSelection" ) var authenticatorSelection : AuthenticatorSelection?     = AuthenticatorSelection(),
  @SerializedName("user"                   ) var user                   : User?                       = User(),
  @SerializedName("rpId") var rpId : String? = null,
  @SerializedName("userVerification") var userVerification : String? = null,
)