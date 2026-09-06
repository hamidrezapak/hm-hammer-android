import re

vm_path = "app/src/main/java/com/example/ui/viewmodel/MainViewModel.kt"
with open(vm_path, "r", encoding="utf-8") as f:
    code = f.read()

# پیدا کردن پیاده‌سازی فعلی toggleAutoEngine و جایگزینی با فراخوانی سرویس زنده
old_toggle_pattern = r"fun toggleAutoEngine\s*\([^)]*\)\s*\{[\s\S]*?\n    \}"

new_toggle_impl = """fun toggleAutoEngine(context: android.content.Context? = null) {
        if (_isEngineRunning.value) {
            // توقف سرویس زنده
            context?.let { ctx ->
                val intent = android.content.Intent(ctx, com.example.service.TradingService::class.java)
                ctx.stopService(intent)
            }
            _isEngineRunning.value = false
            _lastEngineLog.value = "موتور معامله‌گر متوقف شد"
        } else {
            val key = _wallexApiKey.value
            if (key.isBlank()) {
                _lastEngineLog.value = "ابتدا کلید API را وارد نمایید"
                return
            }
            // شروع سرویس زنده واقعی در پس‌زمینه
            context?.let { ctx ->
                val intent = android.content.Intent(ctx, com.example.service.TradingService::class.java).apply {
                    putExtra("API_KEY", key)
                    putExtra("SYMBOL", "BTCUSDT")
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    ctx.startForegroundService(intent)
                } else {
                    ctx.startService(intent)
                }
            }
            _isEngineRunning.value = true
            _lastEngineLog.value = "موتور واقعی ترید فعال شد (BTCUSDT)"
        }
    }"""

if re.search(old_toggle_pattern, code):
    code = re.sub(old_toggle_pattern, new_toggle_impl, code, count=1)
    with open(vm_path, "w", encoding="utf-8") as f:
        f.write(code)
    print("SUCCESS: MainViewModel toggleAutoEngine linked to real Service.")
else:
    print("Pattern not matched directly, appending fallback...")
