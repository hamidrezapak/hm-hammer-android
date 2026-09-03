package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.DarkNavyBg

data class ChatMessage(val id: String, val isUser: Boolean, val text: String)

@Composable
fun AICopilotScreen(currentLanguage: LanguageOption = LanguageOption.FA) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", false, "درود! دستیار هوش مصنوعی HM HAMMER آنلاین است. می‌توانید با دکمه‌های زیر یا با نوشتن سوال، وضعیت استراتژی و الگوها را تست کنید.")
        )
    }

    fun handleSend(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(System.currentTimeMillis().toString(), true, text))
        inputText = ""

        val q = text.lowercase()
        val reply = when {
            q.contains("بیت") || q.contains("btc") ->
                "تحلیل بیت‌کوین: قیمت بالای EMA(200) تثبیت شده و ساختار صعودی حفظ گردیده است. الگوی چکش در محدوده حمایتی ۶۴,۲۰۰ تایید شد."
            q.contains("تست") || q.contains("خرید") ->
                "سیستم آماده معامله است. در تب 'معامله' می‌توانید با دکمه BUY ALL تا ۹۰٪ سرمایه را در ستاپ چکش وارد پوزیشن کنید."
            q.contains("استراتژی") || q.contains("ریسک") ->
                "مدیریت سرمایه فعال: حد ضرر بر مبنای ۱.۵ برابر ATR و تارگت‌ها به ترتیب R:R ۱ به ۱، ۱ به ۱.۶ و ۱ به ۲.۲ چیده شده‌اند."
            q.contains("پلن") ->
                "پلن‌های کاربری در سه سطح برنزی، نقره‌ای و سازمانی تنظیم شده‌اند و از تب پلن‌ها به صورت آنی قابل فعال‌سازی هستند."
            else ->
                "فرمان شما دریافت شد. تمامی ماژول‌های اسکن بازار و مدیریت ریسک در وضعیت ایمن و عملیاتی قرار دارند."
        }
        messages.add(ChatMessage((System.currentTimeMillis() + 1).toString(), false, reply))
        Toast.makeText(context, "پاسخ تحلیل تولید شد", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp)
    ) {
        Surface(
            color = Color(0xFF161B22),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(Color(0xFF00E676), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text("دستیار تحلیل زنده HM HAMMER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        // دکمه‌های آماده تست سریع برای کاربر
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("وضعیت بیت‌کوین چطوره؟", "تست استراتژی چکش", "بررسی مدیریت ریسک", "پلن‌های فعال").forEach { chip ->
                AssistChip(
                    onClick = { handleSend(chip) },
                    label = { Text(chip, fontSize = 10.sp, color = Color.White) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF21262D))
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        color = if (msg.isUser) Color(0xFF1F6FEB) else Color(0xFF161B22),
                        shape = RoundedCornerShape(12.dp),
                        border = if (!msg.isUser) BorderStroke(1.dp, Color(0xFF30363D)) else null,
                        modifier = Modifier.widthIn(max = 290.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("سوال تریدینگ خود را بنویسید...", color = Color.Gray, fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF161B22),
                    unfocusedContainerColor = Color(0xFF161B22),
                    focusedBorderColor = Color(0xFF00E676),
                    unfocusedBorderColor = Color(0xFF30363D),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { handleSend(inputText) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF00B0FF))), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}
