package com.example.util

import java.security.MessageDigest

/**
 * تولید کد فعال‌سازی قطعی (deterministic) بر اساس کد پیگیری تراکنش و شناسه پلن.
 * چون سروری وجود ندارد، این تابع باید هم داخل صفحه مشتری و هم داخل ابزار مدیریت
 * (Admin) با ورودی یکسان، خروجی یکسان بدهد.
 *
 * محدودیت شناخته‌شده: چون منطق داخل خود اپ اجرا می‌شود، از نظر تئوری قابل
 * مهندسی معکوس است. برای مرحله فعلی (تا راه‌اندازی درگاه واقعی) قابل قبول است.
 */
object ActivationCodeUtil {
    private const val SALT = "HM_HAMMER_ACTIVATION_SALT_V1"

    fun generate(trackingCode: String, planId: String): String {
        val input = "${trackingCode.trim()}:${planId.trim()}:$SALT"
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02X".format(it) }.take(8)
    }
}
