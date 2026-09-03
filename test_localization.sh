#!/bin/bash
echo "=================================================="
echo "   HM HAMMER PRO - LOCALIZATION & SCREENS AUDIT   "
echo "=================================================="
echo ""

echo "1. وضعیت اتصال پارامتر currentLanguage در فراخوانی صفحات (MainActivity.kt):"
echo "--------------------------------------------------"
grep -n "Screen(" app/src/main/java/com/example/MainActivity.kt || echo "فایل یافت نشد"

echo ""
echo "2. بررسی تعریف پارامتر زبان در امضای توابع صفحات (Screens):"
echo "--------------------------------------------------"
for file in app/src/main/java/com/example/ui/screens/*.kt; do
    echo -n "$(basename $file): "
    if grep -q "currentLanguage" "$file"; then
        echo "✅ متصل به سیستم زبان"
    else
        echo "❌ بدون پارامتر زبان (هاردکد یا ایزوله)"
    fi
done

echo ""
echo "3. وضعیت پلن‌های اشتراک در SubscriptionsScreen.kt:"
echo "--------------------------------------------------"
grep -E "(Standard|PRO|ELITE|VIP)" app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt || echo "پلن‌ها ناقص هستند"

echo ""
echo "4. بررسی متون هاردکد شده انگلیسی در کل کامپوننت‌ها:"
echo "--------------------------------------------------"
grep -rn "Text(" app/src/main/java/com/example/ui/screens/ | grep -E '"[A-Za-z ]{4,}"' | head -n 15

echo ""
echo "=================================================="
