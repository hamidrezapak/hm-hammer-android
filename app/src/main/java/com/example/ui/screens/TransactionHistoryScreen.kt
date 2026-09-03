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
import com.example.model.TradeOrder
import com.example.model.TradeStatus
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.HistorySortColumn
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TransactionHistoryScreen(viewModel: MainViewModel) {
    val trades by viewModel.filteredHistoryTrades.collectAsState()
    val searchQuery by viewModel.historySearchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setHistorySearchQuery(it) },
            placeholder = { Text("جستجو در نمادها یا دلایل خروج...", color = Color.Gray, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF30363D)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("تاریخچه پوزیشن‌ها (${trades.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { viewModel.setHistorySortColumn(HistorySortColumn.PROFIT) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("سود", color = Color.White, fontSize = 10.sp)
                }
                Button(
                    onClick = { viewModel.setHistorySortColumn(HistorySortColumn.DATE) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("تاریخ", color = Color.White, fontSize = 10.sp)
                }
            }
        }

        if (trades.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("معامله‌ای برای نمایش یافت نشد.", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trades) { trade ->
                    TradeHistoryItem(trade = trade, onClose = { viewModel.closeTradeManually(trade) })
                }
            }
        }
    }
}

@Composable
fun TradeHistoryItem(trade: TradeOrder, onClose: () -> Unit) {
    val isProfit = trade.profitUsdt >= 0
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, if (trade.status == TradeStatus.OPEN) Color(0xFF38BDF8) else if (isProfit) Color(0xFF238636) else Color(0xFF8B1E1E)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(trade.symbol, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(
                    color = if (trade.status == TradeStatus.OPEN) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        if (trade.status == TradeStatus.OPEN) "OPEN" else "CLOSED",
                        color = if (trade.status == TradeStatus.OPEN) Color(0xFF38BDF8) else Color.LightGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ورود: $${String.format("%.2f", trade.entryPrice)}", color = Color.LightGray, fontSize = 11.sp)
                Text(
                    "سود/زیان: ${if (isProfit) "+$" else "-$"}${String.format("%.2f", kotlin.math.abs(trade.profitUsdt))} USDT",
                    color = if (isProfit) Color(0xFF00E676) else Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            if (trade.status == TradeStatus.OPEN) {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("بستن دستی معامله (CLOSE)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
