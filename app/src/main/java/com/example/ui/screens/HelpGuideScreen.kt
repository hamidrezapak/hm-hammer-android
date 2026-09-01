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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderStrokeColor
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.HammerLongGreen
import com.example.ui.theme.LaserCoral
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMint
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.StarShortRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HelpGuideScreen(
    modifier: Modifier = Modifier
) {
    val strategyRules = listOf(
        RuleItem(
            num = "01",
            title = "CANDLE GEOMETRY (HAMMER & SHOOTING STAR)",
            desc = "In Hammer: lower shadow >= 2.0x body, upper shadow <= 0.25x body. Shooting Star is inverse (rejection pinbar).",
            accent = NeonMint
        ),
        RuleItem(
            num = "02",
            title = "MACRO TREND FILTER (EMA 200)",
            desc = "Long positions require Price > EMA200; Short positions require Price < EMA200 for absolute trend alignment.",
            accent = BrightGold
        ),
        RuleItem(
            num = "03",
            title = "PARENT MARKET ALIGNMENT (BTC BULLISH)",
            desc = "Altcoin execution strictly synchronizes with Bitcoin macro trend direction to avoid bull/bear traps.",
            accent = NeonEmerald
        ),
        RuleItem(
            num = "04",
            title = "OSCILLATOR EXTREMES FILTER (RSI 14)",
            desc = "Long entries trigger below 40.0 (oversold bounce); Short entries trigger above 60.0 (overbought pullback).",
            accent = RoyalPurple
        ),
        RuleItem(
            num = "05",
            title = "VOLATILITY BALANCE SPREAD FILTER (ATR 5.0x)",
            desc = "ATR(14) must exceed 5.0x bid-ask spread to protect orders from low-liquidity dry market regimes.",
            accent = LaserCoral
        ),
        RuleItem(
            num = "06",
            title = "TRIPLE DYNAMIC TAKE PROFIT TARGETS (ATR BASED)",
            desc = "TP1: 1.0x ATR | TP2: 1.5x ATR | TP3: 2.0x ATR with trailing stop-loss protection anchored at trigger candle wick.",
            accent = NeonMint
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 12.dp)
            .testTag("help_guide_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "ANTI-FRAGILE ENGINE PROTOCOL & STRATEGY RULES (v2.0)",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(strategyRules.size) { index ->
            val rule = strategyRules[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderStrokeColor, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .background(rule.accent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, rule.accent, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = rule.num,
                        color = rule.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = rule.title,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = rule.desc,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // API Setup FAQ
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderStrokeColor, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "SECURE EXCHANGE INTEGRATION GUIDE",
                    color = BrightGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "1. Log into your Wallex or Nobitex exchange dashboard.\n2. Navigate to Profile > API Key Management.\n3. Create a read & trade only key (disable withdrawal permission).\n4. Save the key in the Wallet tab to begin automated execution.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

data class RuleItem(
    val num: String,
    val title: String,
    val desc: String,
    val accent: Color
)

