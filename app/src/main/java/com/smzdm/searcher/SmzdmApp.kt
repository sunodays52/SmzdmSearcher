package com.smzdm.searcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.smzdm.searcher.data.local.AppDatabase
import com.smzdm.searcher.data.remote.SmzdmApiService
import com.smzdm.searcher.data.repository.DealRepository
import com.smzdm.searcher.di.AppContainer
import com.smzdm.searcher.service.DealCheckWorker
import java.util.concurrent.TimeUnit

class SmzdmApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Create notification channel
        createNotificationChannel()

        // Initialize DI container
        val database = AppDatabase.getInstance(this)
        val apiService = SmzdmApiService()
        val repository = DealRepository(database.dealDao(), database.productDao())
        container = AppContainer(database, apiService, repository)

        // Schedule periodic deal checking
        scheduleDealCheck()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_DEALS,
                getString(R.string.channel_deals_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_deals_desc)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun scheduleDealCheck() {
        val workRequest = PeriodicWorkRequestBuilder<DealCheckWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DEAL_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    companion object {
        const val CHANNEL_DEALS = "smzdm_deals"
        const val DEAL_CHECK_WORK_NAME = "deal_check_work"
        lateinit var instance: SmzdmApp
            private set
    }
}
