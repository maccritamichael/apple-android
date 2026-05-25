package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ITunesResponse(
    @Json(name = "resultCount") val resultCount: Int,
    @Json(name = "results") val results: List<ITunesAppResult>
)

@JsonClass(generateAdapter = true)
data class ITunesAppResult(
    @Json(name = "trackId") val trackId: Long?,
    @Json(name = "trackName") val trackName: String?,
    @Json(name = "artistName") val artistName: String?,
    @Json(name = "artworkUrl512") val artworkUrl512: String?,
    @Json(name = "artworkUrl100") val artworkUrl100: String?,
    @Json(name = "artworkUrl60") val artworkUrl60: String?,
    @Json(name = "price") val price: Double?,
    @Json(name = "formattedPrice") val formattedPrice: String?,
    @Json(name = "primaryGenreName") val primaryGenreName: String?,
    @Json(name = "averageUserRating") val averageUserRating: Double?,
    @Json(name = "userRatingCount") val userRatingCount: Int?,
    @Json(name = "trackViewUrl") val trackViewUrl: String?,
    @Json(name = "version") val version: String?,
    @Json(name = "releaseNotes") val releaseNotes: String?,
    @Json(name = "screenshotUrls") val screenshotUrls: List<String>?
)
