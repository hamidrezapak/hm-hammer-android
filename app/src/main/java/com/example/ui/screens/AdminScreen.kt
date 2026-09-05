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

data class RegisteredAdminUser(
    val username: String,
    val role: String,
    val planType: String,
    val status: String,
    val isPrimary: Boolean = false
)

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    // فقط دو مدیر اصلی ثبت هستند؛ هر کاربر جدید بر اساس خرید پلن اضافه می‌شود
    val usersList = remember {
        mutableStateListOf(
            RegisteredAdminUser(
                username = "حمیدرضا پاکنژاد",
                role = "مدیر ارشد و موسس",
                planType = "دسترسی مادام‌العمر (Full Pro)",
                status = "فعال",
                isPrimary = true
            ),
            RegisteredAdminUser(
                username = "محمد (@masjedi6913)",
                role = "شریک تجاری و مدیر سیستم",
                planType = "دسترسی مادام‌العمر (Full Pro)",
                status = "فعال",
                isPrimary = true
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
                Text("پنل مدیریت دسترسی و لایسنس کاربران", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "کاربران به صورت خودکار پس از خرید یکی از پلن‌های اشتراک فعال خواهند شد.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Text("لیست حساب‌های مجاز و مشترکین فعال", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(usersList) { user ->
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
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(user.role, color = Color.Gray, fontSize = 11.sp)
                            Text("پلن: ${user.planType}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                        }
                        Surface(
                            color = Color(0xFF00E676).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = user.status,
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
