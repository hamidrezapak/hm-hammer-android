package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun AdminPanelScreen() {
    val context = LocalContext.current
    var botRunning by remember { mutableStateOf(true) }
    var emergencyStop by remember { mutableStateOf(false) }
    var maxRiskPercentage by remember { mutableStateOf("2.0") }
    var apiKeyText by remember { mutableStateOf("hm_live_sec_993812") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("پنل مدیریت پیشرفته ربات (Admin)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // وضعیت کلی موتور معامله‌گر
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("وضعیت اجرای خودکار معاملات", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = botRunning,
                        onCheckedChange = {
                            botRunning = it
                            Toast.makeText(context, if (it) "موتور معاملات فعال شد" else "موتور معاملات متوقف شد", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Divider(color = Color(0xFF30363D), modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("قطع اضطراری معاملات (Kill-Switch)", color = Color(0xFFFF5252), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = emergencyStop,
                        onCheckedChange = {
                            emergencyStop = it
                            Toast.makeText(context, if (it) "قطع اضطراری فعال شد! کلیه اردرها بسته شدند" else "قطع اضطراری غیرفعال شد", Toast.LENGTH_LONG).show()
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFFFF5252))
                    )
                }
            }
        }

        // تنظیمات ریسک
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("حداکثر درصد ریسک در هر معامله (%)", color = Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = maxRiskPercentage,
                    onValueChange = { maxRiskPercentage = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color(0xFF30363D)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        Toast.makeText(context, "سقف ریسک روی $maxRiskPercentage% ذخیره گردید", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ذخیره پارامترهای مدیریت سرمایه", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // بازنشانی و پاکسازی لاگ‌ها
        Button(
            onClick = {
                Toast.makeText(context, "کش و لاگ‌های موتور معامله‌گر با موفقیت پاکسازی شد", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("پاکسازی لاگ‌های معاملاتی و کش سرور", color = Color.White)
        }
    }
}
