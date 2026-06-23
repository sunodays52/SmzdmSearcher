package com.smzdm.searcher.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Represents a single deal from the smzdm.com JSON API.
 */
data class SmzdmDeal(
    @SerializedName("article_id")
    val id: Long,

    @SerializedName("article_title")
    val title: String,

    @SerializedName("article_price")
    val price: String,

    @SerializedName("article_content")
    val content: String = "",

    @SerializedName("article_mall")
    val mall: String = "",

    @SerializedName("article_url")
    val url: String = "",

    @SerializedName("article_pic")
    val imageUrl: String = "",

    @SerializedName("article_date")
    val date: String = "",

    @SerializedName("article_type")
    val type: String = "",

    @SerializedName("article_worthy")
    val worthy: Int = 0
)
