package com.example.network

import java.io.File

object SecureKeyStore {
    private var memoryKey: String = ""
    private val keyFile: File
        get() {
            val dir = File("/sdcard/Android/data/com.aistudio.hmhammer.pro7x9/files")
            if (!dir.exists()) dir.mkdirs()
            return File(dir, "wallex_vault.key")
        }

    fun init(dir: File? = null) {
        getKey()
    }

    fun saveKey(key: String) {
        val clean = key.trim()
        memoryKey = clean
        try {
            keyFile.writeText(clean)
        } catch (_: Exception) {}
    }

    fun getKey(): String {
        if (memoryKey.isBlank()) {
            try {
                if (keyFile.exists()) {
                    memoryKey = keyFile.readText().trim()
                }
            } catch (_: Exception) {}
        }
        return memoryKey
    }

    fun hasKey(): Boolean = getKey().isNotBlank()
}
