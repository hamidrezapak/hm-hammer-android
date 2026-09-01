package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.model.TradeOrder
import com.example.model.TradeStatus
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
import com.example.ui.viewmodel.HistorySortColumn
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionHistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allTrades by viewModel.trades.collectAsState()
    val searchQuery by viewModel.historySearchQuery.collectAsState()
    val symbolFilter by viewModel.historySymbolFilter.collectAsState()
    val sideFilter by viewModel.historySideFilter.collectAsState()
    val statusFilter by viewModel.historyStatusFilter.collectAsState()
    val sortColumn by viewModel.historySortColumn.collectAsState()
    val sortAscending by viewModel.historySortAscending.collectAsState()

    var selectedTradeForDetail by remember { mutableStateOf<TradeOrder?>(null) }

    // Filter and sort the trades
    val filteredTrades by remember(
        allTrades,
        searchQuery,
        symbolFilter,
        sideFilter,
        statusFilter,
        sortColumn,
        sortAscending
    ) {
        derivedStateOf {
            allTrades.filter { trade ->
                val matchesSearch = searchQuery.isBlank() ||
                        trade.symbol.contains(searchQuery.trim(), ignoreCase = true) ||
                        trade.side.contains(searchQuery.trim(), ignoreCase = true) ||
                        trade.closeReason.contains(searchQuery.trim(), ignoreCase = true)

                val matchesSymbol = symbolFilter == "ALL" || trade.symbol.equals(symbolFilter, ignoreCase = true)

                val matchesSide = when (sideFilter) {
                    "BUY" -> trade.side.equals("BUY", ignoreCase = true)
                    "SELL" -> trade.side.equals("SELL", ignoreCase = true)
                    else -> true
                }

                val matchesStatus = when (statusFilter) {
                    "PROFIT" -> trade.pnlPercent > 0
                    "LOSS" -> trade.pnlPercent < 0
                    "OPEN" -> trade.status == TradeStatus.OPEN || trade.status == TradeStatus.FILLED
                    "CLOSED" -> trade.status == TradeStatus.CLOSED || trade.status == TradeStatus.SL_HIT || trade.status == TradeStatus.TP3_HIT || trade.status == TradeStatus.TP4_HIT
                    else -> true
                }

                matchesSearch && matchesSymbol && matchesSide && matchesStatus
            }.let { list ->
                val comparator: Comparator<TradeOrder> = when (sortColumn) {
                    HistorySortColumn.ENTRY_TIME -> compareBy { it.entryTimestamp }
                    HistorySortColumn.EXIT_TIME -> compareBy { it.exitTimestamp }
                    HistorySortColumn.PNL_PERCENT -> compareBy { it.pnlPercent }
                    HistorySortColumn.PROFIT_USDT -> compareBy { it.profitUsdt }
                    HistorySortColumn.SYMBOL -> compareBy { it.symbol }
                    HistorySortColumn.ENTRY_PRICE -> compareBy { it.entryPrice }
                }
                if (sortAscending) list.sortedWith(comparator) else list.sortedWith(comparator.reversed())
            }
        }
    }

    val totalFiltered = filteredTrades.size
    val netPnlUsdt = filteredTrades.sumOf { it.profitUsdt }
    val winCount = filteredTrades.count { it.pnlPercent > 0 }
    val winRate = if (totalFiltered > 0) (winCount.toDouble() / totalFiltered) * 100.0 else 0.0

    val symbols = listOf("ALL", "BTC/USDT", "ETH/USDT", "SOL/USDT", "XRP/USDT", "TRX/USDT", "DOGE/USDT", "ADA/USDT")
    val sideOptions = listOf("ALL" to "All Sides", "BUY" to "LONG (BUY)", "SELL" to "SHORT (SELL)")
    val statusOptions = listOf(
        "ALL" to "All Status",
        "PROFIT" to "Profits (+)",
        "LOSS" to "Losses (-)",
        "OPEN" to "Active (Open)",
        "CLOSED" to "Closed"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 12.dp)
            .testTag("transaction_history_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Summary Performance KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStatCard(
                    title = "FILTERED PNL",
                    value = "${if (netPnlUsdt >= 0) "+" else ""}$${String.format("%.2f", netPnlUsdt)}",
                    valueColor = if (netPnlUsdt >= 0) NeonEmerald else StarShortRed,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "WIN RATE",
                    value = "${String.format("%.1f", winRate)}%",
                    valueColor = NeonMint,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "MATCHED TRADES",
                    value = "$totalFiltered / ${allTrades.size}",
                    valueColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search & Quick Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setHistorySearchQuery(it) },
                    placeholder = { Text("Search by pair (e.g. BTC, SOL)...", color = TextMuted, fontSize = 11.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("history_search_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMint,
                        unfocusedBorderColor = BorderStrokeColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkCardSurface,
                        unfocusedContainerColor = DarkCardSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = { viewModel.seedSampleHistory() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkCardElevated,
                        contentColor = NeonMint
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("seed_history_button")
                ) {
                    Text("+ Sample", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Symbol Filter Chips Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "FILTER BY ASSET PAIR",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(symbols) { sym ->
                        val isSelected = sym == symbolFilter
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
                                .clickable { viewModel.setHistorySymbolFilter(sym) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("filter_symbol_$sym")
                        ) {
                            Text(
                                text = sym,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Side & Outcome Filter Chips Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Side filter
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SIDE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        sideOptions.forEach { (key, label) ->
                            val isSelected = key == sideFilter
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) DarkCardElevated else DarkCardSurface,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonMint else BorderStrokeColor,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setHistorySideFilter(key) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when(key) { "BUY" -> "LONG"; "SELL" -> "SHORT"; else -> "ALL" },
                                    color = if (isSelected) NeonMint else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Status filter
                Column(modifier = Modifier.weight(1.5f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "OUTCOME / STATUS",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        statusOptions.forEach { (key, label) ->
                            val isSelected = key == statusFilter
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) DarkCardElevated else DarkCardSurface,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonMint else BorderStrokeColor,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setHistoryStatusFilter(key) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when(key) {
                                        "PROFIT" -> "WIN +"
                                        "LOSS" -> "LOSS -"
                                        "OPEN" -> "ACTIVE"
                                        "CLOSED" -> "DONE"
                                        else -> "ALL"
                                    },
                                    color = if (isSelected) NeonMint else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Table Interactive Header with Column Sorting
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderStrokeColor, RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTION LEDGER (TAP ROW TO INSPECT)",
                        color = BrightGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Sort: ${sortColumn.name} ${if (sortAscending) "↑" else "↓"}",
                        color = NeonMint,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Symbol column
                    SortableHeaderItem(
                        title = "PAIR / SIDE",
                        column = HistorySortColumn.SYMBOL,
                        currentSort = sortColumn,
                        isAscending = sortAscending,
                        onSort = { viewModel.setHistorySortColumn(HistorySortColumn.SYMBOL) },
                        modifier = Modifier.weight(1.3f)
                    )

                    // Entry & Exit column
                    SortableHeaderItem(
                        title = "ENTRY / EXIT",
                        column = HistorySortColumn.ENTRY_PRICE,
                        currentSort = sortColumn,
                        isAscending = sortAscending,
                        onSort = { viewModel.setHistorySortColumn(HistorySortColumn.ENTRY_PRICE) },
                        modifier = Modifier.weight(1.2f)
                    )

                    // PnL & Profit column
                    SortableHeaderItem(
                        title = "PNL / USDT",
                        column = HistorySortColumn.PNL_PERCENT,
                        currentSort = sortColumn,
                        isAscending = sortAscending,
                        onSort = { viewModel.setHistorySortColumn(HistorySortColumn.PNL_PERCENT) },
                        modifier = Modifier.weight(1.2f)
                    )

                    // Timestamp / Status column
                    SortableHeaderItem(
                        title = "TIME / STATUS",
                        column = HistorySortColumn.ENTRY_TIME,
                        currentSort = sortColumn,
                        isAscending = sortAscending,
                        onSort = { viewModel.setHistorySortColumn(HistorySortColumn.ENTRY_TIME) },
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }
        }

        // Table Rows
        if (filteredTrades.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCardSurface, RoundedCornerShape(14.dp))
                        .border(1.dp, BorderStrokeColor, RoundedCornerShape(14.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No transaction records match current filter criteria.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.setHistorySearchQuery("")
                                viewModel.setHistorySymbolFilter("ALL")
                                viewModel.setHistorySideFilter("ALL")
                                viewModel.setHistoryStatusFilter("ALL")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkCardElevated,
                                contentColor = NeonMint
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset All Filters", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredTrades, key = { it.id }) { trade ->
                TransactionHistoryRow(
                    trade = trade,
                    onClick = { selectedTradeForDetail = trade }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Trade Details Inspection Dialog Modal
    selectedTradeForDetail?.let { trade ->
        TradeDetailInspectionDialog(
            trade = trade,
            onDismiss = { selectedTradeForDetail = null },
            onManualClose = {
                viewModel.closeTradeManually(trade.id)
                selectedTradeForDetail = null
            }
        )
    }
}

@Composable
fun SortableHeaderItem(
    title: String,
    column: HistorySortColumn,
    currentSort: HistorySortColumn,
    isAscending: Boolean,
    onSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = currentSort == column
    Row(
        modifier = modifier
            .clickable { onSort() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = if (isSelected) NeonMint else TextMuted,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
        if (isSelected) {
            Text(
                text = if (isAscending) " ▲" else " ▼",
                color = NeonMint,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun TransactionHistoryRow(
    trade: TradeOrder,
    onClick: () -> Unit
) {
    val isBuy = trade.side.equals("BUY", ignoreCase = true)
    val isProfit = trade.pnlPercent >= 0
    val timeFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val entryTimeStr = timeFormat.format(Date(trade.entryTimestamp))
    val exitTimeStr = if (trade.exitTimestamp > 0) timeFormat.format(Date(trade.exitTimestamp)) else "Live"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCardSurface, RoundedCornerShape(12.dp))
            .border(1.dp, BorderStrokeColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("trade_history_row_${trade.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Column 1: Symbol & Side
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

        // Column 2: Entry Price & Exit Price
        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                text = "In: $${formatPrice(trade.entryPrice)}",
                color = TextPrimary,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Out: $${formatPrice(if (trade.exitPrice > 0) trade.exitPrice else trade.currentPrice)}",
                color = if (trade.exitTimestamp > 0) NeonCyan else TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Column 3: PnL % and Currency
        Column(modifier = Modifier.weight(1.2f)) {
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
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        // Column 4: Timestamp & Status badge
        Column(
            modifier = Modifier.weight(1.1f),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .background(
                        when (trade.status) {
                            TradeStatus.TP4_HIT -> NeonEmerald.copy(alpha = 0.25f)
                            TradeStatus.TP3_HIT -> NeonEmerald.copy(alpha = 0.2f)
                            TradeStatus.TP2_HIT -> NeonEmerald.copy(alpha = 0.15f)
                            TradeStatus.TP1_HIT -> NeonMint.copy(alpha = 0.15f)
                            TradeStatus.SL_HIT -> StarShortRed.copy(alpha = 0.15f)
                            TradeStatus.CLOSED -> Color(0x33888888)
                            else -> Color(0x22FFAA00)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (trade.status) {
                        TradeStatus.TP4_HIT -> "🎯 TP4"
                        TradeStatus.TP3_HIT -> "🎯 TP3"
                        TradeStatus.TP2_HIT -> "🎯 TP2"
                        TradeStatus.TP1_HIT -> "🎯 TP1"
                        TradeStatus.SL_HIT -> "⛔ SL"
                        TradeStatus.OPEN -> "● LIVE"
                        TradeStatus.FILLED -> "FILLED"
                        TradeStatus.CLOSED -> "CLOSED"
                    },
                    color = when (trade.status) {
                        TradeStatus.TP4_HIT, TradeStatus.TP3_HIT, TradeStatus.TP2_HIT -> NeonEmerald
                        TradeStatus.TP1_HIT -> NeonMint
                        TradeStatus.SL_HIT -> StarShortRed
                        TradeStatus.CLOSED -> TextSecondary
                        else -> Color(0xFFFFAA00)
                    },
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = entryTimeStr,
                color = TextMuted,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun TradeDetailInspectionDialog(
    trade: TradeOrder,
    onDismiss: () -> Unit,
    onManualClose: () -> Unit
) {
    val isBuy = trade.side.equals("BUY", ignoreCase = true)
    val isProfit = trade.pnlPercent >= 0
    val fullDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val entryTimeFull = fullDateFormat.format(Date(trade.entryTimestamp))
    val exitTimeFull = if (trade.exitTimestamp > 0) fullDateFormat.format(Date(trade.exitTimestamp)) else "Active (Position Open)"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("trade_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonMint.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = trade.symbol,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isBuy) NeonEmerald.copy(alpha = 0.2f) else StarShortRed.copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isBuy) "LONG (BUY)" else "SHORT (SELL)",
                                    color = if (isBuy) NeonEmerald else StarShortRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = "  ${trade.leverage} • ID #${trade.id}",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isProfit) NeonEmerald.copy(alpha = 0.2f) else StarShortRed.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${if (isProfit) "+" else ""}${String.format("%.2f", trade.pnlPercent)}%",
                            color = if (isProfit) NeonEmerald else StarShortRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                HorizontalDivider(color = BorderStrokeColor)

                // Price Specs
                Text(
                    text = "EXECUTION & TIMESTAMPS",
                    color = BrightGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                DetailRow(label = "Entry Timestamp", value = entryTimeFull)
                DetailRow(label = "Exit Timestamp", value = exitTimeFull)
                DetailRow(label = "Entry Price", value = "$${formatPrice(trade.entryPrice)}")
                DetailRow(
                    label = if (trade.exitTimestamp > 0) "Exit Price" else "Current Price",
                    value = "$${formatPrice(if (trade.exitPrice > 0) trade.exitPrice else trade.currentPrice)}"
                )
                DetailRow(label = "Capital Invested", value = "${String.format("%,.0f", trade.amountTmn)} TMN")
                DetailRow(
                    label = "Net Profit/Loss",
                    value = "${if (trade.profitUsdt >= 0) "+" else ""}$${String.format("%.2f", trade.profitUsdt)} USDT",
                    valueColor = if (trade.profitUsdt >= 0) NeonEmerald else StarShortRed
                )
                DetailRow(label = "Execution State", value = trade.closeReason)

                HorizontalDivider(color = BorderStrokeColor)

                // Dynamic Risk Levels
                Text(
                    text = "DYNAMIC RISK TARGETS (SL & TP)",
                    color = BrightGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RiskBadge(title = "STOP LOSS", price = trade.stopLoss, color = StarShortRed, modifier = Modifier.weight(1f))
                    RiskBadge(title = "TP1", price = trade.tp1, color = NeonMint, modifier = Modifier.weight(1f))
                    RiskBadge(title = "TP2", price = trade.tp2, color = NeonMint, modifier = Modifier.weight(1f))
                    RiskBadge(title = "TP3", price = trade.tp3, color = NeonEmerald, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }

                    if (trade.status == TradeStatus.OPEN || trade.status == TradeStatus.FILLED || trade.status == TradeStatus.TP1_HIT || trade.status == TradeStatus.TP2_HIT) {
                        Button(
                            onClick = onManualClose,
                            modifier = Modifier.weight(1.3f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StarShortRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Exit Market Position", fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Text(
            text = value,
            color = valueColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun RiskBadge(
    title: String,
    price: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DarkNavyBg, RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = color, fontSize = 8.sp, fontWeight = FontWeight.Black)
        Text(
            text = "$${formatPrice(price)}",
            color = TextPrimary,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 2.dp)
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
