package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.AppLocale
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PerformanceScreen(
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // آمار خلاصه
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)), border = BorderStroke(1.dp, Color(0xFF30363D))) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("tab_performance", currentLanguage), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("OPS 7", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)), border = BorderStroke(1.dp, Color(0xFF30363D))) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("win_rate", currentLanguage), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("28.6%", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)), border = BorderStroke(1.dp, Color(0xFF30363D))) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("pnl_usdt", currentLanguage), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("-$29.1", color = Color(0xFFFF5252), fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }

        // ترمینال رادار زنده
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(AppLocale.t("radar_terminal", currentLanguage), color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Scanning 15 pairs... Active: BTC, ETH, SOL, XRP [ATR OK, Vol Spike]", color = Color.LightGray, fontSize = 10.sp, lineHeight = 14.sp)
                }
            }
        }

        // ردیف‌های پوزیشن‌های فعال
        item {
            Text(AppLocale.t("recent_exec", currentLanguage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }

        item {
            PositionRowItem(pair = "BTC/USDT", side = "LONG 2x", entry = "$89,450.0", pnl = "-2.87%", isWin = false)
        }
        item {
            PositionRowItem(pair = "SOL/USDT", side = "LONG 2x", entry = "$189.50", pnl = "+5.29%", isWin = true)
        }
        item {
            PositionRowItem(pair = "ETH/USDT", side = "LONG 2x", entry = "$3,410.0", pnl = "-5.04%", isWin = false)
        }
    }
}

@Composable
fun PositionRowItem(pair: String, side: String, entry: String, pnl: String, isWin: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, Color(0xFF30363D)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = if (isWin) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                Text(if (isWin) "TP1" else "SL", color = if (isWin) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
            Text(pnl, color = if (isWin) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("Entry: $entry", color = Color.White, fontSize = 10.sp)
            Text(pair, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}
