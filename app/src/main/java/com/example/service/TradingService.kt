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
import java.util.Locale

class TradingService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("سرویس معاملات فعال است"))
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

    private fun extractBaseAsset(symbol: String): String {
        return symbol.uppercase(Locale.ROOT)
            .removeSuffix("USDT")
            .removeSuffix("TMN")
            .removeSuffix("BTC")
    }

    private fun startTradingLoop(apiKey: String, symbol: String) {
        serviceScope.launch {
            var currentPositionPrice = 0.0
            var holdingAsset = false
            var failureCount = 0
            val baseAsset = extractBaseAsset(symbol)

            while (isActive && isRunning) {
                try {
                    val priceResult = WallexLiveClient.fetchMarketPrice(symbol)
                    if (priceResult.isFailure) {
                        throw priceResult.exceptionOrNull() ?: Exception("خطا در دریافت قیمت")
                    }
                    val currentPrice = priceResult.getOrThrow()

                    if (!holdingAsset && currentPrice > 0.0) {
                        val usdtResult = WallexLiveClient.fetchBalance(apiKey, "USDT")
                        usdtResult.onSuccess { usdtBalance ->
                            failureCount = 0
                            if (usdtBalance >= 1.5) {
                                val buyPrice = currentPrice
                                val rawQty = (usdtBalance * 0.98) / buyPrice
                                val cleanQty = WallexLiveClient.formatQuantity(symbol, rawQty)

                                if (cleanQty > 0.0) {
                                    updateNotification("در حال خرید در $buyPrice...")
                                    val orderResult = WallexLiveClient.executeOrder(apiKey, symbol, "BUY", cleanQty, buyPrice)
                                    orderResult.onSuccess { orderId ->
                                        currentPositionPrice = buyPrice
                                        holdingAsset = true
                                        updateNotification("خرید ثبت شد (سفارش: $orderId)")
                                    }.onFailure { err ->
                                        updateNotification("خطا در خرید: ${err.message}")
                                    }
                                }
                            }
                        }.onFailure { err ->
                            handleError(err)
                        }
                    } else if (holdingAsset && currentPositionPrice > 0.0 && currentPrice > 0.0) {
                        val deltaPct = ((currentPrice - currentPositionPrice) / currentPositionPrice) * 100.0

                        if (deltaPct >= 1.5 || deltaPct <= -2.0) {
                            val assetResult = WallexLiveClient.fetchBalance(apiKey, baseAsset)
                            assetResult.onSuccess { assetBalance ->
                                val cleanQty = WallexLiveClient.formatQuantity(symbol, assetBalance)
                                if (cleanQty > 0.0) {
                                    val sellResult = WallexLiveClient.executeOrder(apiKey, symbol, "SELL", cleanQty, currentPrice)
                                    sellResult.onSuccess {
                                        holdingAsset = false
                                        currentPositionPrice = 0.0
                                        val sign = if (deltaPct >= 0) "+" else ""
                                        updateNotification("فروش انجام شد (${sign}${String.format(Locale.US, "%.2f", deltaPct)}%)")
                                    }.onFailure { err ->
                                        updateNotification("خطا در فروش: ${err.message}")
                                    }
                                }
                            }.onFailure { err ->
                                handleError(err)
                            }
                        }
                    }

                    delay(5000)

                } catch (e: Exception) {
                    failureCount++
                    if (e is WallexException.AuthException) {
                        updateNotification("توقف: کلید API والکس نامعتبر است")
                        stopSelf()
                        break
                    }
                    val waitSec = (5 * failureCount).coerceAtMost(60)
                    updateNotification("خطای ارتباط. تلاش مجدد در $waitSec ثانیه...")
                    delay(waitSec * 1000L)
                }
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        if (throwable is WallexException.AuthException) {
            updateNotification("توقف: دسترسی نامعتبر است (401/403)")
            stopSelf()
        } else {
            updateNotification("خطای موجودی: ${throwable.localizedMessage}")
        }
    }

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ربات HM Hammer")
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
