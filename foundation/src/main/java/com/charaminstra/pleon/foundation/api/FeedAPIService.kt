package com.charaminstra.pleon.foundation.api

import com.charaminstra.pleon.foundation.model.*
import retrofit2.Response
import retrofit2.http.*

interface FeedAPIService{
    /* post feed */
    @POST("feed")
    suspend fun postFeed(
        @Header("Authorization") verifyToken:String,
        @Body feedRequestBody: FeedRequestBody
    ): Response<FeedResponse>

    /*get feed*/
    @GET("feed")
    suspend fun getFeed(
        @Header("Authorization") verifyToken:String,
        @Query("offset") offset:Int?,
        @Query("plant_id")  plantId:String?,
        @Query("publish_date") date:String?
    ): Response<FeedResponse>

    @DELETE("feed/{id}")
    suspend fun deleteFeedId(
        @Header("Authorization") accessToken:String,
        @Path("id") id: String
    ): Response<FeedResponse>

//    @GET("feed")
//    suspend fun getFeedIdAndDate(
//
//    )
}
