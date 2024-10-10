package com.example.nativepasskeys.api.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthnResponse {
    @SerializedName("rawId"                   ) var rawId                   : String?                 = null
    @SerializedName("authenticatorAttachment" ) var authenticatorAttachment : String?                 = null
    @SerializedName("type"                    ) var type                    : String?                 = null
    @SerializedName("id"                      ) var id                      : String?                 = null
    @SerializedName("response"                ) var response                : Response?               = Response()
    @SerializedName("clientExtensionResults"  ) var clientExtensionResults  : ClientExtensionResults? = ClientExtensionResults()

    data class Response (
        @SerializedName("clientDataJSON"     ) var clientDataJSON     : String?           = null,
        @SerializedName("attestationObject"  ) var attestationObject  : String?           = null,

        // login specific
        @SerializedName("authenticatorData" ) var authenticatorData : String? = null,
        @SerializedName("signature"         ) var signature         : String? = null,
        @SerializedName("userHandle"        ) var userHandle        : String? = null
    )

    data class CredProps (

        @SerializedName("rk" ) var rk : Boolean? = null

    )


    data class ClientExtensionResults (

        @SerializedName("credProps" ) var credProps : CredProps? = CredProps()

    )
}