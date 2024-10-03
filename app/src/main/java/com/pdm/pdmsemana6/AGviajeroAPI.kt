package com.pdm.pdmsemana6

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface AGviajeroAPI{
    @POST("/predict/")
    fun predict(@Body request: RequestData): Call<ResponseData>
}