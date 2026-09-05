package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class TradingService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HMHammer::TradingWakeLock").apply {
            acquire(10 * 60 * 1000L /* 10 minutes */)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val apiKey = intent?.getStringExtra("API_KEY") ?: ""
        val symbol = intent?.getStringExtra("SYMBOL") ?: "BTCUSDT"

        if (!isRunning && apiKey.isNotBlank()) {
            isRunning = true
            startForeground(101, buildNotification("موتور ترید خودکار فعال است ($symbol)"))
            startTradingLoop(apiKey, symbol)
        }
        return START_STICKY
    }

    private fun startTradingLoop(apiKey: String, symbol: String) {
        serviceScope.launch {
            var currentPositionPrice = 0.0
            var holdingAsset = false

            while (isActive && isRunning) {
                try {
                    // ۱. دریافت قیمت لحظه‌ای از والکس
                    val currentPrice = fetchMarketPrice(symbol)
                    
                    if (!holdingAsset && currentPrice > 0.0) {
                        // دریافت موجودی تتر
                        val usdtBalance = fetchUsdtBalance(apiKey)
                        if (usdtBalance >= 3.0) { // حداقل کف معامله والکس
                            val buyPrice = currentPrice
                            val quantity = ((usdtBalance * 0.98) / buyPrice) // با کسر ذخیره کارمزد
                            val formattedQty = String.format(java.util.Locale.US, "%.4f", quantity).toDouble()

                            if (formattedQty > 0.0) {
                                val orderSuccess = executeOrder(apiKey, symbol, "BUY", formattedQty, buyPrice)
                                if (orderSuccess) {
                                    currentPositionPrice = buyPrice
                                    holdingAsset = true
                                    updateNotification("خرید موفق در قیمت $buyPrice. در انتظار سود...")
                                }
                            }
                        }
                    } else if (holdingAsset && currentPositionPrice > 0.0) {
                        // ۲. مانیتورینگ سود خالص ۱.۵٪ (پوشش کارمزد + سود خالص)
                        val profitPercentage = ((currentPrice - currentPositionPrice) / currentPositionPrice) * 100.0
                        if (profitPercentage >= 1.5) {
                            val assetBalance = fetchAssetBalance(apiKey, symbol.replace("USDT", ""))
                            if (assetBalance > 0.0) {
                                val formattedQty = String.format(java.util.Locale.US, "%.4f", assetBalance).toDouble()
                                val sellSuccess = executeOrder(apiKey, symbol, "SELL", formattedQty, currentPrice)
                                if (sellSuccess) {
                                    holdingAsset = false
                                    currentPositionPrice = 0.0
                                    updateNotification("فروش با سود +${String.format("%.2f", profitPercentage)}% تکمیل شد. آماده ورود بعدی.")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // مدیریت خطای مقطعی شبکه
                }
                delay(7000) // اسکن هر ۷ ثانیه
            }
        }
    }

    private fun fetchMarketPrice(symbol: String): Double {
        return try {
            val url = URL("https://api.wallex.ir/v1/markets")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            val res = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(res)
            val markets = json.getJSONObject("result").getJSONObject("symbols")
            val stats = markets.getJSONObject(symbol).getJSONObject("stats")
            stats.getDouble("lastPrice")
        } catch (e: Exception) { 0.0 }
    }

    private fun fetchUsdtBalance(apiKey: String): Double {
        return try {
            val url = URL("https://api.wallex.ir/v1/account/balances")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("X-API-Key", apiKey)
            conn.connectTimeout = 5000
            val res = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(res)
            json.getJSONObject("result").getJSONObject("balances").getJSONObject("USDT").getDouble("value")
        } catch (e: Exception) { 0.0 }
    }

    private fun fetchAssetBalance(apiKey: String, asset: String): Double {
        return try {
            val url = URL("https://api.wallex.ir/v1/account/balances")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("X-API-Key", apiKey)
            conn.connectTimeout = 5000
            val res = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(res)
            json.getJSONObject("result").getJSONObject("balances").getJSONObject(asset).getDouble("value")
        } catch (e: Exception) { 0.0 }
    }

    private fun executeOrder(apiKey: String, symbol: String, side: String, quantity: Double, price: Double): Boolean {
        return try {
            val url = URL("https://api.wallex.ir/v1/orders")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("X-API-Key", apiKey)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 6000

            val payload = JSONObject().apply {
                put("symbol", symbol)
                put("type", "LIMIT")
                put("side", side)
                put("price", String.format(java.util.Locale.US, "%.2f", price))
                put("quantity", String.format(java.util.Locale.US, "%.4f", quantity))
            }
            conn.outputStream.write(payload.toString().toByteArray())
            conn.responseCode in 200..299
        } catch (e: Exception) { false }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hammer_trading_channel",
                "HM Hammer Trading Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, "hammer_trading_channel")
            .setContentTitle("سامانه معاملاتی خودکار HM Hammer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(101, buildNotification(text))
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
