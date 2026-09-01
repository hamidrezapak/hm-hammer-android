package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.AppLocale
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TradeScreen(
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    val context = LocalContext.current
    var botActive by remember { mutableStateOf(true) }
    var trailingLock by remember { mutableStateOf(true) }
    var selectedAtrMultiplier by remember { mutableStateOf("1.5x") }
    var selectedTargetCount by remember { mutableStateOf("3") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // کلید روشن/خاموش ربات ترید خودکار
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, if (botActive) Color(0xFF00E676) else Color.Gray),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(AppLocale.t("auto_trading_title", currentLanguage), color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(AppLocale.t("auto_trading_sub", currentLanguage), color = Color.LightGray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = botActive,
                        onCheckedChange = {
                            botActive = it
                            Toast.makeText(context, if (botActive) "موتور معاملات فعال شد" else "موتور معاملات متوقف شد", Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFF00E676))
                    )
                }
            }
        }

        // شاخص‌های خلاصه ریسک
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryBox(modifier = Modifier.weight(1f), title = AppLocale.t("rr_ratio", currentLanguage), value = "1:1.3", color = Color(0xFF00E676))
                SummaryBox(modifier = Modifier.weight(1f), title = AppLocale.t("sl_mode", currentLanguage), value = "$selectedAtrMultiplier ATR", color = Color(0xFFFFB703))
                SummaryBox(modifier = Modifier.weight(1f), title = AppLocale.t("batch_queue", currentLanguage), value = "OPS 0", color = Color(0xFF38BDF8))
            }
        }

        // تنظیمات حد ضرر داینامیک
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(AppLocale.t("dyn_sl_config", currentLanguage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("3.0x", "2.5x", "2.0x", "1.5x", "1.0x", "0.8x").forEach { mul ->
                            val isSel = selectedAtrMultiplier == mul
                            Button(
                                onClick = { selectedAtrMultiplier = mul },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSel) Color(0xFF00E676) else Color(0xFF21262D)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(mul, color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(AppLocale.t("trailing_lock", currentLanguage), color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(AppLocale.t("trailing_desc", currentLanguage), color = Color.Gray, fontSize = 9.sp)
                        }
                        Switch(
                            checked = trailingLock,
                            onCheckedChange = { trailingLock = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFF00E676))
                        )
                    }
                }
            }
        }

        // اهداف حد سود و سطوح زنده
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(AppLocale.t("dyn_tp_config", currentLanguage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("4", "3", "2", "1").forEach { count ->
                            val isSel = selectedTargetCount == count
                            Button(
                                onClick = { selectedTargetCount = count },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSel) Color(0xFF00E676) else Color(0xFF21262D)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("TP $count", color = if (isSel) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(AppLocale.t("live_price", currentLanguage) + ": $92,185.2", color = Color.White, fontSize = 11.sp)
                        Text(AppLocale.t("sim_levels", currentLanguage), color = Color(0xFFFFB703), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryBox(modifier: Modifier = Modifier, title: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, Color(0xFF30363D)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
    }
}
