package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class LanguageOption(val code: String, val title: String) {
    FA("fa", "فارسی"),
    EN("en", "English"),
    AR("ar", "العربية")
}

@Composable
fun HeaderPulseBar(
    isPulseAlive: Boolean = true,
    currentLanguage: LanguageOption = LanguageOption.FA,
    onLanguageSelected: (LanguageOption) -> Unit = {}
) {
    var langMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // منوی کشویی زبان به جای دکمه دایره‌ای ادمین
        Box {
            Surface(
                color = Color(0xFF21262D),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { langMenuExpanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentLanguage.title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = langMenuExpanded,
                onDismissRequest = { langMenuExpanded = false },
                modifier = Modifier.background(Color(0xFF161B22))
            ) {
                LanguageOption.values().forEach { lang ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = lang.title,
                                color = if (currentLanguage == lang) Color(0xFF00E676) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        },
                        onClick = {
                            onLanguageSelected(lang)
                            langMenuExpanded = false
                        }
                    )
                }
            }
        }

        // بخش برند و وضعیت موتور معاملاتی
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "v2.0 ",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "HM HAMMER",
                    color = Color(0xFF00E676),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (currentLanguage == LanguageOption.FA) "موتور معاملاتی فعال " else if (currentLanguage == LanguageOption.AR) "محرك التداول نشط " else "ENGINE ACTIVE ",
                    color = Color(0xFF00E676),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (isPulseAlive) Color(0xFF00E676) else Color(0xFFDC2626), CircleShape)
                )
            }
        }
    }
}
