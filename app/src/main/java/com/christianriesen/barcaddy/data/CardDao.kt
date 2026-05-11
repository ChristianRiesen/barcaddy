package com.christianriesen.barcaddy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY position ASC")
    fun observeAll(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): Card?

    @Query("SELECT COALESCE(MAX(position), -1) FROM cards")
    suspend fun maxPosition(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(card: Card)

    @Update
    suspend fun update(card: Card)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM cards")
    suspend fun deleteAll()

    @Query("SELECT * FROM cards ORDER BY position ASC")
    suspend fun all(): List<Card>

    @Transaction
    suspend fun replaceAll(cards: List<Card>) {
        deleteAll()
        cards.forEach { upsert(it) }
    }

    @Transaction
    suspend fun reorder(orderedIds: List<String>) {
        orderedIds.forEachIndexed { idx, id -> setPosition(id, idx) }
    }

    @Query("UPDATE cards SET position = :position WHERE id = :id")
    suspend fun setPosition(id: String, position: Int)
}
