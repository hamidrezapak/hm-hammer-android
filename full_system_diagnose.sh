#!/bin/bash
echo "========================================================"
echo "🩺 تست و کالبدشکافی زنده کامپوننت‌های HM HAMMER PRO"
echo "========================================================"

echo -e "\n1️⃣ [وضعیت مجوز اینترنت و دسترسی WebView در AndroidManifest]:"
grep -rn "uses-permission.*INTERNET" app/src/main/AndroidManifest.xml || echo "❌ ارور بحرانی: مجوز اینترنت در مانیفست وجود ندارد! (دلیل لود نشدن چارت)"
grep -rn "usesCleartextTraffic" app/src/main/AndroidManifest.xml || echo "⚠️ ترافیک متنی باز نیست."

echo -e "\n2️⃣ [بررسی اتصال دکمه‌های خرید و فروش در TradeScreen]:"
grep -rn "executeOrder\|executeWallexOrder" app/src/main/java/com/example/ui/screens/TradeScreen.kt

echo -e "\n3️⃣ [بررسی وضعیت دکمه استارت/استاپ خودکار]:"
grep -rn "toggleAutoEngine" app/src/main/java/com/example/ui/screens/TradeScreen.kt

echo -e "\n4️⃣ [بررسی تاریخچه و جدول سفارشات در TransactionHistoryScreen]:"
grep -rn "filteredHistoryTrades\|trades" app/src/main/java/com/example/ui/screens/TransactionHistoryScreen.kt | head -n 10

echo -e "\n5️⃣ [بررسی وضعیت رند چارت و آدرس سورس TradingView]:"
grep -rn "WebView\|TradingView" app/src/main/java/com/example/ui/screens/ChartRadarScreen.kt

echo -e "\n6️⃣ [بررسی وضعیت آخرین بیلد در گیت‌هاب اکشنز]:"
gh run list --limit 1
echo "========================================================"
