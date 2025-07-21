package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TrainingResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("total_page")
    @Expose
    var total_page: Int? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<TrainingData> = mutableListOf()
}

class TrainingData{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

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