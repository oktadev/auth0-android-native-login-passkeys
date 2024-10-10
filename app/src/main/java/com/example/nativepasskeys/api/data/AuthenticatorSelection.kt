package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName


data class AuthenticatorSelection (

    @SerializedName("residentKey"      ) var residentKey      : String? = null,
    @SerializedName("userVerification" ) var userVerification : String? = null

)