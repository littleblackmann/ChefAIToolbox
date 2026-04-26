package com.chefai.toolbox.data

import android.graphics.Bitmap
import com.chefai.toolbox.ai.ChatMessage
import com.chefai.toolbox.ai.OpenAIClient
import com.chefai.toolbox.ai.OpenAIException
import com.chefai.toolbox.ai.OpenAIModelCatalog
import com.chefai.toolbox.ai.PromptBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChefAIRepository(
    private val client: OpenAIClient,
    private val settings: AppSettings,
) {

    fun fridgeRecipeStream(
        ingredients: List<String>,
        style: String,
        recipeCount: Int = 3,
        servings: Int = 2,
    ): Flow<String> {
        val key = settings.apiKey ?: return errorFlow("尚未設定 OpenAI API Key")
        val messages = listOf(
            ChatMessage("system", PromptBuilder.recipeSystemPrompt()),
            ChatMessage("user", PromptBuilder.recipeUserPrompt(ingredients, style, recipeCount, servings)),
        )
        return client.streamChat(
            apiKey = key,
            model = OpenAIModelCatalog.RECIPE_MODEL,
            messages = messages,
            temperature = 0.85,
        )
    }

    fun calorieVisionStream(image: Bitmap, userNote: String?): Flow<String> {
        val key = settings.apiKey ?: return errorFlow("尚未設定 OpenAI API Key")
        return client.streamVisionChat(
            apiKey = key,
            model = OpenAIModelCatalog.VISION_MODEL,
            systemPrompt = PromptBuilder.calorieSystemPrompt(),
            userText = PromptBuilder.calorieUserPrompt(userNote),
            image = image,
            temperature = 0.4,
        )
    }

    suspend fun validateApiKey(key: String): Result<Unit> = client.validateKey(key)

    private fun errorFlow(message: String): Flow<String> = flow {
        throw OpenAIException(message)
    }
}
