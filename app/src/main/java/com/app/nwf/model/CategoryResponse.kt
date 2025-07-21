package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoryResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<ResourceCategory> = mutableListOf()
}