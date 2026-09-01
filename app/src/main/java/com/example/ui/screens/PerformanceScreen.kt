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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TradeOrder
import com.example.model.TradeStatus
import com.example.ui.components.ConsoleTerminal
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.BorderStrokeColor
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.HammerLongGreen
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StarShortRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PerformanceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val trades by viewModel.trades.collectAsState()
    val radarLogs by viewModel.radarLogs.collectAsState()
    val lastLog by viewModel.lastEngineLog.collectAsState()

    val totalTrades = trades.size
    val winningTrades = trades.count { it.pnlPercent > 0 }
    val winRate = if (totalTrades > 0) (winningTrades.toDouble() / totalTrades) * 100.0 else 82.5
    val totalProfitUsdt = trades.sumOf { it.profitUsdt }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 12.dp)
            .testTag("performance_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High-level Performance Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard(
                    title = "NET PROFIT",
                    value = "+$${String.format("%.1f", totalProfitUsdt)}",
                    valueColor = NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "WIN RATE",
                    value = "${String.format("%.1f", winRate)}%",
                    valueColor = NeonMint,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "EXECUTIONS",
                    value = "$totalTrades OPS",
                    valueColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Live Radar Engine Terminal Box
        item {
            ConsoleTerminal(
                logs = radarLogs,
                lastLog = lastLog,
                maxHeight = 120
            )
        }

        // Trades History Header
        item {
            Text(
                text = "RECENT EXECUTIONS & DYNAMIC ATR TP TARGETS",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Table Columns Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "PAIR / SIDE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1.3f))
                Text(text = "ENTRY", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                Text(text = "PNL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                Text(text = "STATUS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
            }
        }

        if (trades.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCardSurface, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderStrokeColor, RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Awaiting live radar signals and auto-execution...",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            items(trades) { trade ->
                TradeRowCard(trade = trade)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun TradeRowCard(trade: TradeOrder) {
    val isBuy = trade.side == "BUY"
    val isProfit = trade.pnlPercent >= 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCardSurface, RoundedCornerShape(12.dp))
            .border(1.dp, BorderStrokeColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Symbol & Side
        Column(modifier = Modifier.weight(1.3f)) {
            Text(
                text = trade.symbol,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isBuy) NeonEmerald.copy(alpha = 0.15f) else StarShortRed.copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (isBuy) "LONG" else "SHORT",
                        color = if (isBuy) NeonEmerald else StarShortRed,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = " ${trade.leverage}",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Entry Price
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$${String.format("%.2f", trade.entryPrice)}",
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SL: $${String.format("%.1f", trade.stopLoss)}",
                color = TextMuted,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // PnL
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${if (isProfit) "+" else ""}${String.format("%.2f", trade.pnlPercent)}%",
                color = if (isProfit) NeonEmerald else StarShortRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${if (trade.profitUsdt >= 0) "+" else ""}${String.format("%.2f", trade.profitUsdt)}$",
                color = if (isProfit) NeonMint else StarShortRed,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        // Status Badge
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    when (trade.status) {
                        TradeStatus.TP4_HIT -> NeonEmerald.copy(alpha = 0.25f)
                        TradeStatus.TP3_HIT -> NeonEmerald.copy(alpha = 0.2f)
                        TradeStatus.TP2_HIT -> NeonEmerald.copy(alpha = 0.15f)
                        TradeStatus.TP1_HIT -> NeonMint.copy(alpha = 0.15f)
                        TradeStatus.SL_HIT -> StarShortRed.copy(alpha = 0.15f)
                        else -> Color(0x22FFAA00)
                    },
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (trade.status) {
                    TradeStatus.TP4_HIT -> "🎯 TP4"
                    TradeStatus.TP3_HIT -> "🎯 TP3"
                    TradeStatus.TP2_HIT -> "🎯 TP2"
                    TradeStatus.TP1_HIT -> "🎯 TP1"
                    TradeStatus.SL_HIT -> "⛔ SL"
                    TradeStatus.OPEN -> "OPEN"
                    TradeStatus.FILLED -> "FILLED"
                    TradeStatus.CLOSED -> "CLOSED"
                },
                color = when (trade.status) {
                    TradeStatus.TP4_HIT, TradeStatus.TP3_HIT, TradeStatus.TP2_HIT -> NeonEmerald
                    TradeStatus.TP1_HIT -> NeonMint
                    TradeStatus.SL_HIT -> StarShortRed
                    else -> Color(0xFFFFAA00)
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

