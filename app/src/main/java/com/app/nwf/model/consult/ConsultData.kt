package com.app.nwf.model.consult

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ConsultData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("question")
    @Expose
    var question: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("types")
    @Expose
    var types: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: Any? = null

    @SerializedName("answer")
    @Expose
    var answer: String = ""

    @SerializedName("answer_id")
    @Expose
    var answerId: MutableList<AnswerData> = mutableListOf()
}