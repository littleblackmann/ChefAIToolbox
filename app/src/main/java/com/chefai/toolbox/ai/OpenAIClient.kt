package com.chefai.toolbox.ai

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class OpenAIClient {

    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val sseFactory = EventSources.createFactory(http)

    fun streamChat(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        temperature: Double = 0.8,
    ): Flow<String> = callbackFlow {
        val bodyJson = JSONObject().apply {
            put("model", model)
            put("stream", true)
            put("temperature", temperature)
            put("messages", messages.toJsonArray())
        }

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toString().toRequestBody(JSON_MEDIA))
            .build()

        val source: EventSource = sseFactory.newEventSource(request, object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                if (data == "[DONE]") {
                    close()
                    return
                }
                val token = parseStreamDelta(data)
                if (token.isNotEmpty()) trySend(token)
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: okhttp3.Response?) {
                val msg = buildErrorMessage(response, t)
                close(OpenAIException(msg))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        })

        awaitClose { source.cancel() }
    }.flowOn(Dispatchers.IO)

    fun streamVisionChat(
        apiKey: String,
        model: String,
        systemPrompt: String,
        userText: String,
        image: Bitmap,
        temperature: Double = 0.4,
    ): Flow<String> = callbackFlow {
        val base64 = image.toJpegBase64(quality = 80, maxEdge = 1024)
        val userContent = JSONArray().apply {
            put(JSONObject().apply {
                put("type", "text")
                put("text", userText)
            })
            put(JSONObject().apply {
                put("type", "image_url")
                put("image_url", JSONObject().apply {
                    put("url", "data:image/jpeg;base64,$base64")
                })
            })
        }

        val bodyJson = JSONObject().apply {
            put("model", model)
            put("stream", true)
            put("temperature", temperature)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userContent)
                })
            })
        }

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toString().toRequestBody(JSON_MEDIA))
            .build()

        val source: EventSource = sseFactory.newEventSource(request, object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                if (data == "[DONE]") {
                    close()
                    return
                }
                val token = parseStreamDelta(data)
                if (token.isNotEmpty()) trySend(token)
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: okhttp3.Response?) {
                val msg = buildErrorMessage(response, t)
                close(OpenAIException(msg))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        })

        awaitClose { source.cancel() }
    }.flowOn(Dispatchers.IO)

    suspend fun validateKey(apiKey: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.openai.com/v1/models")
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()
            http.newCall(request).execute().use { response ->
                when {
                    response.isSuccessful -> Result.success(Unit)
                    response.code == 401 -> Result.failure(OpenAIException("API Key 無效或已過期"))
                    response.code == 429 -> Result.failure(OpenAIException("請求過於頻繁，請稍後再試"))
                    else -> Result.failure(OpenAIException("驗證失敗（HTTP ${response.code}）"))
                }
            }
        } catch (e: Exception) {
            val detail = e.message ?: e.javaClass.simpleName
            Result.failure(OpenAIException("網路錯誤：$detail"))
        }
    }

    private fun parseStreamDelta(data: String): String {
        return try {
            val json = JSONObject(data)
            val choices = json.optJSONArray("choices") ?: return ""
            if (choices.length() == 0) return ""
            val delta = choices.getJSONObject(0).optJSONObject("delta") ?: return ""
            delta.optString("content", "")
        } catch (_: Exception) {
            ""
        }
    }

    private fun buildErrorMessage(response: okhttp3.Response?, t: Throwable?): String {
        if (response != null) {
            val code = response.code
            val bodyText = runCatching { response.body?.string() }.getOrNull().orEmpty()
            val apiMsg = runCatching {
                JSONObject(bodyText).optJSONObject("error")?.optString("message")
            }.getOrNull()
            return when {
                !apiMsg.isNullOrBlank() -> "OpenAI 回傳錯誤（$code）：$apiMsg"
                code == 401 -> "API Key 無效，請到設定頁重新輸入"
                code == 404 -> "OpenAI 找不到此模型（可能暫時下架），請稍後再試"
                code == 429 -> "使用額度不足或請求過於頻繁"
                else -> "OpenAI 服務錯誤（HTTP $code）"
            }
        }
        return "網路連線失敗：${t?.message ?: "未知錯誤"}"
    }

    private fun List<ChatMessage>.toJsonArray(): JSONArray {
        val arr = JSONArray()
        forEach {
            arr.put(JSONObject().apply {
                put("role", it.role)
                put("content", it.content)
            })
        }
        return arr
    }

    private fun Bitmap.toJpegBase64(quality: Int, maxEdge: Int): String {
        val scaled = if (width > maxEdge || height > maxEdge) {
            val ratio = maxEdge.toFloat() / maxOf(width, height)
            Bitmap.createScaledBitmap(this, (width * ratio).toInt(), (height * ratio).toInt(), true)
        } else this
        val stream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
    }

    companion object {
        private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
    }
}

class OpenAIException(message: String) : Exception(message)
