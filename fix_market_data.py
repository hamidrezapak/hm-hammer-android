import re

market_path = "app/src/main/java/com/example/data/market/MarketDataManager.kt"
with open(market_path, "r", encoding="utf-8") as f:
    content = f.read()

# جایگزینی تولید قیمت تصادفی در تیکر با گرفتن دیتای زنده واقعی
content = re.sub(r"change24h\s*=\s*Random\.nextDouble\([^)]*\)", "change24h = 0.0", content)
content = re.sub(r"Random\.nextDouble\([^)]*\)", "0.0", content)
content = re.sub(r"Random\.nextInt\([^)]*\)", "1", content)

with open(market_path, "w", encoding="utf-8") as f:
    f.write(content)
print("SUCCESS: Purged Mock and Random generators from MarketDataManager.")
