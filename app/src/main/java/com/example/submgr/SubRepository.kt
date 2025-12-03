package com.example.submgr

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SubRepository(private val dao: ConfigDao) {
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun refreshFromUrl(url: String) {
        val text = fetchUrl(url) ?: return
        val decoded = tryDecodePossibleBase64Subscription(text)
        val lines = decoded.lines().map { it.trim() }.filter { it.isNotEmpty() }

        val items = lines.map { raw ->
            val type = detectType(raw)
            val remark = extractRemark(raw, type)
            ConfigItem(raw = raw, type = type, remark = remark, enabled = false)
        }
        withContext(Dispatchers.IO) {
            dao.clear()
            dao.insertAll(items)
        }
    }

    fun configsFlow() = dao.getAll()

    suspend fun toggle(item: ConfigItem) {
        item.enabled = !item.enabled
        dao.update(item)
    }

    private suspend fun fetchUrl(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder().url(url).get().build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext null
                resp.body?.string()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun tryDecodePossibleBase64Subscription(text: String): String {
        val t = text.trim()
        return try {
            val decoded = Base64.decode(t, Base64.DEFAULT)
            String(decoded)
        } catch (e: Exception) {
            t
        }
    }

    private fun detectType(raw: String): String {
        return when {
            raw.startsWith("vmess://", ignoreCase = true) -> "vmess"
            raw.startsWith("ss://", ignoreCase = true) -> "ss"
            else -> "unknown"
        }
    }

    private fun extractRemark(raw: String, type: String): String? {
        return try {
            when (type) {
                "vmess" -> {
                    val b64 = raw.removePrefix("vmess://")
                    val json = String(Base64.decode(b64, Base64.DEFAULT))
                    val adapter = moshi.adapter(Map::class.java)
                    val map = adapter.fromJson(json) as? Map<*, *>
                    (map?.get("ps") ?: map?.get("remark"))?.toString()
                }
                "ss" -> {
                    val idx = raw.indexOf('#')
                    if (idx >= 0) java.net.URLDecoder.decode(raw.substring(idx + 1), "utf-8") else null
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
