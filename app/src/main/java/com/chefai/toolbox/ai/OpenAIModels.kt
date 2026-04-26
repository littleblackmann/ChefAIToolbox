package com.chefai.toolbox.ai

data class ChatMessage(
    val role: String,
    val content: String,
)

object OpenAIModelCatalog {

    const val RECIPE_MODEL: String = "gpt-5.4-mini"
    const val VISION_MODEL: String = "gpt-5.4"

    const val RECIPE_MODEL_DISPLAY: String = "GPT-5.4 Mini"
    const val VISION_MODEL_DISPLAY: String = "GPT-5.4"
}
