package com.example.model

enum class RiskCalcMode(val title: String) {
    ATR("ATR Multiple"),
    PERCENT("Fixed Percentage")
}

data class UserConfig(
    val userId: String = "89175496",
    val username: String = "masjedi6913",
    val autoTrade: Boolean = true,
    val tradeAmountTmn: Double = 500000.0,
    val exchangeName: String = "wallex",
    val leverage: String = "2x",
    val apiKey: String = "",
    val subscriptionPlan: String = "VIP", // STANDARD, PRO, ELITE, VIP
    val walletTmn: Double = 12500000.0,
    val walletTrx: Double = 345.8,
    val walletUsdt: Double = 250.0,
    val walletBtc: Double = 0.0042,
    val creditTmn: Double = 1500000.0,
    val hwmTmn: Double = 18500000.0,
    val isPostOnly: Boolean = true,
    val maxRiskPerTrade: Double = 0.02,
    // Advanced Risk Management parameters
    val slMode: String = "ATR", // "ATR" or "PERCENT"
    val slAtrMultiplier: Double = 1.5,
    val slPercentage: Double = 2.0,
    val tpMode: String = "ATR", // "ATR" or "PERCENT"
    val tpTargetCount: Int = 3, // 1 to 4 targets
    val tp1Value: Double = 1.0, // 1.0x ATR or 2.0%
    val tp2Value: Double = 1.5, // 1.5x ATR or 4.0%
    val tp3Value: Double = 2.0, // 2.0x ATR or 6.0%
    val tp4Value: Double = 3.0, // 3.0x ATR or 10.0%
    val trailingStopBreakeven: Boolean = true // Move SL to entry upon TP1 hit
)

data class ManagedUserItem(
    val id: String,
    val user: String,
    val plan: String,
    val credit: String,
    val hwm: String,
    val status: String,
    val isActive: Boolean
)

data class AdminDashboardData(
    val totalUsers: Int = 142,
    val activeBots: Int = 89,
    val totalInvested: String = "۴۴,۵۰۰,۰۰۰ تومان",
    val turnover: String = "۲,۶۸۹,۷۴۲ ت",
    val serverAlive: Boolean = true,
    val lastLog: String = "[RADAR] Scanning 15 pairs... Active Engine OK",
    val users: List<ManagedUserItem> = emptyList(),
    val liveTrades: List<TradeOrder> = emptyList()
)
