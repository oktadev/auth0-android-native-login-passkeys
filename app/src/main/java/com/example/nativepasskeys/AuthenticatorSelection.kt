package com.example.nativepasskeys

import com.google.gson.annotations.SerializedName


data class AuthenticatorSelection (

    @SerializedName("residentKey"      ) var residentKey      : String? = null,
    @SerializedName("userVerification" ) var userVerification : String? = null

)