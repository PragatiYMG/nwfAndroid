package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonResponse {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}