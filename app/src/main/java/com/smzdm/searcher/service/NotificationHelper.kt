package com.smzdm.searcher.service

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smzdm.searcher.MainActivity
import com.smzdm.searcher.R
import com.smzdm.searcher.SmzdmApp

object NotificationHelper {

    private var notificationId = 1000

    /**
     * Shows a notification for a new deal.
     */
    fun showDealNotification(
        context: Context,
        title: String,
        price: String,
        mall: String,
        dealId: Long
    ) {
        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("deal_id", dealId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, dealId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationTitle = if (price.isNotBlank()) {
            "💰 $price"
        } else {
            context.getString(R.string.notif_new_deal_title)
        }

        val notificationText = buildString {
            append(title)
            if (mall.isNotBlank()) {
                append(" · $mall")
            }
        }

        val notification = NotificationCompat.Builder(context, SmzdmApp.CHANNEL_DEALS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId++, notification)
    }

    /**
     * Shows a summary notification when multiple deals are found.
     */
    fun showBatchNotification(
        context: Context,
        count: Int,
        keyword: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SmzdmApp.CHANNEL_DEALS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("发现 $count 个好价")
            .setContentText("关键词「$keyword」有 $count 条新好价")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("关键词「$keyword」有 $count 条新好价，点击查看"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId++, notification)
    }
}
