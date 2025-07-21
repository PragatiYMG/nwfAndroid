package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ContactResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null

    @SerializedName("total_page")
    @Expose
    var total_page: Int? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<ContactData> = mutableListOf()
}