package com.smzdm.searcher.service

import android.content.Context
import android.util.Log
import androidx.work.*
import com.smzdm.searcher.SmzdmApp
import com.smzdm.searcher.data.remote.SmzdmDeal
import java.util.concurrent.TimeUnit

/**
 * Worker that periodically checks smzdm for new deals on enabled keywords.
 * Runs every 15 minutes via WorkManager.
 */
class DealCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as SmzdmApp
        val repository = app.container.repository
        val apiService = app.container.apiService

        return try {
            val products = repository.getEnabledProducts()
            if (products.isEmpty()) {
                Log.d(TAG, "No enabled products to monitor")
                return Result.success()
            }

            var totalNewDeals = 0

            for (product in products) {
                try {
                    val apiDeals = apiService.fetchDealsByKeyword(product.keyword)
                    val newDeals = repository.saveNewDeals(apiDeals, product.keyword)

                    if (newDeals.isNotEmpty()) {
                        totalNewDeals += newDeals.size
                        // Send notification for each new deal
                        newDeals.forEach { deal ->
                            NotificationHelper.showDealNotification(
                                applicationContext,
                                deal.title,
                                deal.price,
                                deal.mall,
                                deal.id
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking keyword '${product.keyword}'", e)
                }
            }

            if (totalNewDeals > 0) {
                Log.d(TAG, "Found $totalNewDeals new deals")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Deal check failed", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        private const val TAG = "DealCheckWorker"

        /**
         * Schedules the periodic deal check (every 15 minutes).
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<DealCheckWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    1, TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        private const val WORK_NAME = "deal_check"
    }
}
