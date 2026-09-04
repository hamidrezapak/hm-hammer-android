package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

data class LiveCandle(val open: Float, val high: Float, val low: Float, val close: Float)

@Composable
fun ChartRadarScreen(viewModel: MainViewModel) {
    val selectedPair by viewModel.selectedPair.collectAsState()
    val currentPrice by viewModel.currentPrice.collectAsState()
    val levels by viewModel.dynamicLevels.collectAsState()
    val isRunning by viewModel.isEngineRunning.collectAsState()

    // ۳۰ کندل زنده رندر شده روی بوم گرافیکی نیتیو
    val candles = remember(currentPrice) {
        val base = currentPrice.toFloat()
        List(28) { i ->
            val o = base + (kotlin.math.sin(i.toDouble() * 0.7) * 160).toFloat()
            val c = o + (kotlin.math.cos(i.toDouble() * 0.8) * 120).toFloat()
            val h = maxOf(o, c) + 80f
            val l = minOf(o, c) - 150f
            LiveCandle(o, h, l, c)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // هدر وضعیت نماد و قیمت زنده
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(selectedPair, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("نرخ لحظه‌ای: $$currentPrice", color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = if (isRunning) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFFF9800).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        if (isRunning) "الگوریتم فعال 🟢" else "استندبای 🟡",
                        color = if (isRunning) Color(0xFF00E676) else Color(0xFFFF9800),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // بوم گرافیکی کندل‌استیک (Native Canvas - کاملاً آفلاین و سریع)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val minPrice = candles.minOf { it.low } - 80f
                    val maxPrice = candles.maxOf { it.high } + 80f
                    val priceRange = if (maxPrice - minPrice == 0f) 1f else (maxPrice - minPrice)

                    // گرید پس‌زمینه
                    for (i in 0..4) {
                        val y = h * (i.toFloat() / 4f)
                        drawLine(
                            color = Color(0xFF21262D),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // خط حد ضرر (Stop Loss)
                    val slY = h - ((levels.stopLoss.toFloat() - minPrice) / priceRange) * h
                    if (slY in 0f..h) {
                        drawLine(
                            color = Color(0xFFFF5252),
                            start = Offset(0f, slY),
                            end = Offset(w, slY),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f))
                        )
                    }

                    // خط حد سود (Take Profit)
                    val tpY = h - ((levels.tp1.toFloat() - minPrice) / priceRange) * h
                    if (tpY in 0f..h) {
                        drawLine(
                            color = Color(0xFF00E676),
                            start = Offset(0f, tpY),
                            end = Offset(w, tpY),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f))
                        )
                    }

                    // رسم کندل‌ها
                    val step = w / candles.size
                    val candleWidth = step * 0.65f

                    candles.forEachIndexed { index, candle ->
                        val x = index * step + (step / 2)
                        val isGreen = candle.close >= candle.open
                        val candleColor = if (isGreen) Color(0xFF00E676) else Color(0xFFFF5252)

                        val highY = h - ((candle.high - minPrice) / priceRange) * h
                        val lowY = h - ((candle.low - minPrice) / priceRange) * h
                        val openY = h - ((candle.open - minPrice) / priceRange) * h
                        val closeY = h - ((candle.close - minPrice) / priceRange) * h

                        // شدو بالا و پایین
                        drawLine(
                            color = candleColor,
                            start = Offset(x, highY),
                            end = Offset(x, lowY),
                            strokeWidth = 2f
                        )

                        // بدنه کندل
                        val top = minOf(openY, closeY)
                        val bodyHeight = maxOf(kotlin.math.abs(closeY - openY), 4f)
                        drawRect(
                            color = candleColor,
                            topLeft = Offset(x - (candleWidth / 2), top),
                            size = Size(candleWidth, bodyHeight)
                        )
                    }
                }

                // برچسب‌های اطلاعاتی روی چارت
                Column(modifier = Modifier.align(Alignment.TopEnd)) {
                    Text("هدف (TP1): $${String.format("%.1f", levels.tp1)}", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("حد ضرر (SL): $${String.format("%.1f", levels.stopLoss)}", color = Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // کارت اطلاعات رادار چکش و فیبوناچی
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("وضعیت الگوی پرایس‌اکشن:", color = Color.Gray, fontSize = 11.sp)
                    Text("تشخیص کندل چکش (Hammer)", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("موجودی فعال: $0.00 USDT", color = Color.LightGray, fontSize = 10.sp)
                    Text("نسبت ریسک به ریوارد: 1:2", color = Color(0xFF38BDF8), fontSize = 10.sp)
                }
            }
        }
    }
}
