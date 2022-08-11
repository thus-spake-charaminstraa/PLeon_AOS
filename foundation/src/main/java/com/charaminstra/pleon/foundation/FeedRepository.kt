package com.charaminstra.pleon.foundation

import com.charaminstra.pleon.foundation.api.FeedAPIService
import com.charaminstra.pleon.foundation.api.PleonPreference
import com.charaminstra.pleon.foundation.model.FeedRequestBody
import javax.inject.Inject

class FeedRepository @Inject constructor(private val service: FeedAPIService, private val prefs: PleonPreference){
    suspend fun postFeed(plantId : String, date: String, kind: String, content: String, url: String?) = service.postFeed(
        prefs.getAccessToken(),
        FeedRequestBody(plantId, date, kind, content, url))

    suspend fun getFeedList(offset: Int?,plantId: String?, date: String?) = service.getFeed(
        prefs.getAccessToken(),
        offset,
        plantId,
        date)

    suspend fun getFeedTabList() = service.getFeedTabList(prefs.getAccessToken())

    suspend fun getFeedId(feedId:String) = service.getFeedId(
        prefs.getAccessToken(),
        feedId)

    suspend fun deleteFeedId(feedId: String) = service.deleteFeedId(
        prefs.getAccessToken(),
        feedId
    )
}

