package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SubjectResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<SubjectData> = mutableListOf()
}

class SubjectData{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null
}