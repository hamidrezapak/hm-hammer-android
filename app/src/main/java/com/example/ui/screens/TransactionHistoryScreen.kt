package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.AppLocale
import com.example.ui.viewmodel.MainViewModel

data class HistoryItem(val pair: String, val side: String, val entry: String, val exit: String, val pnl: String, val isWin: Boolean, val time: String)

@Composable
fun TransactionHistoryScreen(
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    var filterPair by remember { mutableStateOf("ALL") }

    val historyItems = listOf(
        HistoryItem("DOGE/USDT", "LONG 2x", "$0.3850", "$0.2586", "-$5.05 (-65.6%)", false, "16:08"),
        HistoryItem("BTC/USDT", "LONG 2x", "$89,450.0", "$88,165.3", "-$0.22 (-2.8%)", false, "15:08"),
        HistoryItem("SOL/USDT", "LONG 2x", "$189.500", "$198.500", "+$0.37 (+4.8%)", true, "12:08"),
        HistoryItem("ETH/USDT", "LONG 2x", "$3,410.0", "$3,324.1", "-$0.39 (-5.0%)", false, "11:08"),
        HistoryItem("XRP/USDT", "SHORT 2x", "$1.950", "$1.850", "+$1.10 (+14.3%)", true, "09:08")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // کارت‌های آمار و برآیند
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)), border = BorderStroke(1.dp, Color(0xFF30363D))) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(AppLocale.t("matched_trades", currentLanguage), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("7 / 7", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
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
                        Text(AppLocale.t("filtered_pnl", currentLanguage), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("-$28.86", color = Color(0xFFFF5252), fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }

        // فیلتر بر اساس جفت‌ارز
        item {
            Text(AppLocale.t("filter_asset_pair", currentLanguage), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ALL", "BTC/USDT", "ETH/USDT", "SOL/USDT", "XRP/USDT").forEach { p ->
                    val isSel = filterPair == p
                    Button(
                        onClick = { filterPair = p },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSel) Color(0xFF00E676) else Color(0xFF21262D)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(p, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // تیتر لیست معاملات
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(AppLocale.t("time_status", currentLanguage), color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(AppLocale.t("pnl_usdt", currentLanguage), color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(AppLocale.t("entry_exit", currentLanguage), color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(AppLocale.t("pair_side", currentLanguage), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ردیف‌های لیست معاملات
        items(historyItems.filter { filterPair == "ALL" || it.pair == filterPair }) { item ->
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
                    Surface(color = if (item.isWin) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text(if (item.isWin) "TP1" else "SL", color = if (item.isWin) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Text(item.pnl, color = if (item.isWin) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("In: ${item.entry}", color = Color.White, fontSize = 10.sp)
                        Text("Out: ${item.exit}", color = Color.Gray, fontSize = 9.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(item.pair, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text(item.side, color = if (item.side.contains("LONG")) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 9.sp)
                    }
                }
            }
        }
    }
}
