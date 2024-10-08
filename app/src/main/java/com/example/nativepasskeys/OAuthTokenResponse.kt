package com.example.nativepasskeys

import com.google.gson.annotations.SerializedName

data class OAuthTokenResponse (
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("refresh_token") var refreshToken: String? = null,
    @SerializedName("id_token") var idToken: String,
    @SerializedName("token_type") var tokenType: String,
    @SerializedName("expires_in") var expiresIn: Int

)