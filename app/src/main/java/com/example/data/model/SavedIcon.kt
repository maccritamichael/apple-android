package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_icons")
data class SavedIcon(
    @PrimaryKey val id: String,
    val name: String,
    val developer: String,
    val artworkUrl512: String,
    val artworkUrl100: String,
    val formattedPrice: String,
    val primaryGenreName: String,
    val averageUserRating: Double,
    val trackViewUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)
