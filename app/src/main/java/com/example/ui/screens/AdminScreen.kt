package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

data class ManagedUser(
    val id: String,
    val name: String,
    val handle: String,
    var plan: String,
    var duration: String,
    val isFounder: Boolean = false
)

@Composable
fun AdminScreen(viewModel: MainViewModel? = null) {
    val context = LocalContext.current

    // لیست اعضای سیستم با تثبیت اکانت موسسین روی VIP مادام‌العمر
    val users = remember {
        mutableStateListOf(
            ManagedUser("1", "حمیدرضا پاکنژاد (موسس)", "@hamidrezapak", "VIP Master", "مادام‌العمر (Lifetime)", true),
            ManagedUser("2", "محمد (شریک)", "@masjedi6913", "VIP Master", "مادام‌العمر (Lifetime)", true),
            ManagedUser("3", "کاربر سرمایه‌گذار ۱", "@investor_crypto", "برنزی (پایه)", "۳۰ روزه"),
            ManagedUser("4", "کاربر تستی VIP", "@vip_trader", "نقره‌ای (Pro)", "۹۰ روزه")
        )
    }

    var selectedUser by remember { mutableStateOf<ManagedUser?>(null) }
    var selectedPlanToGrant by remember { mutableStateOf("طلایی (سازمانی)") }
    var selectedDurationToGrant by remember { mutableStateOf("۳ ماهه") }

    val availablePlans = listOf("برنزی (پایه)", "نقره‌ای (Pro)", "طلایی (سازمانی)", "VIP Master")
    val availableDurations = listOf("۱ ماهه", "۳ ماهه", "۶ ماهه", "۱ ساله", "مادام‌العمر")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ۱. هدر وضعیت پنل نظارت کل
        Surface(
            color = Color(0xFF161B22),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("مرکز کنترل و اعطای اشتراک کاربران HM HAMMER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("مدیریت دستی تایید پلن‌ها، سطوح دسترسی و اکانت‌های VIP موسسین", color = Color(0xFF00E676), fontSize = 10.sp)
            }
        }

        // ۲. بخش تغییر و ارتقای پلن کاربر انتخاب‌شده
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
            border = BorderStroke(1.dp, Color(0xFF21262D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (selectedUser != null) "تنظیم سطح دسترسی برای: ${selectedUser?.name}" else "کاربری را از لیست پایین انتخاب کنید",
                    color = if (selectedUser != null) Color(0xFF38BDF8) else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                if (selectedUser != null && !selectedUser!!.isFounder) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("انتخاب پلن مورد نظر:", color = Color.LightGray, fontSize = 10.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availablePlans.forEach { p ->
                            FilterChip(
                                selected = (selectedPlanToGrant == p),
                                onClick = { selectedPlanToGrant = p },
                                label = { Text(p, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00E676),
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Text("مدت زمان اعتبار هدیه / اشتراک:", color = Color.LightGray, fontSize = 10.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availableDurations.forEach { d ->
                            FilterChip(
                                selected = (selectedDurationToGrant == d),
                                onClick = { selectedDurationToGrant = d },
                                label = { Text(d, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF38BDF8),
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            selectedUser?.let { u ->
                                u.plan = selectedPlanToGrant
                                u.duration = selectedDurationToGrant
                                Toast.makeText(context, "پلن ${u.name} به $selectedPlanToGrant ($selectedDurationToGrant) تغییر یافت!", Toast.LENGTH_LONG).show()
                                selectedUser = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ثبت و اعمال فوری تغییرات اشتراک ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // ۳. لیست تمام کاربران سیستم
        Text("لیست کاربران و سرمایه‌گذاران متصل:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                val isCurrent = selectedUser?.id == user.id

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (user.isFounder) Color(0xFF1B2A1E) else Color(0xFF161B22)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (user.isFounder) Color(0xFF00E676) else if (isCurrent) Color(0xFF38BDF8) else Color(0xFF30363D)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (user.isFounder) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("👑 FOUNDER", color = Color(0xFFFFB703), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${user.handle} • پلن: ${user.plan}", color = Color(0xFF00E676), fontSize = 10.sp)
                            Text("اعتبار: ${user.duration}", color = Color.Gray, fontSize = 9.sp)
                        }

                        if (!user.isFounder) {
                            Button(
                                onClick = { selectedUser = user },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCurrent) Color(0xFF38BDF8) else Color(0xFF21262D)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isCurrent) "در حال ویرایش" else "مدیریت پلن", color = Color.White, fontSize = 10.sp)
                            }
                        } else {
                            Surface(
                                color = Color(0xFF00E676).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "دائمی (VIP)",
                                    color = Color(0xFF00E676),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
