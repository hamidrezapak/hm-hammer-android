package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isRunning by viewModel.isEngineRunning.collectAsState()
    val isApiConnected by viewModel.isApiConnected.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E14))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("پنل مدیریت موتور معاملاتی", color = Color(0xFF00E5FF), fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("اتصال API صرافی", color = Color.LightGray, fontSize = 13.sp)
                    Text(
                        if (isApiConnected) "متصل" else "قطع",
                        color = if (isApiConnected) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("وضعیت موتور معاملات خودکار", color = Color.LightGray, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isRunning) Color(0xFF00E676) else Color(0xFFFF9800),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isRunning) "در حال اجرا" else "متوقف",
                            color = if (isRunning) Color(0xFF00E676) else Color(0xFFFF9800),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("روشن/خاموش کردن موتور خودکار", color = Color.White, fontSize = 13.sp)
                Switch(
                    checked = isRunning,
                    onCheckedChange = { viewModel.toggleAutoEngine(context) }
                )
            }
        }

        Button(
            onClick = {
                viewModel.stopRealEngine(context)
                Toast.makeText(context, "قطع اضطراری اجرا شد — موتور معاملات واقعاً متوقف شد", Toast.LENGTH_LONG).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("⛔ قطع اضطراری فوری معاملات (Kill-Switch)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Text(
            text = "این دکمه بلافاصله سرویس معاملاتی پس‌زمینه را متوقف می‌کند، مستقل از وضعیت فعلی سوییچ بالا. برای شروع دوباره باید از تب معامله اقدام کنید.",
            color = Color.Gray,
            fontSize = 11.sp
        )
    }
}
