package com.example.nativepasskeys.api.data

import com.google.gson.annotations.SerializedName

/**
 * {
 *   "authn_params_public_key": {
 *     "challenge": "<GENERATED CHALLENGE FOR THIS SESSION>",
 *     "timeout": <MILLISECONDS>,
 *     "rp": {
 *       "id": "<THE CUSTOM DOMAIN>",
 *       "name": "<APPLICATION NAME>"
 *     },
 *     "pubKeyCredParams": [
 *       { type: 'public-key', alg: -8 },
 *       { type: 'public-key', alg: -7 },
 *       { type: 'public-key', alg: -257 }
 *     ],
 *     "authenticatorSelection": {
 *       "residentKey": "required",
 *       "userVerification": "preferred"
 *     },
 *     "user": {
 *       "id": "<BASE64URL 64 RANDOM BYTES>",
 *       "name": "<USER-ENTERED IDENTIFIER>",
 *       "displayName": "<USER-ENTERED DISPLAY NAME OR IDENTIFIER IF MISSING"
 *     }
 *   },
 *   "auth_session": "<SESSION ID>"
 * }
 *
 * */
class ChallengeResponse {
    @SerializedName("authn_params_public_key") var authnParamsPublicKey : AuthnParamsPublicKey = AuthnParamsPublicKey()
    @SerializedName("auth_session")
    lateinit var authSession : String
}