package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName


data class PubKeyCredParams (

    @SerializedName("type" ) var type : String? = null,
    @SerializedName("alg"  ) var alg  : Int?    = null

)