package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChartRadarScreen(viewModel: MainViewModel) {
    val selectedPair by viewModel.selectedPair.collectAsState()
    val currentPrice by viewModel.currentPrice.collectAsState()
    val levels by viewModel.dynamicLevels.collectAsState()

    val symbolClean = if (selectedPair.contains("USDT")) {
        "BINANCE:${selectedPair.replace("/", "")}"
    } else {
        "BINANCE:BTCUSDT"
    }

    val widgetHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body, html { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #0D1117; overflow: hidden; }
                #tv_chart_container { width: 100%; height: 100%; }
            </style>
        </head>
        <body>
            <div id="tv_chart_container"></div>
            <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
            <script type="text/javascript">
                new TradingView.widget({
                    "autosize": true,
                    "symbol": "$symbolClean",
                    "interval": "15",
                    "timezone": "Asia/Tehran",
                    "theme": "dark",
                    "style": "1",
                    "locale": "fa_IR",
                    "toolbar_bg": "#0D1117",
                    "enable_publishing": false,
                    "hide_side_toolbar": false,
                    "allow_symbol_change": true,
                    "container_id": "tv_chart_container"
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(selectedPair, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("قیمت زنده: $$currentPrice", color = Color(0xFF00E676), fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("حد ضرر (SL): $${String.format("%.2f", levels.stopLoss)}", color = Color(0xFFFF5252), fontSize = 10.sp)
                    Text("تارگت اول (TP1): $${String.format("%.2f", levels.tp1)}", color = Color(0xFF38BDF8), fontSize = 10.sp)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL("https://tradingview.com", widgetHtml, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://tradingview.com", widgetHtml, "text/html", "UTF-8", null)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
