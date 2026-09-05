package com.example.ui.screens

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
import com.example.ui.viewmodel.MainViewModel

data class AuthorizedAccount(
    val title: String,
    val role: String,
    val planType: String,
    val status: String
)

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    val accounts = remember {
        mutableStateListOf(
            AuthorizedAccount(
                title = "هسته معاملات الگوریتمی همر (Hammer Core Engine)",
                role = "مدیریت الگوریتم‌های کوانت و استراتژی چکش",
                planType = "دسترسی نامحدود VIP",
                status = "فعال و متصل"
            ),
            AuthorizedAccount(
                title = "موتور اجرای پرسرعت و پایپ‌لاین دادگان (Hammer Pipeline & Execution Engine)",
                role = "همگام‌سازی بلادرنگ OrderBook، مانیتورینگ WebSocket و اجرای کم‌تاخیر API",
                planType = "دسترسی نامحدود زیرساخت",
                status = "فعال و متصل"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E14))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("پنل نظارت بر زیرساخت و کاربران", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "کاربران مجاز صرفاً پس از فعال‌سازی اشتراک‌های معتبر در این بخش لیست و احراز هویت می‌شوند.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Text("گره‌های اجرایی الگوریتمی و حساب‌های فعال", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(accounts) { acc ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                            Text(acc.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(acc.role, color = Color.Gray, fontSize = 11.sp, lineHeight = 16.sp)
                            Text("پلن: ${acc.planType}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF00E676).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = acc.status,
                                color = Color(0xFF00E676),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
