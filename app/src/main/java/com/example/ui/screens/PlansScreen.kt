package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

data class SubPlan(
    val title: String,
    val price: String,
    val desc: String,
    val isPopular: Boolean = false
)

@Composable
fun PlansScreen() {
    val context = LocalContext.current
    var activePlan by remember { mutableStateOf("برنزی (پایه)") }

    val planList = listOf(
        SubPlan("پلن برنزی (آزمایشی)", "رایگان", "دسترسی به ۳ جفت‌ارز اصلی، رصد الگوها بدون ترید خودکار"),
        SubPlan("پلن نقره‌ای (حرفه‌ای)", "۲۹ تتر / ماهانه", "معاملات خودکار ۲۴ ساعته، کلیه جفت‌ارزها، اهرم تا ۱۰ برابر", isPopular = true),
        SubPlan("پلن طلایی (سازمانی)", "۷۹ تتر / ماهانه", "هوش مصنوعی بدون محدودیت، اتصال مستقیم به صرافی‌ها، مدیریت ریسک ATR")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "پلن‌های اشتراک معامله‌گر HM HAMMER",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "پلن فعال شما: $activePlan",
                color = Color(0xFF00E676),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(planList.size) { i ->
            val plan = planList[i]
            val isCurrent = activePlan.contains(plan.title.substring(4, 9))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    if (plan.isPopular) 2.dp else 1.dp,
                    if (plan.isPopular) Color(0xFF00E676) else Color(0xFF30363D)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(plan.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(plan.price, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(plan.desc, color = Color.Gray, fontSize = 11.sp, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            activePlan = plan.title
                            Toast.makeText(context, "پلن ${plan.title} با موفقیت فعال شد!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrent) Color(0xFF238636) else Color(0xFF1F6FEB)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isCurrent) "پلن فعال است ✓" else "انتخاب و فعال‌سازی پلن",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
