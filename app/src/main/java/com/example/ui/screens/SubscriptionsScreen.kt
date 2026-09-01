package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderStrokeColor
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.HammerLongGreen
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SubscriptionsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.userConfig.collectAsState()

    val plans = listOf(
        PlanItem(
            name = "VIP",
            fee = "0.0% FEE",
            feePct = 0.0,
            price = "INSTITUTIONAL / SENIOR",
            color = BrightGold,
            perks = listOf(
                "Priority #1 high-speed order batching dispatch",
                "Direct zero-slippage execution (Post-Only Maker)",
                "Real-time monitoring across 15 pairs with ATR volatility lock",
                "24/7 dedicated anti-fragile algorithmic engine support"
            )
        ),
        PlanItem(
            name = "ELITE",
            fee = "0.8% PROFIT",
            feePct = 0.8,
            price = "99 USDT / MONTH",
            color = RoyalPurple,
            perks = listOf(
                "Fast execution up to 50 orders per batch",
                "Triple dynamic ATR-based take profit calculations",
                "Live Hammer & Shooting Star real-time radar alarms",
                "Direct API access to Wallex, Nobitex, and KuCoin"
            )
        ),
        PlanItem(
            name = "PRO",
            fee = "1.5% PROFIT",
            feePct = 1.5,
            price = "49 USDT / MONTH",
            color = NeonMint,
            perks = listOf(
                "Automated trade execution with 2% risk lock",
                "Volume spike & RSI divergence dynamic filter",
                "Supported pairs: BTC, ETH, SOL, XRP, DOGE",
                "Risk-managed leverage up to 5x"
            )
        ),
        PlanItem(
            name = "STANDARD",
            fee = "2.5% PROFIT",
            feePct = 2.5,
            price = "FREE TIER",
            color = TextMuted,
            perks = listOf(
                "Public radar signal scanner",
                "Minimum order size: 50 USDT",
                "15m default timeframe analysis"
            )
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 12.dp)
            .testTag("subscriptions_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "SUBSCRIPTION TIERS & ANTI-FRAGILE ENGINE FEE STRUCTURE",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(plans.size) { index ->
            val plan = plans[index]
            val isCurrent = plan.name.equals(config.subscriptionPlan, ignoreCase = true)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(16.dp))
                    .border(
                        1.5.dp,
                        if (isCurrent) plan.color else BorderStrokeColor,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.name,
                            color = plan.color,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .background(plan.color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .border(0.8.dp, plan.color, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE PLAN",
                                    color = plan.color,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = plan.fee,
                        color = NeonEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                plan.perks.forEach { perk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✔",
                            color = plan.color,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = perk,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }

                if (!isCurrent) {
                    Button(
                        onClick = { viewModel.updateSubscriptionPlan(plan.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("upgrade_plan_${plan.name.lowercase()}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = plan.color,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "UPGRADE TO ${plan.name}",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

data class PlanItem(
    val name: String,
    val fee: String,
    val feePct: Double,
    val price: String,
    val color: Color,
    val perks: List<String>
)

