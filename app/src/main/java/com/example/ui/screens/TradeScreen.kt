package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TradeScreen(viewModel: MainViewModel) {
    var selectedLeverage by remember { mutableStateOf("1x") }
    var selectedAllocation by remember { mutableStateOf(25) }
    var showApiDialog by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf("") }
    var apiMessage by remember { mutableStateOf("") }

    val isRunning by viewModel.isEngineRunning.collectAsState()
    val isApiConnected by viewModel.isApiConnected.collectAsState()
    val currentPair by viewModel.selectedPair.collectAsState()

    val leverages = listOf("1x", "2x", "5x", "10x", "20x", "50x")
    val allocations = listOf(10, 25, 50, 75, 100)

    if (showApiDialog) {
        AlertDialog(
            onDismissRequest = { showApiDialog = false },
            title = {
                Text(
                    text = "اتصال کلید صرافی (API Key)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "کلید API دریافتی از صرافی را وارد کنید:",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            focusedLabelColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (apiMessage.isNotBlank()) {
                        Text(apiMessage, color = Color(0xFFFFB300), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyAndSaveWallexKey(apiKeyInput) { success, msg ->
                            apiMessage = msg
                            if (success) showApiDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                ) {
                    Text("ذخیره و اتصال")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiDialog = false }) {
                    Text("انصراف", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF161B22)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E14))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("جفت‌ارز معاملاتی", color = Color.Gray, fontSize = 11.sp)
                    Text(currentPair, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showApiDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApiConnected) Color(0xFF1B5E20) else Color(0xFFE65100)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isApiConnected) "API متصل است" else "تنظیم و اتصال API",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Button(
            onClick = { viewModel.toggleAutoEngine() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Color(0xFFC62828) else Color(0xFF00C853)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(
                text = if (isRunning) "■ توقف موتور ترید خودکار" else "▶ استارت ترید خودکار بر اساس استراتژی زنده",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("ضریب اهرم معاملاتی (Leverage)", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leverages.forEach { lev ->
                        val isSelected = selectedLeverage == lev
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) Color(0xFF00E5FF) else Color(0xFF21262D),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedLeverage = lev }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lev,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("تخصیص حجم موجودی", color = Color.LightGray, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allocations.forEach { alloc ->
                        val isSelected = selectedAllocation == alloc
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) Color(0xFF00B0FF) else Color(0xFF21262D),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedAllocation = alloc }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$alloc%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.executeOrder("BUY", true) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("خرید سریع (BUY)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.executeOrder("SELL", true) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("فروش سریع (SELL)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
