package com.intermercato.iws_m.api

import com.intermercato.iws_m.realmModels.OtdEvent
import com.intermercato.iws_m.realmModels.OtdOrder
import com.intermercato.iws_m.realmModels.OtdWeight
import retrofit2.http.*

interface ApiRequests {

    @GET
    suspend fun getOrders(@Header("x-api-key") apiKey:String?, @Query("page") page: Int, @Query("page") pageSize : Int) : OtdOrder

    @POST
    suspend fun postWeigh(@Header("x-api-key") apiKey:String?) : OtdWeight

    @POST
    suspend fun postEvent(@Header("x-api-key") apiKey:String?) : OtdEvent
}