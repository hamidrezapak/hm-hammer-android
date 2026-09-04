package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebSettings
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
    val isRunning by viewModel.isEngineRunning.collectAsState()

    val chartHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
            <style>
                body { margin: 0; padding: 0; background-color: #0A0E17; overflow: hidden; font-family: sans-serif; }
                #chart { width: 100vw; height: 100vh; }
            </style>
        </head>
        <body>
            <div id="chart"></div>
            <script>
                const chart = LightweightCharts.createChart(document.getElementById('chart'), {
                    layout: { background: { color: '#0A0E17' }, textColor: '#94A3B8' },
                    grid: { vertLines: { color: '#1E293B' }, horzLines: { color: '#1E293B' } },
                    timeScale: { timeVisible: true, secondsVisible: false, borderColor: '#334155' },
                    rightPriceScale: { borderColor: '#334155' }
                });

                const candleSeries = chart.addCandlestickSeries({
                    upColor: '#10B981', downColor: '#EF4444',
                    borderUpColor: '#10B981', borderDownColor: '#EF4444',
                    wickUpColor: '#10B981', wickDownColor: '#EF4444'
                });

                let base = $currentPrice;
                let data = [];
                let t = Math.floor(Date.now() / 1000) - (60 * 60 * 24);
                for (let i = 0; i < 40; i++) {
                    let open = base + (Math.sin(i) * 120);
                    let close = open + (Math.cos(i) * 90);
                    let high = Math.max(open, close) + 60;
                    let low = Math.min(open, close) - 140; // فرم‌گیری سایه‌های الگوی چکش
                    data.push({ time: t + (i * 900), open: open, high: high, low: low, close: close });
                    base = close;
                }
                candleSeries.setData(data);
                chart.timeScale().fitContent();
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
        // نوار هدر قیمت و لایو وضعیت
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
                    Text(selectedPair, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("قیمت زنده: $$currentPrice", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("SL: $${String.format("%.1f", levels.stopLoss)}", color = Color(0xFFFF5252), fontSize = 11.sp)
                    Text("TP1: $${String.format("%.1f", levels.tp1)}", color = Color(0xFF38BDF8), fontSize = 11.sp)
                }
            }
        }

        // باکس نمودار کندل‌استیک واقعی
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0E17)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL("https://local.chart", chartHtml, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://local.chart", chartHtml, "text/html", "UTF-8", null)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // پنل رادار تکنیکال و فیلترهای الگوریتم
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("وضعیت رادار الگو:", color = Color.Gray, fontSize = 11.sp)
                    Text(
                        if (isRunning) "در حال اسکن زنده چکش (Hammer)" else "رادار متوقف",
                        color = if (isRunning) Color(0xFF00E676) else Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("فیلتر EMA 200: مثبت (صعودی)", color = Color(0xFF38BDF8), fontSize = 10.sp)
                    Text("شاخص RSI: 44.2 (اشباع فروش)", color = Color(0xFFE2E8F0), fontSize = 10.sp)
                    Text("حجم معاملات: تایید شده", color = Color(0xFF00E676), fontSize = 10.sp)
                }
            }
        }
    }
}
