package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.DarkNavyBg

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val hasChart: Boolean = false,
    val pair: String = "BTC/USDT",
    val timeframe: String = "15M",
    val hammerIndex: Int = 8
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
                text = if (isFa) "سلام! من دستیار هوشمند و تحلیل‌گر الگوریتم HM HAMMER هستم. هر جفت‌ارز یا تحلیلی می‌خواهید بگویید تا چارت و نقاط ورود و الگوهای چکش را برایتان ترسیم و تحلیل کنم."
                else "Hello! I am your HM HAMMER AI Copilot. Ask me to draw any chart, identify hammer patterns, or audit live market risks.",
                hasChart = false
            ),
            ChatMessage(
                id = "2",
                isUser = false,
                text = if (isFa) "📌 چارت ۱۵ دقیقه‌ای درخواستی BTC/USDT با مشخصات ستاپ ورود چکش و شیب EMA(200):"
                else "📌 Requested BTC/USDT 15M Chart with Hammer confirmation and EMA(200) trend filter:",
                hasChart = true,
                pair = "BTC/USDT",
                timeframe = "15M",
                hammerIndex = 9
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(12.dp)
    ) {
        // نوار هدر مدرن دستیار
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
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF00E676), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFa) "موتور تحلیل چندزبانه و هوش مصنوعی HM HAMMER" else "HM HAMMER MULTI-LANGUAGE AI COPILOT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        // جریان پیام‌ها
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // باکس ورودی پیام
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        if (isFa) "مثلاً: چارت ۴ ساعته اتریوم رو بکش و تحلیل کن..."
                        else "e.g. Draw 4H ETH chart with hammer setup...",
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
                        val prompt = inputText
                        messages.add(ChatMessage(id = System.currentTimeMillis().toString(), isUser = true, text = prompt))
                        inputText = ""
                        
                        // پاسخ هوشمند با رندر نمودار
                        messages.add(
                            ChatMessage(
                                id = (System.currentTimeMillis() + 1).toString(),
                                isUser = false,
                                text = if (isFa) "تحلیل الگوی چکش و پایش سطوح فیبوناچی برای درخواست شما انجام شد:"
                                else "Setup verified. Hammer wick ratio and ATR dynamic bands rendered below:",
                                hasChart = true,
                                pair = "SOL/USDT",
                                timeframe = "1H",
                                hammerIndex = 11
                            )
                        )
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF00B0FF))),
                        CircleShape
                    )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (message.isUser) Color(0xFF1F6FEB) else Color(0xFF161B22),
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (message.isUser) 14.dp else 2.dp,
                bottomEnd = if (message.isUser) 2.dp else 14.dp
            ),
            border = if (!message.isUser) BorderStroke(1.dp, Color(0xFF30363D)) else null,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                // رندر مینی‌چارت در صورت فعال بودن فلگ چارت
                if (message.hasChart) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFF0D1117),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF21262D)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${message.pair} • ${message.timeframe}",
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Text("HAMMER DETECTED 🔨", color = Color(0xFFFFB703), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height
                                    val candleCount = 14
                                    val candleW = w / candleCount

                                    for (i in 0 until candleCount) {
                                        val x = i * candleW + 6f
                                        val isHammer = i == message.hammerIndex
                                        val isUp = if (isHammer) true else i % 2 == 0
                                        val color = if (isHammer) Color(0xFFFFB703) else if (isUp) Color(0xFF00E676) else Color(0xFFFF5252)

                                        val bodyTop = if (isHammer) h * 0.45f else (h * 0.3f) + (i * 3f % (h * 0.3f))
                                        val bodyH = if (isHammer) 16f else 28f
                                        val wickBottom = if (isHammer) bodyTop + 55f else bodyTop + bodyH + 18f
                                        val wickTop = bodyTop - 10f

                                        // رسم سایه (Wick)
                                        drawLine(color, Offset(x + candleW / 4, wickTop), Offset(x + candleW / 4, wickBottom), strokeWidth = if (isHammer) 3.5f else 2f)
                                        // رسم بدنه (Body)
                                        drawRect(color, Offset(x, bodyTop), Size(candleW / 2, bodyH))
                                    }

                                    // خط روند میانگین متحرک EMA
                                    val emaPath = Path()
                                    emaPath.moveTo(0f, h * 0.75f)
                                    emaPath.quadraticBezierTo(w * 0.5f, h * 0.6f, w, h * 0.35f)
                                    drawPath(emaPath, Color(0xFF38BDF8), style = Stroke(width = 2.5f))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "EMA(200) Macro Trend: BULLISH | Risk: 2% | R:R ~ 1:1.6",
                                color = Color.Gray,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
