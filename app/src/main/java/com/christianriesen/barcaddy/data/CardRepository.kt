package com.christianriesen.barcaddy.data

import kotlinx.coroutines.flow.Flow

class CardRepository(private val dao: CardDao) {

    fun observeAll(): Flow<List<Card>> = dao.observeAll()

    suspend fun all(): List<Card> = dao.all()

    suspend fun findById(id: String): Card? = dao.findById(id)

    suspend fun save(card: Card) {
        val existing = dao.findById(card.id)
        val withPos = if (existing == null) {
            card.copy(position = dao.maxPosition() + 1)
        } else {
            card.copy(position = existing.position)
        }
        dao.upsert(withPos)
    }

    suspend fun delete(id: String) {
        dao.delete(id)
        // Compact remaining positions so reordering stays simple.
        val remaining = dao.all()
        dao.reorder(remaining.map { it.id })
    }

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun reorder(orderedIds: List<String>) = dao.reorder(orderedIds)

    suspend fun replaceAll(cards: List<Card>) {
        dao.replaceAll(cards.mapIndexed { idx, c -> c.copy(position = idx) })
    }
}
