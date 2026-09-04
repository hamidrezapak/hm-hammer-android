package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.TelegramNotifier
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class AdminRoleAccount(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val roleTag: String,
    val handle: String,
    var tier: String,
    val badgeColor: Color
)

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var selectedAccountForEdit by remember { mutableStateOf<AdminRoleAccount?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val accounts = remember {
        mutableStateListOf(
            AdminRoleAccount("1", "معمار سیستم و فناوری", "Lead System & Technology Architect", "SYSTEM ARCHITECT", "@tech_core", "دسترسی مادام‌العمر (VIP Master)", Color(0xFF00E676)),
            AdminRoleAccount("2", "اپراتور ارشد سیستم", "Senior System Operator", "CORE OPERATOR", "@ops_desk", "دسترسی مادام‌العمر (VIP Master)", Color(0xFF38BDF8)),
            AdminRoleAccount("3", "سرمایه‌گذار تجاری ۱", "Enterprise Investor Desk", "ENTERPRISE", "@investor_desk", "پلن برنزی (۳۰ روزه)", Color(0xFFCD7F32)),
            AdminRoleAccount("4", "حساب آزمایشی و تست مارکت", "Market Sandbox Account", "SANDBOX", "@market_tester", "پلن نقره‌ای (۹۰ روزه)", Color(0xFFC0C0C0))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("مرکز کنترل و مدیریت دسترسی‌ها", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text("System Control & Role Management", color = Color(0xFF38BDF8), fontSize = 11.sp)
            }
        }

        snackbarMessage?.let { msg ->
            Surface(
                color = Color(0xFF238636).copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color(0xFF2EA043)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(msg, color = Color(0xFF00E676), fontSize = 11.sp, modifier = Modifier.padding(10.dp))
            }
        }

        Text("فهرست اپراتورها و سطوح دسترسی:", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(accounts.size) { index ->
                val acc = accounts[index]
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = BorderStroke(1.dp, acc.badgeColor.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(acc.titleFa, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Surface(
                                    color = acc.badgeColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(acc.roleTag, color = acc.badgeColor, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                            Text(acc.titleEn, color = Color(0xFF38BDF8).copy(alpha = 0.9f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Text("${acc.handle} • ${acc.tier}", color = Color.Gray, fontSize = 10.sp)
                        }

                        Button(
                            onClick = { selectedAccountForEdit = acc },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("مدیریت", color = Color(0xFF38BDF8), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    selectedAccountForEdit?.let { target ->
        AlertDialog(
            onDismissRequest = { selectedAccountForEdit = null },
            containerColor = Color(0xFF161B22),
            title = { Text("ویرایش دسترسی ${target.handle}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("سطح دسترسی فعلی: ${target.tier}", color = Color.LightGray, fontSize = 12.sp)
                    Text("آیا وضعیت دسترسی این اپراتور بررسی و تایید شود؟", color = Color.Gray, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            TelegramNotifier.sendNotification("🔒 *بروزرسانی دسترسی اپراتور*\nشناسه: ${target.handle}\nنقش: ${target.titleEn}\nوضعیت: تایید صلاحیت و فعال")
                            snackbarMessage = "مجوز حساب ${target.handle} با موفقیت تایید و همگام‌سازی شد."
                            selectedAccountForEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636))
                ) {
                    Text("تایید و ثبت", color = Color.White, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAccountForEdit = null }) {
                    Text("انصراف", color = Color.Gray, fontSize = 11.sp)
                }
            }
        )
    }
}
