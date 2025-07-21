package com.app.nwf.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class PublicationResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    @SerializedName("total_page")
    @Expose
    var totalPage: Int? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<PublicationData> = mutableListOf()
}

class PublicationData{

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("pdf")
    @Expose
    var pdf: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
}