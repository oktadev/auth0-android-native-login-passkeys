package com.example.nativepasskeys.api

import com.example.nativepasskeys.api.data.ChallengeResponse
import com.example.nativepasskeys.api.data.OAuthTokenResponse
import com.example.nativepasskeys.api.data.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationAPI {
    @POST("passkey/register")
    @JvmSuppressWildcards
    fun signUpWithPasskey(@Body signUpBody: Map<String, Any>): Call<ChallengeResponse>

    @POST("oauth/token")
    @JvmSuppressWildcards
    fun oAuthToken(@Body oAuthTokenBody: Map<String, Any>): Call<OAuthTokenResponse>

    @POST("passkey/challenge")
    @JvmSuppressWildcards
    fun startChallenge(@Body body: Map<String, Any>): Call<RegisterResponse>
}

