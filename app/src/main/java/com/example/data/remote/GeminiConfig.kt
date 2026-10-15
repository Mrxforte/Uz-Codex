package com.example.data.remote

import com.example.BuildConfig

object GeminiConfig {

    const val DEFAULT_GENERATION_MODEL = "gemini-2.5-flash"

    val FALLBACK_GENERATION_MODELS = listOf(
        "gemini-2.5-flash",
        "gemini-1.5-pro",
        "gemini-1.5-flash",
        "gemini-2.0-flash"
    )

    const val EMBEDDING_MODEL = "text-embedding-004"

    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else ""
        } catch (_: Throwable) {
            ""
        }
    }
}
