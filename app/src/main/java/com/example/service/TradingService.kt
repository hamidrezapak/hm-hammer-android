package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.network.WallexException
import com.example.network.WallexLiveClient
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.util.Locale

class TradingService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("سرویس معاملات پایدار آماده است"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val apiKey = intent?.getStringExtra("API_KEY")?.trim() ?: ""
        val symbol = intent?.getStringExtra("SYMBOL")?.trim() ?: "BTCUSDT"

        if (!isRunning && apiKey.isNotBlank()) {
            isRunning = true
            startTradingLoop(apiKey, symbol)
        }
        return START_STICKY
    }

    private fun getPositionFile(): File {
        val dir = File("/data/data/com.example/files")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "active_position.json")
    }

    private fun savePositionState(holding: Boolean, price: Double) {
        try {
            val json = JSONObject().apply {
                put("holding", holding)
                put("price", price)
            }
            getPositionFile().writeText(json.toString())
        } catch (_: Exception) {}
    }

    private fun loadPositionState(): Pair<Boolean, Double> {
        return try {
            val file = getPositionFile()
            if (file.exists()) {
                val json = JSONObject(file.readText())
                Pair(json.optBoolean("holding", false), json.optDouble("price", 0.0))
            } else {
                Pair(false, 0.0)
            }
        } catch (_: Exception) {
            Pair(false, 0.0)
        }
    }

    private fun extractBaseAsset(symbol: String): String {
        return symbol.uppercase(Locale.ROOT)
            .removeSuffix("USDT")
            .removeSuffix("TMN")
            .removeSuffix("BTC")
    }

    private fun startTradingLoop(apiKey: String, symbol: String) {
        serviceScope.launch {
            val (savedHolding, savedPrice) = loadPositionState()
            var holdingAsset = savedHolding
            var currentPositionPrice = savedPrice
            var failureCount = 0
            val baseAsset = extractBaseAsset(symbol)

            while (isActive && isRunning) {
                try {
                    val priceResult = WallexLiveClient.fetchMarketPrice(symbol)
                    if (priceResult.isFailure) {
                        throw priceResult.exceptionOrNull() ?: Exception("خطا در دریافت قیمت")
                    }
                    val currentPrice = priceResult.getOrThrow()

                    // سناریوی خرید
                    if (!holdingAsset && currentPrice > 0.0) {
                        val usdtResult = WallexLiveClient.fetchBalance(apiKey, "USDT")
                        usdtResult.onSuccess { usdtBalance ->
                            failureCount = 0
                            if (usdtBalance >= 1.5) {
                                val buyPrice = currentPrice
                                val rawQty = (usdtBalance * 0.98) / buyPrice
                                val cleanQty = WallexLiveClient.formatQuantity(symbol, rawQty)

                                if (cleanQty > 0.0) {
                                    updateNotification("ثبت سفارش خرید در $buyPrice...")
                                    val orderResult = WallexLiveClient.executeOrder(apiKey, symbol, "BUY", cleanQty, buyPrice)
                                    
                                    orderResult.onSuccess { orderId ->
                                        updateNotification("سفارش $orderId ثبت شد. بررسی وضعیت اجرا...")
                                        val isFilled = waitForOrderFill(apiKey, orderId, maxWaitSeconds = 25)
                                        if (isFilled) {
                                            holdingAsset = true
                                            currentPositionPrice = buyPrice
                                            savePositionState(true, buyPrice)
                                            updateNotification("خرید کامل شد در قیمت $buyPrice")
                                        } else {
                                            WallexLiveClient.cancelOrder(apiKey, orderId)
                                            updateNotification("سفارش خرید به دلیل عدم مچ لغو شد.")
                                        }
                                    }.onFailure { err ->
                                        updateNotification("رد سفارش خرید: ${err.message}")
                                    }
                                }
                            }
                        }.onFailure { err ->
                            handleError(err)
                        }
                    } 
                    // سناریوی فروش
                    else if (holdingAsset && currentPositionPrice > 0.0 && currentPrice > 0.0) {
                        val deltaPct = ((currentPrice - currentPositionPrice) / currentPositionPrice) * 100.0

                        if (deltaPct >= 1.5 || deltaPct <= -2.0) {
                            val assetResult = WallexLiveClient.fetchBalance(apiKey, baseAsset)
                            assetResult.onSuccess { assetBalance ->
                                val cleanQty = WallexLiveClient.formatQuantity(symbol, assetBalance)
                                if (cleanQty > 0.0) {
                                    updateNotification("ثبت سفارش فروش در $currentPrice...")
                                    val sellResult = WallexLiveClient.executeOrder(apiKey, symbol, "SELL", cleanQty, currentPrice)
                                    sellResult.onSuccess { orderId ->
                                        val isFilled = waitForOrderFill(apiKey, orderId, maxWaitSeconds = 20)
                                        if (isFilled) {
                                            holdingAsset = false
                                            currentPositionPrice = 0.0
                                            savePositionState(false, 0.0)
                                            val sign = if (deltaPct >= 0) "+" else ""
                                            updateNotification("فروش موفق (${sign}${String.format(Locale.US, "%.2f", deltaPct)}%)")
                                        } else {
                                            WallexLiveClient.cancelOrder(apiKey, orderId)
                                            updateNotification("سفارش فروش کامل نشد و لغو گردید.")
                                        }
                                    }.onFailure { err ->
                                        updateNotification("خطا در فروش: ${err.message}")
                                    }
                                }
                            }.onFailure { err ->
                                handleError(err)
                            }
                        }
                    }

                    delay(6000)

                } catch (e: Exception) {
                    failureCount++
                    if (e is WallexException.AuthException) {
                        updateNotification("توقف: کلید API نامعتبر است")
                        stopSelf()
                        break
                    }
                    val waitSec = (6 * failureCount).coerceAtMost(60)
                    updateNotification("خطای ارتباط. تلاش مجدد در $waitSec ثانیه...")
                    delay(waitSec * 1000L)
                }
            }
        }
    }

    private suspend fun waitForOrderFill(apiKey: String, orderId: String, maxWaitSeconds: Int): Boolean {
        var elapsed = 0
        while (elapsed < maxWaitSeconds) {
            delay(3000)
            elapsed += 3
            val statusResult = WallexLiveClient.checkOrderStatus(apiKey, orderId)
            if (statusResult.isSuccess) {
                val status = statusResult.getOrNull().orEmpty()
                if (status == "FILLED" || status == "DONE" || status == "CLOSED") {
                    return true
                }
                if (status == "CANCELED" || status == "REJECTED") {
                    return false
                }
            }
        }
        return false
    }

    private fun handleError(throwable: Throwable) {
        if (throwable is WallexException.AuthException) {
            updateNotification("توقف: دسترسی احراز هویت رد شد")
            stopSelf()
        } else {
            updateNotification("خطای حساب: ${throwable.localizedMessage}")
        }
    }

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ربات تریدر HM Hammer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Trading Service", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        isRunning = false
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "trading_service_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
