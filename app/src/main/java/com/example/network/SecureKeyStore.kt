package com.example.network

import java.io.File

object SecureKeyStore {
    private var memoryKey: String = ""
    private var storageDir: File? = null

    fun init(dir: File) {
        storageDir = dir
        val file = File(dir, "wallex_vault.key")
        if (file.exists()) {
            memoryKey = file.readText().trim()
        }
    }

    fun saveKey(key: String) {
        memoryKey = key.trim()
        storageDir?.let {
            val file = File(it, "wallex_vault.key")
            file.writeText(memoryKey)
        }
    }

    fun getKey(): String = memoryKey

    fun hasKey(): Boolean = memoryKey.isNotBlank()
}
