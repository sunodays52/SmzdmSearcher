package com.smzdm.searcher.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
    val name: String = "",
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
