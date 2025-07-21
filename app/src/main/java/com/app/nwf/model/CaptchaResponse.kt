package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CaptchaResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("data")
    @Expose
    var data: String? = null
}