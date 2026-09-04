package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TradeStatus
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PerformanceScreen(viewModel: MainViewModel) {
    val trades by viewModel.trades.collectAsState()
    val radarLogs by viewModel.radarLogs.collectAsState()
    val lastLog by viewModel.lastEngineLog.collectAsState()

    val totalTrades = trades.size
    val closedTrades = trades.filter { it.status == TradeStatus.CLOSED }
    val winTrades = closedTrades.filter { it.profitUsdt > 0 }.size
    val totalProfit = closedTrades.sumOf { it.profitUsdt }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // کارت خلاصه عملکرد
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("گزارش عملکرد واقعی معاملات", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("کل پوزیشن‌ها: $totalTrades", color = Color.Gray, fontSize = 12.sp)
                    Text("پوزیشن‌های بسته شده: ${closedTrades.size}", color = Color.Gray, fontSize = 12.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("معاملات موفق: $winTrades", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "سود/زیان کل: ${if (totalProfit >= 0) "+$" else "-$"}${String.format("%.2f", kotlin.math.abs(totalProfit))} USDT",
                        color = if (totalProfit >= 0) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // بخش آخرین رویدادهای سیستم
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("وضعیت جاری موتور:", color = Color.Gray, fontSize = 11.sp)
                Text(lastLog, color = Color(0xFF38BDF8), fontSize = 11.sp, lineHeight = 16.sp)
            }
        }

        Text("لاگ رادار هوشمند", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(radarLogs) { log ->
                Surface(
                    color = Color(0xFF0D1117),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF21262D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        log,
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
