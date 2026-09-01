package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ManagedUserItem
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.BorderStrokeColor
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.HammerLongGreen
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.StarShortRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val adminData by viewModel.adminData.collectAsState()
    val isAlive by viewModel.isRadarPulseAlive.collectAsState()

    var selectedUserForPlanChange by remember { mutableStateOf<ManagedUserItem?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 12.dp)
            .testTag("admin_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Admin Master Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMMAND CENTER 👑",
                    color = BrightGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .background(BrightGold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .border(1.dp, BrightGold, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "ADMIN ACCESS",
                        color = BrightGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 4 KPI Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard(
                    title = "TOTAL USERS",
                    value = "${adminData.totalUsers}",
                    valueColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "ACTIVE BOTS",
                    value = "${adminData.activeBots}",
                    valueColor = NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard(
                    title = "DEPLOYED CAPITAL",
                    value = adminData.totalInvested,
                    valueColor = BrightGold,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "REAL WALLEX VOL",
                    value = adminData.turnover,
                    valueColor = NeonMint,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Server System Control Box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderStrokeColor, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LINUX ENGINE SYSTEM DAEMON",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (isAlive) NeonEmerald else StarShortRed, CircleShape)
                        )
                        Text(
                            text = if (isAlive) " ONLINE" else " RESTARTING...",
                            color = if (isAlive) NeonEmerald else StarShortRed,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "System command orchestrator:\nsystemctl restart hmserver hmbot nginx",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 15.sp
                )

                Button(
                    onClick = { viewModel.restartEngine() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("admin_restart_engine_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkCardElevated,
                        contentColor = NeonMint
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonMint.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "🔄 RESTART & RE-SYNC ANALYTIC DAEMON",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Users Management List Header
        item {
            Text(
                text = "USER ACCOUNTS & SUBSCRIPTION TIERS",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(adminData.users) { user ->
            AdminUserCard(
                user = user,
                onChangePlanClick = { selectedUserForPlanChange = user }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Plan Change Dialog
    if (selectedUserForPlanChange != null) {
        val u = selectedUserForPlanChange!!
        AlertDialog(
            onDismissRequest = { selectedUserForPlanChange = null },
            title = {
                Text(
                    text = "CHANGE PLAN FOR @${u.user}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("VIP", "ELITE", "PRO", "STANDARD").forEach { plan ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkNavyBg, RoundedCornerShape(8.dp))
                                .border(1.dp, if (u.plan == plan) NeonMint else BorderStrokeColor, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.adminChangeUserPlan(u.id, plan)
                                    selectedUserForPlanChange = null
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "PLAN $plan ${if (u.plan == plan) "(CURRENT)" else ""}",
                                color = if (u.plan == plan) NeonMint else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedUserForPlanChange = null }) {
                    Text("CANCEL", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCardSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AdminUserCard(
    user: ManagedUserItem,
    onChangePlanClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCardSurface, RoundedCornerShape(12.dp))
            .border(1.dp, BorderStrokeColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1.3f)) {
            Text(
                text = "@${user.user}",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "CAPITAL: ${user.credit}",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "STATUS: ${user.status.uppercase()}",
                color = if (user.isActive) NeonEmerald else TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .background(
                        when (user.plan) {
                            "VIP" -> BrightGold.copy(alpha = 0.15f)
                            "ELITE" -> RoyalPurple.copy(alpha = 0.15f)
                            "PRO" -> NeonMint.copy(alpha = 0.15f)
                            else -> TextMuted.copy(alpha = 0.15f)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        0.8.dp,
                        when (user.plan) {
                            "VIP" -> BrightGold
                            "ELITE" -> RoyalPurple
                            "PRO" -> NeonMint
                            else -> BorderStrokeColor
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = user.plan,
                    color = when (user.plan) {
                        "VIP" -> BrightGold
                        "ELITE" -> RoyalPurple
                        "PRO" -> NeonMint
                        else -> TextMuted
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onChangePlanClick,
                modifier = Modifier
                    .height(30.dp)
                    .testTag("change_plan_btn_${user.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkCardElevated,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderStrokeColor)
            ) {
                Text(text = "CHANGE PLAN", fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
            }
        }
    }
}

