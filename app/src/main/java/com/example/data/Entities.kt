package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "89175496",
    val symbol: String,
    val side: String, // BUY / SELL
    val entryPrice: Double,
    val currentPrice: Double,
    val exitPrice: Double = 0.0,
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val tp3: Double,
    val tp4: Double = 0.0,
    val amountTmn: Double,
    val leverage: String = "2x",
    val status: String = "OPEN",
    val pnlPercent: Double = 0.0,
    val profitUsdt: Double = 0.0,
    val isPostOnly: Boolean = true,
    val entryTimestamp: Long = System.currentTimeMillis(),
    val exitTimestamp: Long = 0L,
    val closeReason: String = "Active",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "signals")
data class SignalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val action: String,
    val patternType: String,
    val entryPrice: Double,
    val stopLoss: Double,
    val tp1: Double,
    val tp2: Double,
    val tp3: Double,
    val atr: Double,
    val rsi: Double,
    val ema200: Double,
    val volumeSpike: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val timeframe: String = "15m"
)

@Entity(tableName = "user_config")
data class UserConfigEntity(
    @PrimaryKey val userId: String = "89175496",
    val username: String = "masjedi6913",
    val autoTrade: Boolean = true,
    val tradeAmountTmn: Double = 500000.0,
    val exchangeName: String = "wallex",
    val leverage: String = "2x",
    val apiKey: String = "",
    val subscriptionPlan: String = "VIP",
    val walletTmn: Double = 12500000.0,
    val walletTrx: Double = 345.8,
    val walletUsdt: Double = 250.0,
    val walletBtc: Double = 0.0042,
    val creditTmn: Double = 1500000.0,
    val hwmTmn: Double = 18500000.0,
    val isPostOnly: Boolean = true,
    val maxRiskPerTrade: Double = 0.02,
    val slMode: String = "ATR",
    val slAtrMultiplier: Double = 1.5,
    val slPercentage: Double = 2.0,
    val tpMode: String = "ATR",
    val tpTargetCount: Int = 3,
    val tp1Value: Double = 1.0,
    val tp2Value: Double = 1.5,
    val tp3Value: Double = 2.0,
    val tp4Value: Double = 3.0,
    val trailingStopBreakeven: Boolean = true
)
