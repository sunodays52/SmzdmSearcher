package com.smzdm.searcher.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deals",
    indices = [Index(value = ["smzdmId"], unique = true)]
)
data class DealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val smzdmId: Long,
    val productKeyword: String,
    val title: String,
    val price: String,
    val content: String = "",
    val mall: String = "",
    val url: String = "",
    val imageUrl: String = "",
    val discoveredAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
