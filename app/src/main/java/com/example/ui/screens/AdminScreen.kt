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
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

data class AdminRoleAccount(
    val titleFa: String,
    val titleEn: String,
    val roleTag: String,
    val handle: String,
    val tier: String,
    val badgeColor: Color
)

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    val accounts = listOf(
        AdminRoleAccount(
            titleFa = "معمار سیستم و فناوری",
            titleEn = "Lead System & Technology Architect",
            roleTag = "SYSTEM ARCHITECT",
            handle = "@tech_core",
            tier = "دسترسی مادام‌العمر (VIP Master)",
            badgeColor = Color(0xFF00E676)
        ),
        AdminRoleAccount(
            titleFa = "اپراتور ارشد سیستم",
            titleEn = "Senior System Operator",
            roleTag = "CORE OPERATOR",
            handle = "@ops_desk",
            tier = "دسترسی مادام‌العمر (VIP Master)",
            badgeColor = Color(0xFF38BDF8)
        ),
        AdminRoleAccount(
            titleFa = "سرمایه‌گذار تجاری ۱",
            titleEn = "Enterprise Investor Desk",
            roleTag = "ENTERPRISE",
            handle = "@investor_desk",
            tier = "پلن برنزی (۳۰ روزه)",
            badgeColor = Color(0xFFCD7F32)
        ),
        AdminRoleAccount(
            titleFa = "حساب آزمایشی و تست مارکت",
            titleEn = "Market Sandbox Account",
            roleTag = "SANDBOX",
            handle = "@market_tester",
            tier = "پلن نقره‌ای (۹۰ روزه)",
            badgeColor = Color(0xFFC0C0C0)
        )
    )

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
                Text(
                    "مرکز کنترل و مدیریت دسترسی‌ها",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    "System Control & Role Management",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp
                )
            }
        }

        Text(
            "فهرست اپراتورها و سطوح دسترسی:",
            color = Color.LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

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
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    acc.titleFa,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Surface(
                                    color = acc.badgeColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        acc.roleTag,
                                        color = acc.badgeColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                acc.titleEn,
                                color = Color(0xFF38BDF8).copy(alpha = 0.9f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${acc.handle} • ${acc.tier}",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = { },
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
}
