package com.alexpaxom.developerslife.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GifInfo(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("gifURL")
    @Expose
    var gifURL: String? = null,

    @SerializedName("previewURL")
    @Expose
    var previewURL: String? = null
)
