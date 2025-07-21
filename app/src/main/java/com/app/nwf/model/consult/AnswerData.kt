package com.app.nwf.model.consult

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class AnswerData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("consultation_option")
    @Expose
    var consultationOption: String? = null

    @SerializedName("consultation_id")
    @Expose
    var consultationId: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null
}