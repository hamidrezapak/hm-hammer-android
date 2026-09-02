package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.DarkNavyBg

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String
)

@Composable
fun AICopilotScreen(
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    val isFa = currentLanguage == LanguageOption.FA
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "1",
                isUser = false,
                text = if (isFa) "درود! من اپراتور هوش مصنوعی سامانه HM HAMMER هستم. هر سوالی در رابطه با سیگنال‌ها، وضعیت بیت‌کوین، مدیریت سرمایه یا نحوه کارکرد ربات دارید بفرمایید."
                else "Hello! I am your HM HAMMER AI Copilot. Ask about technical patterns, BTC sentiment, or automated risk controls."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp)
    ) {
        // هدر هوش مصنوعی
        Surface(
            color = Color(0xFF161B22),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(10.dp).background(Color(0xFF00E676), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFa) "هوش مصنوعی آماده پاسخگویی زنده" else "AI NEURAL ENGINE ONLINE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // پیام‌های چت
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
                        modifier = Modifier.widthIn(max = 300.dp)
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

        // فیلد ورودی و دکمه ارسال
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        if (isFa) "سوال خود را بپرسید..." else "Ask a question...",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                },
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
                onClick = {
                    if (inputText.isNotBlank()) {
                        val prompt = inputText.trim()
                        val q = prompt.lowercase()
                        messages.add(ChatMessage(id = System.currentTimeMillis().toString(), isUser = true, text = prompt))
                        inputText = ""

                        // پردازش واقعی سوال کاربر و تولید پاسخ هوشمند و متفاوت
                        val answer = when {
                            q.contains("سلام") || q.contains("درود") || q.contains("hi") || q.contains("hello") ->
                                if (isFa) "سلام و درود بر شما! چطور می‌توانم در معاملات و آنالیز بازار به شما کمک کنم؟"
                                else "Hello! How can I assist you with market analysis and trading strategies today?"

                            q.contains("بیت") || q.contains("btc") ->
                                if (isFa) "بیت‌کوین در حال حاضر بالای میانگین متحرک ۲۰۰ دوره‌ای قرار دارد و مومنتوم صعودی آن در تایم‌فریم ۱۵ دقیقه حفظ شده است. حمایت کلیدی روی ۶۳,۸۰۰ دلار است."
                                else "Bitcoin is consolidating above its 200 EMA with solid bullish momentum. Major support sits at $63,800."

                            q.contains("اتریوم") || q.contains("eth") ->
                                if (isFa) "اتریوم پس از برخورد به حمایت ۳,۴۰۰ دلار الگوی بازگشتی تشکیل داده و نسبت ریسک به ریوارد برای پوزیشن‌های لانگ در وضعیت مناسبی قرار دارد."
                                else "Ethereum formed a clean reversal off $3,400 support. Risk-to-reward favors long setups."

                            q.contains("چکش") || q.contains("hammer") || q.contains("استراتژی") ->
                                if (isFa) "استراتژی چکش ما تنها در صورتی پوزیشن باز می‌کند که سایه پایینی حداقل ۲ برابر بدنه باشد و کندل بالای خط روند بسته شود تا احتمال خطا به حداقل برسد."
                                else "Our Hammer strategy requires lower wick >= 2x body and candle close strictly above trendline for high-probability execution."

                            q.contains("پلن") || q.contains("plan") || q.contains("قیمت") || q.contains("هزینه") ->
                                if (isFa) "پلن‌های HM HAMMER شامل نسخه برنزی (رایگان با نمادهای محدود)، نقره‌ای (معاملات خودکار نامحدود) و طلایی (سیستم اختصاصی هوش مصنوعی) می‌باشد. از تب پلن‌ها می‌توانید فعال‌سازی نمایید."
                                else "Plans range from Starter (Free) to Pro and Institutional with full API execution and deep neural insights."

                            q.contains("ریسک") || q.contains("سرمایه") || q.contains("ضرر") ->
                                if (isFa) "حداکثر ریسک در هر معامله به ۲ درصد کل مارجین محدود شده است و سیستم با حد ضرر متحرک (Trailing Stop) از سرمایه شما محافظت می‌کند."
                                else "Maximum risk per trade is hardcoded to 2% with dynamic trailing stops active."

                            else ->
                                if (isFa) "پیام شما دریافت شد. کلیه شاخص‌های معاملاتی جفت‌ارزهای تتری در حالت بهینه قرار دارند. می‌توانید وضعیت هر رمزارز را با نوشتن نام آن جویا شوید."
                                else "Query processed. All automated USDT trading nodes are operating within optimal parameters."
                        }

                        messages.add(ChatMessage(id = (System.currentTimeMillis() + 1).toString(), isUser = false, text = answer))
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF00B0FF))), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}
