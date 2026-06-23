package com.smzdm.searcher.di

import com.smzdm.searcher.data.local.AppDatabase
import com.smzdm.searcher.data.remote.SmzdmApiService
import com.smzdm.searcher.data.repository.DealRepository

class AppContainer(
    val database: AppDatabase,
    val apiService: SmzdmApiService,
    val repository: DealRepository
)
