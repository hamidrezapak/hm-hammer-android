package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.components.LanguageOption
import com.example.ui.theme.AppLocale
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ChartRadarScreen(
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    var selectedTf by remember { mutableStateOf("15M") }
    var selectedPair by remember { mutableStateOf("BTC/USDT") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // سود ۲۴ ساعته
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("ANTI-FRAGILE PNL", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("+12.8%", color = Color(0xFF00E676), fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text(AppLocale.t("net_profit_24h", currentLanguage), color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }

        // شاخص‌های ATR و Trend
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = BorderStroke(1.dp, Color(0xFF30363D)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("atr_volatility", currentLanguage), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("x 5.42", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = BorderStroke(1.dp, Color(0xFF30363D)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("btc_trend", currentLanguage), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(AppLocale.t("bullish", currentLanguage), color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }

        // تایم‌فریم‌ها
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("1D", "4H", "1H", "15M").forEach { tf ->
                    val isSel = selectedTf == tf
                    Button(
                        onClick = { selectedTf = tf },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSel) Color(0xFF00E676) else Color(0xFF21262D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(tf, color = if (isSel) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // کادر نمودار زنده کندل‌استیک
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$92,352.64", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Surface(color = Color(0xFF00E676).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("$selectedTf $selectedPair", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            // رسم کندل‌ها
                            val candleW = w / 15f
                            for (i in 0 until 14) {
                                val isUp = i % 2 == 0
                                val color = if (isUp) Color(0xFF00E676) else Color(0xFFFF5252)
                                val x = i * candleW + 10f
                                val top = (h * 0.2f) + (i * 4f % (h * 0.4f))
                                val btm = top + (h * 0.3f)
                                drawLine(color, Offset(x + candleW / 4, top - 15), Offset(x + candleW / 4, btm + 15), strokeWidth = 2f)
                                drawRect(color, Offset(x, top), Size(candleW / 2, btm - top))
                            }
                            // خط روند میانگین متحرک ۲۰۰
                            val path = Path()
                            path.moveTo(0f, h * 0.7f)
                            path.quadraticBezierTo(w * 0.5f, h * 0.6f, w, h * 0.25f)
                            drawPath(path, Color(0xFFFFB703), style = Stroke(width = 3f))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(AppLocale.t("vol_filter_active", currentLanguage), color = Color.Gray, fontSize = 9.sp)
                        Text(AppLocale.t("ema_trend_line", currentLanguage), color = Color(0xFFFFB703), fontSize = 9.sp)
                    }
                }
            }
        }

        // اسکنر زنده بازار
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(AppLocale.t("pairs_scanning", currentLanguage), color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(AppLocale.t("live_pulse_scanner", currentLanguage), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("BTC/USDT" to "$92,517.25", "ETH/USDT" to "$3,259.01", "SOL/USDT" to "$194.19", "TRX/USDT" to "$0.23").forEach { (pair, price) ->
                    val isSel = selectedPair == pair
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF21262D),
                        border = BorderStroke(1.dp, if (isSel) Color(0xFF00E676) else Color(0xFF30363D)),
                        modifier = Modifier.clickable { selectedPair = pair }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(pair, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(price, color = Color(0xFF00E676), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
