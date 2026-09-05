import re

path = "app/src/main/java/com/example/ui/viewmodel/MainViewModel.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# حذف توابع قبلی در صورت وجود
text = re.sub(r"fun startRealEngine[\s\S]*", "", text).rstrip()

# پیدا کردن آخرین آکولاد بسته کلاس
last_brace_idx = text.rfind("}")
if last_brace_idx != -1:
    body = text[:last_brace_idx].rstrip()
    service_code = """

    fun startRealEngine(context: android.content.Context, symbol: String = "BTCUSDT") {
        val key = _wallexApiKey.value
        if (key.isBlank()) {
            _lastEngineLog.value = "API Key is required"
            return
        }
        val intent = android.content.Intent(context, com.example.service.TradingService::class.java).apply {
            putExtra("API_KEY", key)
            putExtra("SYMBOL", symbol)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        _isEngineRunning.value = true
        _lastEngineLog.value = "Trading engine started for " + symbol
    }

    fun stopRealEngine(context: android.content.Context) {
        val intent = android.content.Intent(context, com.example.service.TradingService::class.java)
        context.stopService(intent)
        _isEngineRunning.value = false
        _lastEngineLog.value = "Trading engine stopped"
    }
}
"""
    new_text = body + service_code
    with open(path, "w", encoding="utf-8") as f:
        f.write(new_text)
    print("SUCCESS: MainViewModel updated properly inside class bounds.")
else:
    print("ERROR: Closing brace not found.")
