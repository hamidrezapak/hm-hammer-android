package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun ChartRadarScreen(
    viewModel: MainViewModel? = null
) {
    val pairs = listOf(
        Pair("BTC/USDT", 64500.0),
        Pair("ETH/USDT", 3480.0),
        Pair("SOL/USDT", 152.0),
        Pair("BNB/USDT", 588.0),
        Pair("DOGE/USDT", 0.125)
    )
    var selectedPair by remember { mutableStateOf(pairs[0]) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // نوار انتخاب جفت‌ارزها
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(pairs) { item ->
                val isSelected = item.first == selectedPair.first
                Button(
                    onClick = {
                        selectedPair = item
                        webViewInstance?.evaluateJavascript(
                            "setPair('${item.first}', ${item.second});",
                            null
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF00E676) else Color(0xFF161B22)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E676) else Color(0xFF30363D)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        item.first,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // کارت قیمت زنده و وضعیت الگوریتم
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(selectedPair.first, color = Color.Gray, fontSize = 10.sp)
                    Text("$${selectedPair.second}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
                Surface(
                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "TRADINGVIEW CORE ⚡",
                        color = Color(0xFF00E676),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // محفظه وب چارت تریدینگ‌ویو (با زوم و لمس دو انگشتی فعال)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
            border = BorderStroke(1.dp, Color(0xFF21262D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = WebViewClient()
                        loadUrl("file:///android_asset/tradingview_chart.html")
                        webViewInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
