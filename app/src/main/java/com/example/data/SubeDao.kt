package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubeDao {
    // Cards
    @Query("SELECT * FROM transit_cards ORDER BY isPrimary DESC, lastReadTimestamp DESC")
    fun getAllCards(): Flow<List<TransitCard>>

    @Query("SELECT * FROM transit_cards WHERE cardId = :cardId LIMIT 1")
    suspend fun getCardById(cardId: String): TransitCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCard(card: TransitCard)

    @Update
    suspend fun updateCard(card: TransitCard)

    @Query("DELETE FROM transit_cards WHERE cardId = :cardId")
    suspend fun deleteCard(cardId: String)

    // Transactions
    @Query("SELECT * FROM transactions WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getTransactionsForCard(cardId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    // TopUp Orders
    @Query("SELECT * FROM top_up_orders WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getTopUpOrdersForCard(cardId: String): Flow<List<TopUpOrder>>

    @Query("SELECT * FROM top_up_orders WHERE status = 'PENDING_ACCREDITATION'")
    fun getPendingTopUpOrders(): Flow<List<TopUpOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopUpOrder(order: TopUpOrder)

    @Update
    suspend fun updateTopUpOrder(order: TopUpOrder)
}
