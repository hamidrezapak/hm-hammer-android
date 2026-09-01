package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Trade operations
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Query("SELECT * FROM trades WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun getTradesForUser(userId: String): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity): Long

    @Update
    suspend fun updateTrade(trade: TradeEntity)

    @Query("UPDATE trades SET status = :status, currentPrice = :currentPrice, exitPrice = :exitPrice, pnlPercent = :pnl, profitUsdt = :profit, exitTimestamp = :exitTimestamp, closeReason = :closeReason, stopLoss = :stopLoss WHERE id = :id")
    suspend fun updateTradeStatusAndExit(
        id: Long,
        status: String,
        currentPrice: Double,
        exitPrice: Double,
        pnl: Double,
        profit: Double,
        exitTimestamp: Long,
        closeReason: String,
        stopLoss: Double
    )

    @Query("UPDATE trades SET status = 'CLOSED', exitPrice = :exitPrice, exitTimestamp = :exitTimestamp, pnlPercent = :pnl, profitUsdt = :profit, closeReason = 'MANUAL_CLOSE' WHERE id = :id")
    suspend fun closeTradeManually(id: Long, exitPrice: Double, exitTimestamp: Long, pnl: Double, profit: Double)

    @Query("DELETE FROM trades")
    suspend fun clearAllTrades()

    // Signals operations
    @Query("SELECT * FROM signals ORDER BY timestamp DESC LIMIT 30")
    fun getAllSignals(): Flow<List<SignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: SignalEntity): Long

    // User Config operations
    @Query("SELECT * FROM user_config WHERE userId = :userId LIMIT 1")
    fun getUserConfig(userId: String): Flow<UserConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserConfig(config: UserConfigEntity)
}
