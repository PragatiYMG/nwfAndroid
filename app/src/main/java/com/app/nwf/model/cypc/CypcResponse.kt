package com.app.nwf.model.cypc

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CypcResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("banner")
    @Expose
    var banner: String? = null

    @SerializedName("data")
    @Expose
    var data: MutableList<CypcData> = mutableListOf()
}

class CypcData{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("files")
    @Expose
    var files: String? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

}