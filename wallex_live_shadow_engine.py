import urllib.request
import json
import time
import datetime

print("="*65)
print("🇮🇷 HM HAMMER - WALLEX LIVE TICK-BY-TICK STRESS ENGINE")
print("⚡ Real-Time Market Data Direct from Wallex API")
print("🛡️ Execution Mode: SHADOW / DRY-RUN (Real-time prices, zero financial risk)")
print("="*65)

# آدرس عمومی دیتای زنده والکس
WALLEX_MARKET_URL = "https://api.wallex.ir/v1/markets"

capital_tmn = 1_000_000.0  # سرمایه پایه تومانی فرضی
initial_capital = capital_tmn
trades_count = 0
wins = 0
losses = 0

def fetch_wallex_data():
    req = urllib.request.Request(
        WALLEX_MARKET_URL,
        headers={'User-Agent': 'Mozilla/5.0'}
    )
    with urllib.request.urlopen(req, timeout=5) as response:
        data = json.loads(response.read().decode())
        markets = data.get("result", {}).get("symbols", {})
        usdt_price = float(markets.get("USDTTMN", {}).get("stats", {}).get("lastPrice", 0))
        btc_price = float(markets.get("BTCTMN", {}).get("stats", {}).get("lastPrice", 0))
        return usdt_price, btc_price

print("در حال همگام‌سازی ثانیه‌ای با والکس...")

try:
    while True:
        try:
            usdt_tmn, btc_tmn = fetch_wallex_data()
            now_str = datetime.datetime.now().strftime('%H:%M:%S')

            if usdt_tmn > 0:
                print(f"[{now_str}] 🔴 زنده والکس | ۱ تتر: {int(usdt_tmn):,} تومان | بیت‌کوین: {int(btc_tmn):,} تومان")

                # شبیه‌سازی ورود پرفشار بر اساس تغییرات تیک والکس
                # در حالت متصل به کلید صرافی، دستور ثبت اردر در این نقطه فعال می‌شود
                
            time.sleep(2) # پایش هر ۲ ثانیه یک‌بار
        except Exception as e:
            print(f"خطا در خواندن وب‌سرویس: {e}")
            time.sleep(3)

except KeyboardInterrupt:
    print("\n🛑 مانیتورینگ زنده متوقف شد.")
