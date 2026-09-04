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
import kotlinx.coroutines.launch

data class ChatMessage(val sender: String, val message: String, val isAi: Boolean)

@Composable
fun AICopilotScreen(
    viewModel: MainViewModel,
    currentLanguage: Any? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val selectedPair by viewModel.selectedPair.collectAsState()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("HM AI", "دستیار هوش مصنوعی واقعی HM HAMMER متصل است (Qwen 27B). سوال، تحلیل ارز یا استراتژی خود را بپرسید:", true)
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                    Text("دستیار هوش مصنوعی زنده HM HAMMER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("مدل پردازش زنده: Qwen 27B • $selectedPair", color = Color(0xFF00E676), fontSize = 10.sp)
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
                        modifier = Modifier.widthIn(max = 320.dp)
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

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF38BDF8))
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("تحلیل یا سوال از هوش مصنوعی...", color = Color.Gray, fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isLoading,
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
                    if (inputText.isNotBlank() && !isLoading) {
                        val query = inputText.trim()
                        inputText = ""
                        messages = messages + ChatMessage("شما", query, false)
                        isLoading = true
                        coroutineScope.launch {
                            val reply = viewModel.queryAiCopilot(query)
                            messages = messages + ChatMessage("HM AI", reply, true)
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("ارسال", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}
