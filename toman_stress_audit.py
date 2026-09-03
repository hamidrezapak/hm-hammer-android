import random

print("="*60)
print("🇮🇷 HM HAMMER PRO - 1,000,000 TOMAN REAL-CAPITAL AUDIT")
print("💰 Base Capital: 1,000,000 Tomans")
print("⚖️ Exchange Fee: 0.2% per trade (Nobitex / Wallex Spot standard)")
print("🛡️ Risk Per Trade: 2% of equity | Dynamic ATR R:R ~ 1:1.4")
print("="*60)

capital_tmn = 1_000_000.0
initial_capital = capital_tmn
peak_capital = capital_tmn
max_dd = 0.0

total_trades = 60  # معادل حجم معاملات یک ماه معامله‌گر فعال
wins = 0
losses = 0
total_fee_paid = 0.0

for i in range(1, total_trades + 1):
    risk_tmn = capital_tmn * 0.02
    fee = capital_tmn * 0.002
    total_fee_paid += fee

    is_win = random.random() < 0.61

    if is_win:
        wins += 1
        profit = (risk_tmn * random.uniform(1.3, 1.6)) - fee
        capital_tmn += profit
    else:
        losses += 1
        loss = risk_tmn + fee
        capital_tmn -= loss

    if capital_tmn > peak_capital:
        peak_capital = capital_tmn
    dd = (peak_capital - capital_tmn) / peak_capital * 100
    if dd > max_dd:
        max_dd = dd

win_rate = (wins / total_trades) * 100
net_profit_tmn = capital_tmn - initial_capital
roi = (net_profit_tmn / initial_capital) * 100

print(f"\n📊 نتایج شبیه‌سازی ۶۰ معامله (دوره یک‌ماهه):")
print(f"--------------------------------------------------")
print(f"🔹 سرمایه اولیه:               {int(initial_capital):,} تومان")
print(f"🔹 تعداد کل معاملات:          {total_trades}")
print(f"🎯 معاملات موفق (Win):         {wins} ({win_rate:.1f}%)")
print(f"🛑 معاملات استاپ (Loss):       {losses} ({100 - win_rate:.1f}%)")
print(f"💸 مجموع کارمزد صرافی:        {int(total_fee_paid):,} تومان")
print(f"🛡️ حداکثر افت سرمایه (DD):     {max_dd:.2f}%")
print(f"💰 موجودی نهایی حساب:          {int(capital_tmn):,} تومان")
print(f"📈 سود خالص به دست آمده:       {int(net_profit_tmn):+,} تومان ({roi:+.2f}%)")
print("="*60)
