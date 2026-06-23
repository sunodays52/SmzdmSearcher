package com.smzdm.searcher.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smzdm.searcher.data.local.DealEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class SmzdmApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val gson = Gson()

    /**
     * Fetches deals for a given keyword from the smzdm.com JSON API.
     * Returns a list of DealEntity objects that match the keyword.
     */
    suspend fun fetchDealsByKeyword(keyword: String): List<SmzdmDeal> = withContext(Dispatchers.IO) {
        val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
        val url = "https://www.smzdm.com/json_more?page=1&s=$encodedKeyword"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Referer", "https://www.smzdm.com/")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext emptyList()

        val listType = object : TypeToken<List<SmzdmDeal>>() {}.type
        val deals: List<SmzdmDeal> = gson.fromJson(body, listType) ?: emptyList()

        // Filter results that contain the keyword in the title (case-insensitive)
        val lowerKeyword = keyword.lowercase()
        deals.filter { deal ->
            deal.title.lowercase().contains(lowerKeyword) ||
                deal.content.lowercase().contains(lowerKeyword)
        }
    }

    /**
     * Fetches the latest deals across all categories (no keyword filter).
     */
    suspend fun fetchLatestDeals(): List<SmzdmDeal> = withContext(Dispatchers.IO) {
        val url = "https://www.smzdm.com/json_more?page=1"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Referer", "https://www.smzdm.com/")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext emptyList()

        val listType = object : TypeToken<List<SmzdmDeal>>() {}.type
        gson.fromJson<List<SmzdmDeal>>(body, listType).orEmpty()
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.230 Mobile Safari/537.36"
    }
}
