package com.turovfy.app.model

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("cover") val cover: String
)

data class SearchResponse(
    @SerializedName("results") val results: List<Track>
)

data class LyricsResponse(
    @SerializedName("type") val type: String,
    @SerializedName("lyrics") val lyrics: String
)

data class LyricsLine(
    val timeSeconds: Float,
    val text: String
)