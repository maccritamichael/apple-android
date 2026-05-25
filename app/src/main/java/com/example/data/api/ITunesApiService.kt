package com.example.data.api

import com.example.data.model.ITunesResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    suspend fun searchApps(
        @Query("term") term: String,
        @Query("media") media: String = "software",
        @Query("entity") entity: String = "software",
        @Query("limit") limit: Int = 40,
        @Query("country") country: String = "us"
    ): ITunesResponse

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"

        val instance: ITunesApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(ITunesApiService::class.java)
        }
    }
}
