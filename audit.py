import os, re

src = "app/src/main"
files = {}
for root, _, fnames in os.walk(src):
    for f in fnames:
        if f.endswith(".kt") or f.endswith(".xml"):
            p = os.path.join(root, f)
            with open(p, "r", encoding="utf-8", errors="ignore") as fl:
                files[p] = fl.read()

txt = "\n".join(files.values())

print("=== REALITY CHECK AUDIT ===")

# 1. API Call check
wallex_call = bool(re.search(r"api\.wallex\.ir", txt))
print("1. Direct Wallex API:", "REAL (Found URL)" if wallex_call else "FAKE/MISSING")

# 2. Key Persistence
persisted = "wallex_vault.key" in txt or "sharedPreferences" in txt.lower() or "getSharedPreferences" in txt
print("2. API Key Persistence:", "ACTIVE (Saved to disk)" if persisted else "RAM ONLY (Wipes on exit)")

# 3. UI Button binding
bound = any("connectApiKey" in c or "placeOrder" in c for p, c in files.items() if "TradeScreen" in p)
print("3. UI Button Binding:", "CONNECTED" if bound else "DISCONNECTED (No-op)")

# 4. Mock / Random data
mocks = [os.path.basename(p) for p, c in files.items() if "Random.nextDouble" in c or "Random.nextInt" in c]
print("4. Mock Data Generator:", f"WARNING in {mocks}" if mocks else "CLEAN (Real figures)")

# 5. Background Service
bg = "startForeground" in txt and "Service" in txt
print("5. 24/7 Background Service:", "ENABLED" if bg else "DISABLED (Killed when app closes)")

# 6. Internet Permission
net = "android.permission.INTERNET" in txt
print("6. Manifest Internet Perm:", "GRANTED" if net else "BLOCKED")

print("===========================")
