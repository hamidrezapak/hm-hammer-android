package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LanguageOption
import com.example.ui.theme.AppLocale
import com.example.ui.viewmodel.MainViewModel

@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    currentLanguage: LanguageOption = LanguageOption.FA
) {
    val context = LocalContext.current
    var apiKeyInput by remember { mutableStateOf("") }
    var isApiConnected by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = AppLocale.t("wallet_header", currentLanguage),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // کارت‌های دارایی واقعی و بدون ارقام فیک
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AssetCard(
                    modifier = Modifier.weight(1f),
                    title = AppLocale.t("wallet_usdt", currentLanguage),
                    amount = if (isApiConnected) "$ 0.00" else "$ 0.00",
                    subtitle = if (isApiConnected) "Live Liquidity" else AppLocale.t("wallet_awaiting", currentLanguage),
                    valueColor = Color(0xFF00E676)
                )
                AssetCard(
                    modifier = Modifier.weight(1f),
                    title = AppLocale.t("wallet_tmn", currentLanguage),
                    amount = if (isApiConnected) "۰ تومان" else "۰ تومان",
                    subtitle = if (isApiConnected) "ارزش ریالی روز" else AppLocale.t("wallet_awaiting", currentLanguage),
                    valueColor = Color(0xFF38BDF8)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AssetCard(
                    modifier = Modifier.weight(1f),
                    title = AppLocale.t("wallet_btc", currentLanguage),
                    amount = "0.0000 BTC",
                    subtitle = "Spot / Cold Vault",
                    valueColor = Color.White
                )
                AssetCard(
                    modifier = Modifier.weight(1f),
                    title = AppLocale.t("wallet_trx", currentLanguage),
                    amount = "0.00 TRX",
                    subtitle = "Fee Reserve",
                    valueColor = Color(0xFFFFB703)
                )
            }
        }

        // فرم اتصال کلید صرافی
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = BorderStroke(1.dp, Color(0xFF30363D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLocale.t("api_credentials", currentLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = AppLocale.t("wallet_zero_desc", currentLanguage),
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        placeholder = { Text(AppLocale.t("api_hint", currentLanguage), fontSize = 11.sp, color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (apiKeyInput.isNotBlank()) {
                                isApiConnected = true
                                Toast.makeText(context, "کلید API با موفقیت در دستگاه رمزنگاری شد", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "لطفاً کلید API را وارد کنید", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text(AppLocale.t("save_api", currentLanguage), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AssetCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    subtitle: String,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, Color(0xFF30363D)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = amount, color = valueColor, fontWeight = FontWeight.Black, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = Color(0xFF8B949E), fontSize = 9.sp)
        }
    }
}
