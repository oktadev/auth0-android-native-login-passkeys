package com.example.nativepasskeys

import com.google.gson.annotations.SerializedName


data class Rp (

    @SerializedName("id"   ) var id   : String? = null,
    @SerializedName("name" ) var name : String? = null

)