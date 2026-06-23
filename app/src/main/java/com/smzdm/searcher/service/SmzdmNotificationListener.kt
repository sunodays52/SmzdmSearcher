package com.smzdm.searcher.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.smzdm.searcher.data.local.DealEntity
import com.smzdm.searcher.data.repository.DealRepository
import kotlinx.coroutines.*

/**
 * Listens to notifications from the installed smzdm app.
 * Captures deal info from notification content and stores it in the local DB.
 */
class SmzdmNotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var repository: DealRepository? = null

    override fun onCreate() {
        super.onCreate()
        val app = applicationContext as com.smzdm.searcher.SmzdmApp
        repository = app.container.repository
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        // Only process notifications from the smzdm app
        if (packageName != SMZDM_PACKAGE) return

        val notification = sbn.notification
        val extras = notification.extras ?: return

        val title = extras.getString(KEY_TITLE) ?: return
        val text = extras.getString(KEY_TEXT) ?: ""

        // Extract deal info from notification text
        val dealInfo = parseDealFromNotification(title, text) ?: return

        scope.launch {
            try {
                val repo = repository ?: return@launch
                // Generate a stable ID from hash to avoid duplicates
                val id = dealInfo.title.hashCode().toLong() and 0x7FFFFFFFL

                val dealEntity = DealEntity(
                    smzdmId = id,
                    productKeyword = "_notification",
                    title = dealInfo.title,
                    price = dealInfo.price,
                    mall = dealInfo.mall,
                    url = dealInfo.url,
                    discoveredAt = sbn.postTime
                )

                repo.saveNewDeals(
                    listOf(
                        com.smzdm.searcher.data.remote.SmzdmDeal(
                            id = id,
                            title = dealInfo.title,
                            price = dealInfo.price,
                            mall = dealInfo.mall,
                            url = dealInfo.url,
                            content = dealInfo.content
                        )
                    ),
                    "_notification"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    /**
     * Parses deal information from a notification's title and text.
     * Returns null if it doesn't look like a deal notification.
     */
    private fun parseDealFromNotification(
        title: String,
        text: String
    ): DealNotification? {
        // If title contains price indicators, it's likely a deal
        val hasPrice = title.contains("¥") || title.contains("元") ||
            title.contains("$") || title.contains("€")

        if (!hasPrice && text.isBlank()) return null

        // Try to extract price from title
        val pricePattern = Regex("""[¥￥$€]?\d+[.,]?\d*""")
        val priceMatch = pricePattern.find(title)
        val price = priceMatch?.value ?: ""

        return DealNotification(
            title = title.replace(price, "").trim('-', ' ', '·'),
            price = price,
            content = text,
            mall = "", // Mall info is usually not in notifications
            url = ""
        )
    }

    data class DealNotification(
        val title: String,
        val price: String,
        val content: String,
        val mall: String,
        val url: String
    )

    companion object {
        private const val TAG = "SmzdmNotifListener"
        private const val SMZDM_PACKAGE = "com.smzdm.client.android"
        private const val KEY_TITLE = "android.title"
        private const val KEY_TEXT = "android.text"
    }
}
