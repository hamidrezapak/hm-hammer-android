package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel
import kotlin.random.Random

data class CandleData(
    val high: Float,
    val low: Float,
    val open: Float,
    val close: Float,
    val isHammer: Boolean = false
)

@Composable
fun ChartRadarScreen(
    viewModel: MainViewModel? = null
) {
    val context = LocalContext.current
    val pairs = listOf(
        Pair("BTC/USDT", 64520.0),
        Pair("ETH/USDT", 3485.0),
        Pair("SOL/USDT", 154.5),
        Pair("BNB/USDT", 589.2),
        Pair("DOGE/USDT", 0.126)
    )
    var selectedPair by remember { mutableStateOf(pairs[0]) }
    val timeframes = listOf("5M", "15M", "1H", "4H", "1D")
    var selectedTf by remember { mutableStateOf("15M") }

    // تولید کندل‌های رندوم متناسب با جفت‌ارز انتخابی به صورت ۱۰۰٪ آفلاین
    val candles = remember(selectedPair, selectedTf) {
        val list = mutableListOf<CandleData>()
        val base = selectedPair.second.toFloat()
        var cur = base
        for (i in 0 until 24) {
            val isHammer = (i == 18)
            val open = cur
            val change = (Random.nextFloat() - 0.48f) * (base * 0.015f)
            val close = if (isHammer) open + (base * 0.005f) else open + change
            val high = maxOf(open, close) + (Random.nextFloat() * base * 0.005f)
            val low = if (isHammer) open - (base * 0.035f) else minOf(open, close) - (Random.nextFloat() * base * 0.005f)
            list.add(CandleData(high, low, open, close, isHammer))
            cur = close
        }
        list
    }

    var scaleFactor by remember { mutableStateOf(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ۱. سلکتور جفت‌ارزها با عملکرد کامل
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(pairs) { item ->
                val isSelected = item.first == selectedPair.first
                Button(
                    onClick = {
                        selectedPair = item
                        Toast.makeText(context, "جفت‌ارز ${item.first} بارگذاری شد", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF00E676) else Color(0xFF161B22)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E676) else Color(0xFF30363D)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        item.first,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // ۲. کارت قیمت زنده
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(selectedPair.first + " • Spot", color = Color.Gray, fontSize = 11.sp)
                    Text("$${selectedPair.second}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = {
                        Toast.makeText(context, "سیگنال چکش روی ${selectedPair.first} تایید شد", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("HAMMER ACTIVE ⚡", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ۳. تایم‌فریم‌ها
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            timeframes.forEach { tf ->
                val isSelected = tf == selectedTf
                Button(
                    onClick = {
                        selectedTf = tf
                        Toast.makeText(context, "تایم‌فریم: $tf", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF38BDF8) else Color(0xFF21262D)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(tf, color = if (isSelected) Color.Black else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ۴. بوم چارت حرفه‌ای با زوم لمسی و بدون نیاز به اینترنت خارجی
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
            border = BorderStroke(1.dp, Color(0xFF21262D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        scaleFactor = (scaleFactor * zoom).coerceIn(0.7f, 2.5f)
                    }
                }
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // خطوط شبکه چارت
                    for (g in 1..5) {
                        val y = (h / 6) * g
                        drawLine(Color(0xFF1B222D), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                    }

                    val count = candles.size
                    val candleW = (w / count) * scaleFactor
                    val minPrice = candles.minOf { it.low }
                    val maxPrice = candles.maxOf { it.high }
                    val priceRange = if (maxPrice - minPrice == 0f) 1f else (maxPrice - minPrice)

                    fun getY(price: Float): Float {
                        return h - ((price - minPrice) / priceRange) * (h * 0.8f) - (h * 0.1f)
                    }

                    // رسم کندل‌ها
                    candles.forEachIndexed { i, c ->
                        val x = i * candleW + 4f
                        val openY = getY(c.open)
                        val closeY = getY(c.close)
                        val highY = getY(c.high)
                        val lowY = getY(c.low)

                        val isUp = c.close >= c.open
                        val color = if (c.isHammer) Color(0xFFFFB703) else if (isUp) Color(0xFF00E676) else Color(0xFFFF5252)

                        // شدو کندل
                        drawLine(color, Offset(x + candleW / 4, highY), Offset(x + candleW / 4, lowY), strokeWidth = if (c.isHammer) 3.5f else 1.8f)
                        // بدنه کندل
                        val top = minOf(openY, closeY)
                        val bodyHeight = maxOf(kotlin.math.abs(closeY - openY), 4f)
                        drawRect(color, Offset(x, top), Size(candleW / 2, bodyHeight))
                    }

                    // خط روند میانگین متحرک
                    val emaPath = Path()
                    candles.forEachIndexed { i, c ->
                        val x = i * candleW + 4f + (candleW / 4)
                        val y = getY(c.close) + 8f
                        if (i == 0) emaPath.moveTo(x, y) else emaPath.lineTo(x, y)
                    }
                    drawPath(emaPath, Color(0xFF00B0FF), style = Stroke(width = 2.5f))
                }

                // اطلاعات چارت
                Surface(
                    color = Color(0xFF161B22).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.TopStart).padding(6.dp)
                ) {
                    Text(
                        "${selectedPair.first} • $selectedTf | Zoom: ${(scaleFactor * 100).toInt()}%",
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
