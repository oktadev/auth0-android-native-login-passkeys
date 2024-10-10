package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName

class AuthnParamsPublicKeyLogin {
  @SerializedName("challenge"              ) var challenge              : String?                     = null
  @SerializedName("timeout"                ) var timeout                : Int?                        = null
  @SerializedName("rpId") var rpId : String? = null
  @SerializedName("userVerification") var userVerification : String? = null
//  @SerializedName("allowCredentials") var allowCredentials = intArrayOf()
}