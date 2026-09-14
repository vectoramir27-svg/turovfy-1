package com.turovfy.app.network

import com.turovfy.app.model.LyricsResponse
import com.turovfy.app.model.SearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("api/search")
    suspend fun searchTracks(@Query("query") query: String): SearchResponse

    @GET("api/wave")
    suspend fun getWave(
        @Query("seed_id") seedId: String = "",
        @Query("artist") artist: String = ""
    ): SearchResponse

    @GET("api/lyrics")
    suspend fun getLyrics(
        @Query("track") track: String,
        @Query("artist") artist: String
    ): LyricsResponse

    companion object {
        const val BASE_URL = "http://10.0.2.2:8000/"

        fun getStreamUrl(trackId: String): String {
            return "${BASE_URL}api/listen/$trackId"
        }

        fun create(): ApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
