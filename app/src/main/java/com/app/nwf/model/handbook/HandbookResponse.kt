package com.app.nwf.model.handbook

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class HandbookResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    @SerializedName("total_page")
    @Expose
    var total_page: Int? = null

    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<HandbookData> = mutableListOf()
}