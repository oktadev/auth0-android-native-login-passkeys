package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName


data class User (

    @SerializedName("id"          ) var id          : String? = null,
    @SerializedName("name"        ) var name        : String? = null,
    @SerializedName("displayName" ) var displayName : String? = null

)