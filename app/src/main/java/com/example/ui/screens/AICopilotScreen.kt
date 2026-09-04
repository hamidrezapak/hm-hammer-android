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
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

data class ChatMessage(val sender: String, val message: String, val isAi: Boolean)

@Composable
fun AICopilotScreen(
    viewModel: MainViewModel,
    currentLanguage: Any? = null
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("HM AI", "دستیار هوشمند الگوریتم چکش متصل است. آنالیز کندل‌های زنده، سطوح فیبوناچی و ارزیابی ریسک فعال می‌باشد. سوال یا درخواست تحلیل خود را مطرح کنید:", true)
            )
        )
    }
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF38BDF8)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("دستیار هوش مصنوعی HM HAMMER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("آنالیز جریان سفارشات و کندل چکش", color = Color(0xFF00E676), fontSize = 10.sp)
                }
                Surface(
                    color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("LIVE AI", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val bg = if (msg.isAi) Color(0xFF161B22) else Color(0xFF238636).copy(alpha = 0.8f)
                val align = if (msg.isAi) Alignment.Start else Alignment.End

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                    Surface(
                        color = bg,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (msg.isAi) Color(0xFF30363D) else Color(0xFF2EA043)),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(msg.sender, color = if (msg.isAi) Color(0xFF38BDF8) else Color(0xFFE6EDF3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(msg.message, color = Color.White, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("تحلیل یا سوال از بازار...", color = Color.Gray, fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF30363D)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val query = inputText.trim()
                        val response = viewModel.queryAiCopilot(query)
                        messages = messages + ChatMessage("شما", query, false) + ChatMessage("HM AI", response, true)
                        inputText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("ارسال", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}
