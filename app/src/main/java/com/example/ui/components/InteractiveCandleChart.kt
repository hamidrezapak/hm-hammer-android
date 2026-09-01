package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Candle
import com.example.ui.theme.BorderStrokeColor
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.HammerLongGreen
import com.example.ui.theme.NeonMint
import com.example.ui.theme.StarShortRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun InteractiveCandleChart(
    symbol: String,
    candles: List<Candle>,
    timeframe: String,
    modifier: Modifier = Modifier
) {
    val displayCandles = if (candles.size > 35) candles.takeLast(35) else candles
    val lastCandle = displayCandles.lastOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkCardSurface, RoundedCornerShape(16.dp))
            .border(1.dp, BorderStrokeColor, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("interactive_candle_chart")
    ) {
        // Chart Header Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = symbol,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.3).sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(NeonMint.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = timeframe.uppercase(),
                        color = NeonMint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            if (lastCandle != null) {
                val isBullish = lastCandle.close >= lastCandle.open
                Text(
                    text = "$${String.format("%.2f", lastCandle.close)}",
                    color = if (isBullish) HammerLongGreen else StarShortRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Indicator status chips
        if (lastCandle != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "EMA(200): $${String.format("%.2f", lastCandle.ema200 ?: lastCandle.close)}",
                    color = BrightGold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "RSI(14): ${String.format("%.1f", lastCandle.rsi ?: 50.0)}",
                    color = NeonMint,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "ATR: ${String.format("%.4f", lastCandle.atr ?: 0.0)}",
                    color = HammerLongGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Candlestick Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            if (displayCandles.isEmpty()) return@Canvas

            val minPrice = displayCandles.minOf { it.low } * 0.998
            val maxPrice = displayCandles.maxOf { it.high } * 1.002
            val priceRange = maxOf(maxPrice - minPrice, 0.0001)

            val maxVol = maxOf(displayCandles.maxOf { it.volume }, 1.0)

            val count = displayCandles.size
            val candleWidth = (size.width / count) * 0.7f
            val spacing = size.width / count

            val emaPoints = mutableListOf<Offset>()

            // Draw grid lines
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = (size.height / gridLines) * i
                drawLine(
                    color = Color(0x15FFFFFF),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 0.8f
                )
            }

            displayCandles.forEachIndexed { index, c ->
                val xCenter = (index * spacing) + (spacing / 2f)

                val yHigh = size.height - (((c.high - minPrice) / priceRange) * size.height).toFloat()
                val yLow = size.height - (((c.low - minPrice) / priceRange) * size.height).toFloat()
                val yOpen = size.height - (((c.open - minPrice) / priceRange) * size.height).toFloat()
                val yClose = size.height - (((c.close - minPrice) / priceRange) * size.height).toFloat()

                val isGreen = c.close >= c.open
                val candleColor = if (isGreen) HammerLongGreen else StarShortRed

                // Draw Wick
                drawLine(
                    color = candleColor,
                    start = Offset(xCenter, yHigh),
                    end = Offset(xCenter, yLow),
                    strokeWidth = 1.8f,
                    cap = StrokeCap.Round
                )

                // Draw Body
                val topY = min(yOpen, yClose)
                val bodyHeight = max(abs(yOpen - yClose), 2f)
                drawRect(
                    color = candleColor,
                    topLeft = Offset(xCenter - (candleWidth / 2f), topY),
                    size = Size(candleWidth, bodyHeight)
                )

                // Draw Volume Bar at base
                val volHeight = ((c.volume / maxVol) * (size.height * 0.22f)).toFloat()
                drawRect(
                    color = candleColor.copy(alpha = 0.35f),
                    topLeft = Offset(xCenter - (candleWidth / 2f), size.height - volHeight),
                    size = Size(candleWidth, volHeight)
                )

                // Collect EMA Points
                c.ema200?.let { emaVal ->
                    val yEma = size.height - (((emaVal - minPrice) / priceRange) * size.height).toFloat()
                    emaPoints.add(Offset(xCenter, yEma))
                }
            }

            // Draw EMA200 Overlay Line
            if (emaPoints.size > 1) {
                val emaPath = Path()
                emaPath.moveTo(emaPoints[0].x, emaPoints[0].y)
                for (i in 1 until emaPoints.size) {
                    emaPath.lineTo(emaPoints[i].x, emaPoints[i].y)
                }
                drawPath(
                    path = emaPath,
                    color = BrightGold.copy(alpha = 0.85f),
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )
            }
        }

        // Bottom Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "● EMA(200) TREND LINE",
                color = BrightGold,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "VOLUME FILTER ACTIVE",
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

