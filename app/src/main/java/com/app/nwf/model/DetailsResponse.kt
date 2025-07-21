package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DetailsResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<DetailsData> = mutableListOf()

}

class DetailsData{
    @SerializedName("subject")
    @Expose
    var subject: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

}