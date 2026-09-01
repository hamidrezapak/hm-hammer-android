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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
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
import com.example.engine.PairTicker
import com.example.model.SignalAction
import com.example.ui.components.InteractiveCandleChart
import com.example.ui.theme.BorderStrokeColor
import com.example.ui.theme.BrightGold
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
fun ChartRadarScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedPair by viewModel.selectedPair.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val tickers by viewModel.tickers.collectAsState()
    val candles = viewModel.getCandlesForSelectedPair()

    val timeframes = listOf("15M", "1H", "4H", "1D")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 12.dp)
            .testTag("chart_radar_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Anti-Fragile PNL Section (Bold Typography Theme)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "ANTI-FRAGILE PNL",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "+12.8",
                        color = TextPrimary,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp,
                        lineHeight = 46.sp
                    )
                    Text(
                        text = "%",
                        color = NeonMint,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                    )
                }
                Text(
                    text = "Net Profit • Last 24 Hours",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Two-Column Trend & Volatility Highlights
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // BTC Trend
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(DarkCardSurface, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderStrokeColor, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "BTC TREND",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(NeonEmerald.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "Bullish",
                                tint = NeonEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "BULLISH",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // ATR Volatility
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(DarkCardSurface, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderStrokeColor, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ATR VOLATILITY",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "5.42",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "x",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                }
            }
        }

        // Timeframe Selector & Chart Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TIMEFRAME",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    timeframes.forEach { tf ->
                        val isSelected = tf.equals(selectedTimeframe, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NeonMint else DarkCardSurface,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonMint else BorderStrokeColor,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setSelectedTimeframe(tf.lowercase()) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("timeframe_$tf"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tf,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Interactive Candlestick Chart
        item {
            InteractiveCandleChart(
                symbol = selectedPair,
                candles = candles,
                timeframe = selectedTimeframe
            )
        }

        // Quick Pair Selector Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE PULSE SCANNER",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "15 PAIRS SCANNING",
                        color = NeonMint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tickers) { ticker ->
                        val isSelected = ticker.symbol == selectedPair
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) DarkCardElevated else DarkCardSurface,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.2.dp,
                                    if (isSelected) NeonMint else BorderStrokeColor,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setSelectedPair(ticker.symbol) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("ticker_chip_${ticker.symbol.replace('/', '_')}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = ticker.symbol,
                                    color = if (isSelected) NeonMint else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "$${String.format("%.2f", ticker.price)}",
                                    color = if (ticker.change24h >= 0) HammerLongGreen else StarShortRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Detailed Radar Scanner Pair Cards (Bold Typography Items)
        item {
            Text(
                text = "ANTI-FRAGILE SIGNAL RADAR",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(tickers) { ticker ->
            RadarPairDetailCard(
                ticker = ticker,
                isSelected = ticker.symbol == selectedPair,
                onSelect = { viewModel.setSelectedPair(ticker.symbol) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun RadarPairDetailCard(
    ticker: PairTicker,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val isBullish = ticker.change24h >= 0
    val accentBarColor = if (ticker.lastSignal != null) {
        if (ticker.lastSignal.action == SignalAction.BUY) HammerLongGreen else StarShortRed
    } else if (isBullish) {
        HammerLongGreen
    } else {
        StarShortRed
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) DarkCardElevated else DarkCardSurface, RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isSelected) NeonMint else BorderStrokeColor,
                RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Vertical Accent Pill + Symbol & Signal Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bold Vertical Indicator Bar
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(34.dp)
                        .background(accentBarColor, RoundedCornerShape(2.dp))
                )

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ticker.symbol,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.2).sp
                        )
                        if (ticker.isVolumeSpike) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(BrightGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .border(0.8.dp, BrightGold, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "VOL SPIKE",
                                    color = BrightGold,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = if (ticker.lastSignal != null) {
                            if (ticker.lastSignal.action == SignalAction.BUY) "HAMMER SIGNAL" else "SHOOTING STAR"
                        } else {
                            "RADAR ACTIVE"
                        },
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Right: Price & Change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", ticker.price)}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${if (isBullish) "+" else ""}${String.format("%.2f", ticker.change24h)}%",
                    color = if (isBullish) NeonEmerald else StarShortRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Metrics row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "RSI: ${String.format("%.1f", ticker.rsi)}",
                color = if (ticker.rsi <= 40) HammerLongGreen else if (ticker.rsi >= 60) StarShortRed else TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SPREAD: ${String.format("%.4f", ticker.spread)}$",
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "ATR: ${String.format("%.4f", ticker.atr)}",
                color = NeonMint,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

