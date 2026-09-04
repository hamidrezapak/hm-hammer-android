package com.example.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChartRadarScreen(viewModel: MainViewModel) {
    val selectedPair by viewModel.selectedPair.collectAsState()
    var selectedInterval by remember { mutableStateOf("15") }

    val intervals = listOf(
        "1" to "1m",
        "5" to "5m",
        "15" to "15m",
        "60" to "1h",
        "240" to "4h",
        "D" to "1D"
    )

    // ایجاد HTML ویجت رسمی تریدینگ ویو دقیقا مانند وب‌اپ تلگرام
    val tradingViewHtml = remember(selectedPair, selectedInterval) {
        val cleanSymbol = if (selectedPair.contains("USDT")) "BINANCE:${selectedPair.uppercase()}" else "BINANCE:BTCUSDT"
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body, html { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #0d1117; overflow: hidden; }
                #tradingview_widget { width: 100%; height: 100%; }
            </style>
        </head>
        <body>
            <div id="tradingview_widget"></div>
            <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
            <script type="text/javascript">
                new TradingView.widget({
                    "autosize": true,
                    "symbol": "$cleanSymbol",
                    "interval": "$selectedInterval",
                    "timezone": "Asia/Tehran",
                    "theme": "dark",
                    "style": "1",
                    "locale": "en",
                    "toolbar_bg": "#0d1117",
                    "enable_publishing": false,
                    "hide_side_toolbar": false,
                    "allow_symbol_change": true,
                    "details": true,
                    "hotlist": false,
                    "calendar": false,
                    "container_id": "tradingview_widget"
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
    ) {
        // نوار انتخاب تایم‌فریم مشابه وب‌اپ ترید
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161B22))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            intervals.forEach { (intervalVal, label) ->
                val isSelected = selectedInterval == intervalVal
                Button(
                    onClick = { selectedInterval = intervalVal },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF238636) else Color(0xFF21262D)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(label, color = if (isSelected) Color.White else Color.LightGray, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // وب‌ویو رندرکننده تریدینگ ویو کامل
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://s3.tradingview.com", tradingViewHtml, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://s3.tradingview.com", tradingViewHtml, "text/html", "UTF-8", null)
            }
        )
    }
}
