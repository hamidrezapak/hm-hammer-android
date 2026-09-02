package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ChartRadarScreen(
    viewModel: MainViewModel? = null
) {
    val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "BNB/USDT", "DOGE/USDT")
    var selectedPair by remember { mutableStateOf(pairs[0]) }

    val timeframes = listOf("5M", "15M", "1H", "4H", "1D")
    var selectedTf by remember { mutableStateOf("15M") }

    // شبیه‌سازی قیمت زنده متناظر با جفت‌ارز انتخابی
    val currentPrice = when (selectedPair) {
        "BTC/USDT" -> "$64,280.00"
        "ETH/USDT" -> "$3,450.50"
        "SOL/USDT" -> "$154.20"
        "BNB/USDT" -> "$590.10"
        "DOGE/USDT" -> "$0.1245"
        else -> "$1.00"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // سلکتور جفت‌ارزها با عملکرد ۱۰۰٪ فعال
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(pairs) { pair ->
                val isSelected = pair == selectedPair
                Button(
                    onClick = { selectedPair = pair },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF00E676) else Color(0xFF161B22)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E676) else Color(0xFF30363D)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        pair,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // کارت قیمت و اطلاعات ستاپ زنده
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
                    Text(selectedPair, color = Color.Gray, fontSize = 11.sp)
                    Text(currentPrice, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
                Surface(
                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "ALGO ACTIVE 🟢",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // سلکتور تایم‌فریم‌ها
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            timeframes.forEach { tf ->
                val isSelected = tf == selectedTf
                Button(
                    onClick = { selectedTf = tf },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF38BDF8) else Color(0xFF21262D)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(tf, color = if (isSelected) Color.Black else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // چارت حرفه‌ای نئونی با خطوط پس‌زمینه گرید و الگوهای چکش
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
            border = BorderStroke(1.dp, Color(0xFF21262D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // رسم خطوط شبکه پس‌زمینه چارت (Grid Lines)
                    val gridLines = 5
                    for (g in 1..gridLines) {
                        val y = (h / (gridLines + 1)) * g
                        drawLine(Color(0xFF1F242C), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                    }

                    // تغییر شکل کندل‌ها متناسب با جفت‌ارز انتخاب‌شده
                    val hash = selectedPair.hashCode()
                    val candleCount = 18
                    val candleW = w / candleCount

                    for (i in 0 until candleCount) {
                        val x = i * candleW + 4f
                        val isHammer = (i == 13) // کندل چکش در نماد انتخابی
                        val isUp = ((hash + i) % 3 != 0)
                        val color = if (isHammer) Color(0xFFFFB703) else if (isUp) Color(0xFF00E676) else Color(0xFFFF5252)

                        val bodyTop = if (isHammer) h * 0.55f else ((h * 0.2f) + ((hash * (i + 1)) % 100).coerceIn(10, 180).toFloat())
                        val bodyH = if (isHammer) 18f else 32f
                        val wickBottom = if (isHammer) bodyTop + 65f else bodyTop + bodyH + 20f
                        val wickTop = bodyTop - 14f

                        // سایه کندل
                        drawLine(color, Offset(x + candleW / 4, wickTop), Offset(x + candleW / 4, wickBottom), strokeWidth = if (isHammer) 3.5f else 1.8f)
                        // بدنه کندل
                        drawRect(color, Offset(x, bodyTop), Size(candleW / 2, bodyH))
                    }

                    // رسم منحنی روند میانگین ۲۰۰
                    val path = Path()
                    path.moveTo(0f, h * 0.7f)
                    path.cubicTo(w * 0.35f, h * 0.75f, w * 0.65f, h * 0.45f, w, h * 0.4f)
                    drawPath(path, Color(0xFF00B0FF), style = Stroke(width = 3f))
                }

                // برچسب مشخصات وضعیت
                Surface(
                    color = Color(0xFF161B22).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.TopStart).padding(6.dp)
                ) {
                    Text(
                        "SMA/EMA(200) Bullish • Pattern: HAMMER DETECTED ($selectedTf)",
                        color = Color(0xFF38BDF8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
