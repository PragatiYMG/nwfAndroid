package com.app.nwf.model.consult

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
class ConsultResponse {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<ConsultData> = mutableListOf()
}

