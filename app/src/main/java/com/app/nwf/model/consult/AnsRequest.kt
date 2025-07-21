package com.app.nwf.model.consult

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnsRequest {
    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("answers")
    @Expose
    var answers: List<Answer>? = null
}

class Answer {
    @SerializedName("question_id")
    @Expose
    var questionId: Int? = null

    @SerializedName("answer_id")
    @Expose
    var answerId: Int? = null

    @SerializedName("answer")
    @Expose
    var answer: String? = null

    @SerializedName("types")
    @Expose
    var types: String? = null
}