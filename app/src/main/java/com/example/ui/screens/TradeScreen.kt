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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.BorderStrokeColor
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.HammerLongGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StarShortRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import java.util.Locale

@Composable
fun TradeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.userConfig.collectAsState()
    val selectedPair by viewModel.selectedPair.collectAsState()
    val tickers by viewModel.tickers.collectAsState()
    val activeQueueCount by viewModel.executionManager.activeQueueCount.collectAsState()

    val currentTicker = tickers.find { it.symbol == selectedPair } ?: tickers.firstOrNull()
    val entryPrice = currentTicker?.price ?: 91420.0
    val currentAtr = currentTicker?.atr ?: (entryPrice * 0.015)

    // Calculate dynamic levels preview for BUY and SELL
    val previewLevels = viewModel.calculateDynamicLevels(
        symbol = selectedPair,
        side = "BUY",
        entryPrice = entryPrice,
        atr = currentAtr,
        config = config
    )

    var tradeAmountInput by remember(config.tradeAmountTmn) {
        mutableStateOf(config.tradeAmountTmn.toLong().toString())
    }

    val leverages = listOf("1x", "2x", "5x", "10x", "20x")
    val exchanges = listOf(
        "wallex" to "Wallex",
        "nobitex" to "Nobitex",
        "kucoin" to "KuCoin",
        "binance" to "Binance",
        "bingx" to "BingX"
    )

    val slAtrPresets = listOf(0.8, 1.0, 1.5, 2.0, 2.5, 3.0)
    val slPctPresets = listOf(0.5, 1.0, 1.5, 2.0, 3.0, 5.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 12.dp)
            .testTag("trade_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Auto-Trade Master Toggle Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (config.autoTrade) NeonMint else BorderStrokeColor,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AUTO-TRADING BOT & RISK ENGINE",
                            color = if (config.autoTrade) NeonMint else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (config.autoTrade) "Anti-Fragile Engine: Dynamic SL & TP targets active" else "Auto-trade execution currently paused",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Switch(
                        checked = config.autoTrade,
                        onCheckedChange = { viewModel.updateAutoTrade(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonMint,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkCardElevated
                        ),
                        modifier = Modifier.testTag("auto_trade_switch")
                    )
                }
            }
        }

        // Quick Stats Row (Batch queue & Safeguards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard(
                    title = "BATCH QUEUE",
                    value = "$activeQueueCount OPS",
                    valueColor = NeonMint,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "SL MODE",
                    value = if (config.slMode == "ATR") "${config.slAtrMultiplier}x ATR" else "${config.slPercentage}% FIX",
                    valueColor = BrightGold,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "R:R RATIO",
                    value = "1:${String.format(Locale.US, "%.1f", previewLevels.riskRewardRatio)}",
                    valueColor = NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ADVANCED RISK MANAGEMENT SECTION
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, NeonMint.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("advanced_risk_management_card"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ADVANCED RISK MANAGEMENT",
                        color = NeonMint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "DYNAMIC LEVELS",
                        color = BrightGold,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                // 1. Dynamic Stop-Loss Settings
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "1. DYNAMIC STOP-LOSS (SL) CONFIGURATION",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )

                    // Mode switch: ATR vs Fixed Percentage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ATR" to "ATR Multiple", "PERCENT" to "Fixed Percentage (%)").forEach { (mode, label) ->
                            val isSelected = config.slMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) NeonMint else DarkNavyBg,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonMint else BorderStrokeColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateSlMode(mode) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Value selectors
                    if (config.slMode == "ATR") {
                        Text(
                            text = "Selected ATR Multiplier: ${config.slAtrMultiplier}x (ATR: $${formatPrice(currentAtr)})",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            slAtrPresets.forEach { preset ->
                                val isSelected = config.slAtrMultiplier == preset
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) DarkCardElevated else DarkNavyBg,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonMint else BorderStrokeColor,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { viewModel.updateSlAtrMultiplier(preset) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${preset}x",
                                        color = if (isSelected) NeonMint else TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Selected Fixed Stop-Loss: ${config.slPercentage}% below/above entry",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            slPctPresets.forEach { preset ->
                                val isSelected = config.slPercentage == preset
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) DarkCardElevated else DarkNavyBg,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) StarShortRed else BorderStrokeColor,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { viewModel.updateSlPercentage(preset) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${preset}%",
                                        color = if (isSelected) StarShortRed else TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    // Trailing Stop to Breakeven
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkNavyBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TRAILING BREAKEVEN LOCK",
                                color = NeonEmerald,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Auto-move Stop Loss to entry price when TP1 is hit",
                                color = TextMuted,
                                fontSize = 8.5.sp
                            )
                        }
                        Switch(
                            checked = config.trailingStopBreakeven,
                            onCheckedChange = { viewModel.updateTrailingStopBreakeven(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = NeonEmerald,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkCardElevated
                            )
                        )
                    }
                }

                HorizontalDivider(color = BorderStrokeColor)

                // 2. Dynamic Take-Profit (TP) Settings
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "2. DYNAMIC TAKE-PROFIT (TP) TARGETS",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )

                    // TP Mode switch: ATR vs Fixed Percentage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ATR" to "ATR Multiple", "PERCENT" to "Fixed Percentage (%)").forEach { (mode, label) ->
                            val isSelected = config.tpMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) NeonEmerald else DarkNavyBg,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonEmerald else BorderStrokeColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateTpMode(mode) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Number of active TP targets
                    Text(
                        text = "Active Target Count: ${config.tpTargetCount} Targets",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 2, 3, 4).forEach { count ->
                            val isSelected = config.tpTargetCount == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) DarkCardElevated else DarkNavyBg,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonMint else BorderStrokeColor,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.updateTpTargetCount(count) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count Target${if (count > 1) "s" else ""}",
                                    color = if (isSelected) NeonMint else TextPrimary,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // TP Value Configuration Targets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TargetLevelBox(
                            title = "TP1",
                            value = if (config.tpMode == "ATR") "${config.tp1Value}x" else "${config.tp1Value}%",
                            isActive = config.tpTargetCount >= 1,
                            modifier = Modifier.weight(1f)
                        )
                        TargetLevelBox(
                            title = "TP2",
                            value = if (config.tpMode == "ATR") "${config.tp2Value}x" else "${config.tp2Value}%",
                            isActive = config.tpTargetCount >= 2,
                            modifier = Modifier.weight(1f)
                        )
                        TargetLevelBox(
                            title = "TP3",
                            value = if (config.tpMode == "ATR") "${config.tp3Value}x" else "${config.tp3Value}%",
                            isActive = config.tpTargetCount >= 3,
                            modifier = Modifier.weight(1f)
                        )
                        TargetLevelBox(
                            title = "TP4",
                            value = if (config.tpMode == "ATR") "${config.tp4Value}x" else "${config.tp4Value}%",
                            isActive = config.tpTargetCount >= 4,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(color = BorderStrokeColor)

                // 3. Live Simulation Preview Calculator Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkNavyBg, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SIMULATED LEVELS (${selectedPair})",
                            color = BrightGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Live Price: $${formatPrice(entryPrice)}",
                            color = TextPrimary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SimLevelBadge(title = "STOP LOSS", price = previewLevels.stopLoss, color = StarShortRed, modifier = Modifier.weight(1f))
                        SimLevelBadge(title = "TP1", price = previewLevels.tp1, color = NeonMint, modifier = Modifier.weight(1f))
                        if (config.tpTargetCount >= 2) {
                            SimLevelBadge(title = "TP2", price = previewLevels.tp2, color = NeonMint, modifier = Modifier.weight(1f))
                        }
                        if (config.tpTargetCount >= 3) {
                            SimLevelBadge(title = "TP3", price = previewLevels.tp3, color = NeonEmerald, modifier = Modifier.weight(1f))
                        }
                        if (config.tpTargetCount >= 4) {
                            SimLevelBadge(title = "TP4", price = previewLevels.tp4, color = NeonEmerald, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Trading Settings Form Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderStrokeColor, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "CAPITAL & EXCHANGE SETTINGS",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                // Trade Amount Input Field
                Column {
                    Text(
                        text = "CAPITAL PER TRADE (TOMAN)",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = tradeAmountInput,
                        onValueChange = {
                            tradeAmountInput = it
                            it.toDoubleOrNull()?.let { amt -> viewModel.updateTradeAmount(amt) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trade_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMint,
                            unfocusedBorderColor = BorderStrokeColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkNavyBg,
                            unfocusedContainerColor = DarkNavyBg
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Leverage Selector
                Column {
                    Text(
                        text = "LEVERAGE MULTIPLIER",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        leverages.forEach { lev ->
                            val isSelected = lev == config.leverage
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) NeonMint else DarkNavyBg,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonMint else BorderStrokeColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateLeverage(lev) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lev,
                                    color = if (isSelected) Color.Black else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Exchange Selector
                Column {
                    Text(
                        text = "DESTINATION EXCHANGE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        exchanges.take(3).forEach { (key, label) ->
                            val isSelected = key == config.exchangeName
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) DarkCardElevated else DarkNavyBg,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonMint else BorderStrokeColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateExchange(key) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    color = if (isSelected) NeonMint else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Manual Instant Order Execution
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderStrokeColor, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "MANUAL RISK-MANAGED DISPATCH (${selectedPair})",
                    color = BrightGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                if (currentTicker != null) {
                    val entry = currentTicker.price
                    val atr = currentTicker.atr

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PRICE: $${formatPrice(entry)}",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "ATR(14): ${String.format(Locale.US, "%.4f", atr)}",
                            color = NeonMint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.executeManualOrder(selectedPair, "BUY") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("manual_buy_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HammerLongGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "LONG (BUY)",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.executeManualOrder(selectedPair, "SELL") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("manual_sell_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StarShortRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "SHORT (SELL)",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun TargetLevelBox(
    title: String,
    value: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(if (isActive) DarkNavyBg else DarkNavyBg.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .border(
                1.dp,
                if (isActive) NeonEmerald.copy(alpha = 0.6f) else BorderStrokeColor.copy(alpha = 0.3f),
                RoundedCornerShape(6.dp)
            )
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = if (isActive) NeonEmerald else TextMuted,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = value,
                color = if (isActive) TextPrimary else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun SimLevelBadge(
    title: String,
    price: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DarkCardSurface, RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = color, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
        Text(
            text = "$${formatPrice(price)}",
            color = TextPrimary,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

private fun formatPrice(price: Double): String {
    return if (price >= 1000) {
        String.format(Locale.US, "%,.1f", price)
    } else if (price >= 1) {
        String.format(Locale.US, "%.3f", price)
    } else {
        String.format(Locale.US, "%.4f", price)
    }
}


