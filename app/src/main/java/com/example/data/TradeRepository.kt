package com.example.data

import com.example.model.TradeOrder
import com.example.model.TradeStatus
import com.example.model.TradingSignal
import com.example.model.UserConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TradeRepository(private val dao: AppDao) {

    val allTrades: Flow<List<TradeOrder>> = dao.getAllTrades().map { list ->
        list.map { it.toModel() }
    }

    val allSignals: Flow<List<SignalEntity>> = dao.getAllSignals()

    fun getUserConfig(userId: String): Flow<UserConfig?> = dao.getUserConfig(userId).map {
        it?.toModel()
    }

    suspend fun saveUserConfig(config: UserConfig) {
        dao.insertOrUpdateUserConfig(config.toEntity())
    }

    suspend fun insertTrade(order: TradeOrder): Long {
        return dao.insertTrade(order.toEntity())
    }

    suspend fun updateTradeStatus(
        id: Long,
        status: TradeStatus,
        currentPrice: Double,
        exitPrice: Double,
        pnl: Double,
        profit: Double,
        exitTimestamp: Long,
        closeReason: String,
        stopLoss: Double
    ) {
        dao.updateTradeStatusAndExit(
            id = id,
            status = status.name,
            currentPrice = currentPrice,
            exitPrice = exitPrice,
            pnl = pnl,
            profit = profit,
            exitTimestamp = exitTimestamp,
            closeReason = closeReason,
            stopLoss = stopLoss
        )
    }

    suspend fun closeTradeManually(id: Long, exitPrice: Double, pnl: Double, profit: Double) {
        dao.closeTradeManually(
            id = id,
            exitPrice = exitPrice,
            exitTimestamp = System.currentTimeMillis(),
            pnl = pnl,
            profit = profit
        )
    }

    suspend fun clearAllTrades() {
        dao.clearAllTrades()
    }

    suspend fun insertSignal(signal: TradingSignal): Long {
        return dao.insertSignal(
            SignalEntity(
                symbol = signal.symbol,
                action = signal.action.name,
                patternType = signal.patternType.name,
                entryPrice = signal.entryPrice,
                stopLoss = signal.stopLoss,
                tp1 = signal.tp1,
                tp2 = signal.tp2,
                tp3 = signal.tp3,
                atr = signal.atr,
                rsi = signal.rsi,
                ema200 = signal.ema200,
                volumeSpike = signal.volumeSpike,
                timestamp = signal.timestamp,
                timeframe = signal.timeframe
            )
        )
    }
}

// Extension functions for mapping
fun TradeEntity.toModel(): TradeOrder {
    return TradeOrder(
        id = id,
        userId = userId,
        symbol = symbol,
        side = side,
        entryPrice = entryPrice,
        currentPrice = currentPrice,
        exitPrice = exitPrice,
        stopLoss = stopLoss,
        tp1 = tp1,
        tp2 = tp2,
        tp3 = tp3,
        tp4 = tp4,
        amountTmn = amountTmn,
        leverage = leverage,
        status = runCatching { TradeStatus.valueOf(status) }.getOrDefault(TradeStatus.OPEN),
        pnlPercent = pnlPercent,
        profitUsdt = profitUsdt,
        isPostOnly = isPostOnly,
        entryTimestamp = entryTimestamp,
        exitTimestamp = exitTimestamp,
        closeReason = closeReason,
        timestamp = timestamp
    )
}

fun TradeOrder.toEntity(): TradeEntity {
    return TradeEntity(
        id = id,
        userId = userId,
        symbol = symbol,
        side = side,
        entryPrice = entryPrice,
        currentPrice = currentPrice,
        exitPrice = exitPrice,
        stopLoss = stopLoss,
        tp1 = tp1,
        tp2 = tp2,
        tp3 = tp3,
        tp4 = tp4,
        amountTmn = amountTmn,
        leverage = leverage,
        status = status.name,
        pnlPercent = pnlPercent,
        profitUsdt = profitUsdt,
        isPostOnly = isPostOnly,
        entryTimestamp = entryTimestamp,
        exitTimestamp = exitTimestamp,
        closeReason = closeReason,
        timestamp = timestamp
    )
}

fun UserConfigEntity.toModel(): UserConfig {
    return UserConfig(
        userId = userId,
        username = username,
        autoTrade = autoTrade,
        tradeAmountTmn = tradeAmountTmn,
        exchangeName = exchangeName,
        leverage = leverage,
        apiKey = apiKey,
        subscriptionPlan = subscriptionPlan,
        walletTmn = walletTmn,
        walletTrx = walletTrx,
        walletUsdt = walletUsdt,
        walletBtc = walletBtc,
        creditTmn = creditTmn,
        hwmTmn = hwmTmn,
        isPostOnly = isPostOnly,
        maxRiskPerTrade = maxRiskPerTrade,
        slMode = slMode,
        slAtrMultiplier = slAtrMultiplier,
        slPercentage = slPercentage,
        tpMode = tpMode,
        tpTargetCount = tpTargetCount,
        tp1Value = tp1Value,
        tp2Value = tp2Value,
        tp3Value = tp3Value,
        tp4Value = tp4Value,
        trailingStopBreakeven = trailingStopBreakeven
    )
}

fun UserConfig.toEntity(): UserConfigEntity {
    return UserConfigEntity(
        userId = userId,
        username = username,
        autoTrade = autoTrade,
        tradeAmountTmn = tradeAmountTmn,
        exchangeName = exchangeName,
        leverage = leverage,
        apiKey = apiKey,
        subscriptionPlan = subscriptionPlan,
        walletTmn = walletTmn,
        walletTrx = walletTrx,
        walletUsdt = walletUsdt,
        walletBtc = walletBtc,
        creditTmn = creditTmn,
        hwmTmn = hwmTmn,
        isPostOnly = isPostOnly,
        maxRiskPerTrade = maxRiskPerTrade,
        slMode = slMode,
        slAtrMultiplier = slAtrMultiplier,
        slPercentage = slPercentage,
        tpMode = tpMode,
        tpTargetCount = tpTargetCount,
        tp1Value = tp1Value,
        tp2Value = tp2Value,
        tp3Value = tp3Value,
        tp4Value = tp4Value,
        trailingStopBreakeven = trailingStopBreakeven
    )
}
