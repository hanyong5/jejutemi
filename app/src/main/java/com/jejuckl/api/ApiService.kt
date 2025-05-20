package com.jejuckl.api

import com.jejuckl.model.ChatResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface ApiService {
    @GET("/api/jeju_content_agency/chat_with_ai")
    suspend fun chatWithAI(@Query("message") message: String): Response<ChatResponse>
}