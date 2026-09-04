#!/bin/bash
echo "=================================================="
echo "🔍 اسکن عمیق کدهای سورس HM HAMMER (نسخه موبایل/کاتلین)"
echo "=================================================="

echo -e "\n[1] فایل‌های مرتبط با دستیار هوش مصنوعی (AI Copilot):"
find app/src/main/java -name "*Copilot*" -o -name "*Ai*" -o -name "*Assistant*"

echo -e "\n[2] فایل‌های مربوط به موتور چارت و رندرینگ کندل‌ها:"
find app/src/main/java -name "*Chart*" -o -name "*Candle*"

echo -e "\n[3] جستجوی توابع فیک، رندوم یا پاسخ‌های هاردکدشده:"
grep -rn "random" app/src/main/java/ --include="*.kt" | head -n 15

echo -e "\n[4] جستجوی متدهای ارسال سفارش واقعی به والکس (POST /v1/orders):"
grep -rn "wallex" app/src/main/java/ --include="*.kt"

echo -e "\n[5] وضعیت پکیج‌ها و متدهای شبکه (Retrofit / Ktor / OkHttp):"
grep -rn "Retrofit\|OkHttp\|HttpClient" app/src/main/java/ --include="*.kt" | head -n 10
echo "=================================================="
