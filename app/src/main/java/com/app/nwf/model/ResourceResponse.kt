package com.app.nwf.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ResourceResponse {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("total_page")
    @Expose
    var total_page: Int? = null

    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<ResourceData> = mutableListOf()
}

class ResourceData {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

}