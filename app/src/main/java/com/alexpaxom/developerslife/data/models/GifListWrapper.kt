package com.alexpaxom.developerslife.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GifListWrapper(
    @SerializedName("result")
    @Expose
    var images: ArrayList<GifInfo>
)
