package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.PinLockStore
import com.example.ui.theme.DarkNavyBg

@Composable
fun PinEntryScreen(onUnlocked: () -> Unit) {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("HM HAMMER PRO", color = Color(0xFF38BDF8), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("برای ورود، کد PIN را وارد کنید", color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = pinInput,
            onValueChange = {
                if (it.length <= 8) {
                    pinInput = it
                    errorMessage = null
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF30363D)
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage!!, color = Color(0xFFFF5252), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (PinLockStore.verifyPin(pinInput)) {
                    onUnlocked()
                } else {
                    errorMessage = "کد PIN اشتباه است"
                    pinInput = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.7f).height(48.dp)
        ) {
            Text("ورود", fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
