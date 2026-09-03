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
import com.example.ui.components.LanguageOption
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

data class PlanItem(
    val id: String,
    val title: String,
    val price: String,
    val desc: String,
    val isPopular: Boolean = false
)

@Composable
fun SubscriptionsScreen(
    viewModel: MainViewModel? = null,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    val context = LocalContext.current
    var selectedPlanId by remember { mutableStateOf("starter") }

    val plans = listOf(
        PlanItem("starter", "پلن برنزی (تست استراتژی)", "رایگان", "دسترسی به مانیتور ۳ جفت‌ارز تتری، بدون ترید خودکار"),
        PlanItem("pro", "پلن نقره‌ای (معامله‌گر VIP)", "۲۹ تتر / ماهانه", "اتصال به موتور چکش، ترید خودکار ۲۴ ساعته، اهرم تا ۱۰ برابر", isPopular = true),
        PlanItem("master", "پلن طلایی (سازمانی Master)", "۷۹ تتر / ماهانه", "دستیار اختصاصی هوش مصنوعی، ترید بدون کارمزد، اولویت اجرای اردرها")
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
                "سطوح دسترسی و پلن‌های تجاری HM HAMMER",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "پلن فعال شما: ${plans.find { it.id == selectedPlanId }?.title}",
                color = Color(0xFF00E676),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(plans.size) { i ->
            val plan = plans[i]
            val isCurrent = selectedPlanId == plan.id

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
                        Text(plan.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(plan.price, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(plan.desc, color = Color.Gray, fontSize = 11.sp, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            selectedPlanId = plan.id
                            Toast.makeText(context, "✅ پلن «${plan.title}» برای شما با موفقیت فعال شد!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrent) Color(0xFF238636) else Color(0xFF1F6FEB)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isCurrent) "پلن فعال است ✓" else "انتخاب و ارتقا به این پلن",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
