package com.smzdm.searcher.data.repository

import com.smzdm.searcher.data.local.*
import com.smzdm.searcher.data.remote.SmzdmDeal
import kotlinx.coroutines.flow.Flow

class DealRepository(
    private val dealDao: DealDao,
    private val productDao: ProductDao
) {
    // ─── Products ───

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun getEnabledProducts(): List<ProductEntity> = productDao.getEnabledProducts()

    suspend fun addProduct(keyword: String, name: String = ""): Long {
        val product = ProductEntity(keyword = keyword, name = name.ifEmpty { keyword })
        return productDao.insert(product)
    }

    suspend fun deleteProduct(id: Long) = productDao.deleteById(id)

    suspend fun setProductEnabled(id: Long, enabled: Boolean) =
        productDao.setEnabled(id, enabled)

    // ─── Deals ───

    suspend fun getDealsByKeyword(keyword: String): Flow<List<DealEntity>> =
        dealDao.getDealsByKeyword(keyword)

    fun getAllDeals(): Flow<List<DealEntity>> = dealDao.getAllDeals()

    fun getRecentDeals(limit: Int = 50): Flow<List<DealEntity>> =
        dealDao.getRecentDeals(limit)

    /**
     * Saves a list of API deals, skipping any that already exist in the DB.
     * Returns only the newly inserted deals.
     */
    suspend fun saveNewDeals(apiDeals: List<SmzdmDeal>, keyword: String): List<DealEntity> {
        val newDeals = apiDeals.mapNotNull { deal ->
            // Check if already exists
            if (dealDao.countBySmzdmId(deal.id) == 0) {
                DealEntity(
                    smzdmId = deal.id,
                    productKeyword = keyword,
                    title = deal.title,
                    price = deal.price,
                    content = deal.content,
                    mall = deal.mall,
                    url = deal.url,
                    imageUrl = deal.imageUrl
                )
            } else null
        }

        if (newDeals.isNotEmpty()) {
            dealDao.insertAll(newDeals)
        }
        return newDeals
    }

    suspend fun markDealAsRead(id: Long) = dealDao.markAsRead(id)
}
