package com.smzdm.searcher.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {

    @Query("SELECT * FROM deals ORDER BY discoveredAt DESC")
    fun getAllDeals(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE productKeyword = :keyword ORDER BY discoveredAt DESC")
    fun getDealsByKeyword(keyword: String): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals ORDER BY discoveredAt DESC LIMIT :limit")
    fun getRecentDeals(limit: Int = 50): Flow<List<DealEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deal: DealEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(deals: List<DealEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM deals WHERE smzdmId = :smzdmId")
    suspend fun countBySmzdmId(smzdmId: Long): Int

    @Query("UPDATE deals SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("DELETE FROM deals")
    suspend fun deleteAll()
}
