package com.pdm.pdmsemana6

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAGviajero{
    private const val BASE_URL = "https://arnoldjbs-pdm-s6.hf.space/"
    val aGviajeroAPI:AGviajeroAPI by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(AGviajeroAPI::class.java)
    }
}