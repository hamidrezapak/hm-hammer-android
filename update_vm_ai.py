with open("app/src/main/java/com/example/ui/viewmodel/MainViewModel.kt", "r") as f:
    code = f.read()

# وارد کردن ایمپورت هوش مصنوعی
if "import com.example.ai.AICopilotEngine" not in code:
    code = "import com.example.ai.AICopilotEngine\n" + code

# جایگزینی متد با فراخوانی کوروتین هوش مصنوعی واقعی
old_func_start = "fun queryAiCopilot(userQuestion: String): String {"
if old_func_start in code:
    idx = code.find(old_func_start)
    end_idx = code.find("fun purgeSandbox()", idx)
    new_func = """suspend fun queryAiCopilot(userQuestion: String): String {
        return AICopilotEngine.queryRealAi(_selectedPair.value, _currentPrice.value, userQuestion)
    }

    """
    code = code[:idx] + new_func + code[end_idx:]

with open("app/src/main/java/com/example/ui/viewmodel/MainViewModel.kt", "w") as f:
    f.write(code)
print("MainViewModel AI updated!")
