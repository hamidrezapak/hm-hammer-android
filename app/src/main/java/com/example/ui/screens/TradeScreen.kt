package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TradeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val balance by viewModel.usdtBalance.collectAsState()
    val isConnected by viewModel.isApiConnected.collectAsState()
    val isRunning by viewModel.isEngineRunning.collectAsState()
    val engineLog by viewModel.lastEngineLog.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var inputKey by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, if (isConnected) Color(0xFF00E676) else Color(0xFF30363D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("درگاه صرافی والکس (Wallex API)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Surface(
                        color = if (isConnected) Color(0xFF00E676).copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            if (isConnected) "متصل و تایید شده 🟢" else "نیازمند ثبت کلید 🔴",
                            color = if (isConnected) Color(0xFF00E676) else Color.Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        placeholder = { Text("کلید API والکس...", color = Color.Gray, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF30363D)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.verifyAndSaveWallexKey(inputKey) { ok, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("ذخیره و تست", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = if (isRunning) Color(0xFF14321E) else Color(0xFF161B22)),
            border = BorderStroke(1.5.dp, if (isRunning) Color(0xFF00E676) else Color(0xFF38BDF8)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (isRunning) "موتور در حال اسکن..." else "موتور ترید متوقف است",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text("موجودی: $${String.format("%.2f", balance)} USDT", color = Color(0xFF00E676), fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        viewModel.toggleAutoEngine()
                        Toast.makeText(context, if (!isRunning) "موتور فعال شد!" else "موتور خاموش شد.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Color(0xFFFF5252) else Color(0xFF00E676)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (isRunning) "STOP ENGINE ⏹" else "START ENGINE ▶",
                        color = if (isRunning) Color.White else Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    viewModel.executeOrder("BUY", maxAllocation = true)
                    Toast.makeText(context, "سفارش ثبت شد", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("خرید (BUY ALL)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = { showReportDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6FEB)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("سند کارفرما 📑", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = {
                    viewModel.purgeSandbox()
                    Toast.makeText(context, "پاکسازی انجام شد.", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🗑️", color = Color.White)
            }
        }

        Text("گزارش وقایع و لاگ امنیتی:", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(auditLogs) { log ->
                Surface(
                    color = Color(0xFF0D1117),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (log.isSuccess) Color(0xFF238636) else Color(0xFF8B1E1E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(log.eventType, color = if (log.isSuccess) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(log.timestamp, color = Color.Gray, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(log.message, color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        Surface(color = Color(0xFF161B22), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
            Text(engineLog, color = Color(0xFF38BDF8), fontSize = 10.sp, modifier = Modifier.padding(6.dp))
        }
    }

    if (showReportDialog) {
        val reportContent = viewModel.exportAuditReport()
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("سند رسمی عملکرد ربات", color = Color.White, fontSize = 14.sp) },
            text = { Text(reportContent, color = Color.LightGray, fontSize = 10.sp, maxLines = 15) },
            confirmButton = {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("HM_Audit_Report", reportContent))
                    Toast.makeText(context, "سند کپی شد!", Toast.LENGTH_LONG).show()
                    showReportDialog = false
                }) {
                    Text("کپی سند")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text("بستن", color = Color.Gray) }
            },
            containerColor = Color(0xFF161B22)
        )
    }
}
