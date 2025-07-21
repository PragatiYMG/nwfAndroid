package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LanguageResponse {
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
    var data: MutableList<LanguageData> = mutableListOf()
}

class LanguageData{
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