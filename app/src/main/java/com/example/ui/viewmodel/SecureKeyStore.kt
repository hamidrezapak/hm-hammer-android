package com.example.ui.viewmodel

import java.io.File

object SecureKeyStore {
    private var memoryKey: String = ""
    private var rootDir: File? = null

    fun init(dir: File) {
        rootDir = dir
        val f = File(dir, "wallex_vault.key")
        if (f.exists()) {
            memoryKey = f.readText().trim()
        }
    }

    fun saveKey(key: String) {
        memoryKey = key.trim()
        rootDir?.let {
            try {
                File(it, "wallex_vault.key").writeText(memoryKey)
            } catch (e: Exception) {}
        }
    }

    fun getKey(): String = memoryKey
    fun hasKey(): Boolean = memoryKey.isNotBlank()
}
