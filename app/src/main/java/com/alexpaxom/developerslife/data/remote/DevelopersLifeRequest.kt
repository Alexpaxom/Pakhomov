package com.alexpaxom.developerslife.data.remote

import com.alexpaxom.developerslife.data.models.GifInfo
import com.alexpaxom.developerslife.data.models.GifListWrapper
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface DevelopersLifeGifRequest {
    @GET("/random?json=true")
    fun getRandom(): Single<GifInfo>

    @GET("/latest/{pageNum}?json=true")
    fun getLatest(
        @Path("pageNum") pageNum:Int
    ): Single<GifListWrapper>
}