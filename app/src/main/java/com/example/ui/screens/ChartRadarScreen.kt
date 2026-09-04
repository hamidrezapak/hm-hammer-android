package com.example.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkNavyBg
import com.example.ui.viewmodel.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChartRadarScreen(viewModel: MainViewModel) {
    val selectedPair by viewModel.selectedPair.collectAsState()

    val tradingViewHtml = remember(selectedPair) {
        val cleanSymbol = if (selectedPair.contains("USDT")) "BINANCE:${selectedPair.uppercase()}" else "BINANCE:BTCUSDT"
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body, html { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #0d1117; overflow: hidden; }
                #tv_chart_container { width: 100%; height: 100%; }
            </style>
        </head>
        <body>
            <div id="tv_chart_container"></div>
            <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
            <script type="text/javascript">
                new TradingView.widget({
                    "autosize": true,
                    "symbol": "$cleanSymbol",
                    "interval": "15",
                    "timezone": "Asia/Tehran",
                    "theme": "dark",
                    "style": "1",
                    "locale": "en",
                    "toolbar_bg": "#0d1117",
                    "enable_publishing": false,
                    "hide_top_toolbar": false,
                    "withdateranges": true,
                    "hide_side_toolbar": false,
                    "allow_symbol_change": true,
                    "save_image": false,
                    "container_id": "tv_chart_container"
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg),
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
