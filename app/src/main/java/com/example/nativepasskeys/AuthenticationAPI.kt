package com.example.nativepasskeys

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationAPI {
    @POST("passkey/register")
    @JvmSuppressWildcards
    fun signUpWithPasskey(@Body signUpBody: Map<String, Any>): Call<SignUpResponse>
}

