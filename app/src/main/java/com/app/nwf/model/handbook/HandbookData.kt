package com.app.nwf.model.handbook

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HandbookData {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("pdf")
    @Expose
    var pdf: String? = null

}