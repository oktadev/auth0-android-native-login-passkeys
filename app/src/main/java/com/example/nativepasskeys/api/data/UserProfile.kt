package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName


data class UserProfile (

    @SerializedName("email") var email : String? = null,
    @SerializedName("name") var name : String? = null

)