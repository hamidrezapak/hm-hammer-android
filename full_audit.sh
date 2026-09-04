#!/bin/bash
echo "========================================================"
echo "🔎 آغاز تست عمیق و بازرسی کل اپلیکیشن HM HAMMER PRO"
echo "========================================================"

echo -e "\n1️⃣ [تست دکمه‌ها]: بررسی دکمه‌های با بدنه خالی یا بدون اکشن (Dead Buttons):"
grep -rn -E "onClick\s*=\s*\{\s*\}" app/src/main/java/com/example/ui/ || echo "✅ دکمه کاملاً خالی یافت نشد."

echo -e "\n2️⃣ [تست اکشن‌های ماکت]: بررسی دکمه‌هایی که صرفاً توست (Toast) یا لاگ نمایشی دارند:"
grep -rn "Toast.makeText" app/src/main/java/com/example/ui/screens/

echo -e "\n3️⃣ [تست رندر چارت]: وضعیت پیاده‌سازی صفحه ChartRadarScreen:"
grep -rn "WebView\|Canvas\|Candle" app/src/main/java/com/example/ui/screens/ChartRadarScreen.kt

echo -e "\n4️⃣ [تست یکپارچگی View Model]: متغیرهایی که در اسکرین‌ها فراخوانی شده‌اند:"
for f in app/src/main/java/com/example/ui/screens/*.kt; do
    echo "--- بررسی فایل: $(basename $f) ---"
    grep -o "viewModel\.[a-zA-Z0-9_]*" "$f" | sort -u
done

echo -e "\n5️⃣ [تست داده‌های فیک و مقادیر هاردکد شده]:"
grep -rn "random" app/src/main/java/com/example/ui/viewmodel/
grep -rn "64500" app/src/main/java/com/example/ui/viewmodel/

echo -e "\n6️⃣ [بررسی وضعیت آخرین بیلد و خطاهای کامپایل گیت‌هاب]:"
gh run list --limit 1

echo "========================================================"
echo "🏁 پایان اسکن اولیه سورس‌کد"
echo "========================================================"
