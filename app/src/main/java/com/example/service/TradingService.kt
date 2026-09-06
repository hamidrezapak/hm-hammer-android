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
            acquire()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val apiKey = intent?.getStringExtra("API_KEY")?.trim() ?: ""
        val symbol = intent?.getStringExtra("SYMBOL")?.trim() ?: "BTCUSDT"

        if (!isRunning && apiKey.isNotBlank()) {
            isRunning = true
            startForeground(101, buildNotification("ربات ترید فعال است ($symbol)"))
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
                    val currentPrice = fetchMarketPrice(symbol)

                    if (!holdingAsset && currentPrice > 0.0) {
                        val usdtBalance = fetchUsdtBalance(apiKey)
                        if (usdtBalance >= 1.5) {
                            val buyPrice = currentPrice
                            val quantity = ((usdtBalance * 0.98) / buyPrice)
                            val formattedQty = String.format(java.util.Locale.US, "%.5f", quantity).toDouble()

                            if (formattedQty > 0.0) {
                                val orderSuccess = executeOrder(apiKey, symbol, "BUY", formattedQty, buyPrice)
                                if (orderSuccess) {
                                    delay(5000)
                                    val assetBalance = fetchAssetBalance(apiKey, symbol.replace("USDT", ""))
                                    if (assetBalance > 0.0) {
                                        currentPositionPrice = buyPrice
                                        holdingAsset = true
                                        updateNotification("خرید ثبت شد در قیمت $buyPrice. مانیتورینگ سود...")
                                    }
                                }
                            }
                        }
                    } else if (holdingAsset && currentPositionPrice > 0.0 && currentPrice > 0.0) {
                        val deltaPct = ((currentPrice - currentPositionPrice) / currentPositionPrice) * 100.0

                        // تارگت سود +1.5% یا حد ضرر اضطراری -2.0%
                        if (deltaPct >= 1.5 || deltaPct <= -2.0) {
                            val assetBalance = fetchAssetBalance(apiKey, symbol.replace("USDT", ""))
                            if (assetBalance > 0.0) {
                                val formattedQty = String.format(java.util.Locale.US, "%.5f", assetBalance).toDouble()
                                val sellSuccess = executeOrder(apiKey, symbol, "SELL", formattedQty, currentPrice)
                                if (sellSuccess) {
                                    holdingAsset = false
                                    currentPositionPrice = 0.0
                                    val msg = if (deltaPct >= 1.5) "خروج با سود +${String.format("%.2f", deltaPct)}%" else "خروج با حد ضرر ${String.format("%.2f", deltaPct)}%"
                                    updateNotification("$msg. آماده چرخه خرید بعدی.")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // جلوگیری از توقف با خطاهای شبکه
                }
                delay(6000)
            }
        }
    }

    private fun fetchMarketPrice(symbol: String): Double {
        return try {
            val url = URL("https://api.wallex.ir/v1/markets")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
            }
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
            val conn = (url.openConnection() as HttpURLConnection).apply {
                setRequestProperty("X-API-Key", apiKey)
                connectTimeout = 5000
                readTimeout = 5000
            }
            val res = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(res)
            json.getJSONObject("result").getJSONObject("balances").getJSONObject("USDT").getDouble("value")
        } catch (e: Exception) { 0.0 }
    }

    private fun fetchAssetBalance(apiKey: String, asset: String): Double {
        return try {
            val url = URL("https://api.wallex.ir/v1/account/balances")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                setRequestProperty("X-API-Key", apiKey)
                connectTimeout = 5000
                readTimeout = 5000
            }
            val res = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(res)
            json.getJSONObject("result").getJSONObject("balances").getJSONObject(asset).getDouble("value")
        } catch (e: Exception) { 0.0 }
    }

    private fun executeOrder(apiKey: String, symbol: String, side: String, quantity: Double, price: Double): Boolean {
        return try {
            val url = URL("https://api.wallex.ir/v1/orders")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("X-API-Key", apiKey)
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 7000
                readTimeout = 7000
            }

            val payload = JSONObject().apply {
                put("symbol", symbol)
                put("type", "LIMIT")
                put("side", side)
                put("price", String.format(java.util.Locale.US, "%.2f", price))
                put("quantity", String.format(java.util.Locale.US, "%.5f", quantity))
            }
            conn.outputStream.write(payload.toString().toByteArray())
            conn.responseCode in 200..299
        } catch (e: Exception) { false }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hammer_trading_channel",
                "HM Hammer Trading Engine",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, "hammer_trading_channel")
            .setContentTitle("موتور معاملاتی خودکار والکس")
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
