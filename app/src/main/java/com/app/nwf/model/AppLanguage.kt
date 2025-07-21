package com.app.nwf.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AppLanguage {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("code")
    @Expose
    var code: String? = null

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