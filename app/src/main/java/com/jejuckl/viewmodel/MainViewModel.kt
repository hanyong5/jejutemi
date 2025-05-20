package com.jejuckl.viewmodel

// viewmodel/MainViewModel.kt
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jejuckl.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.jejuckl.model.ChatResponse

class MainViewModel : ViewModel() {
    private val _aiResponse = MutableStateFlow("")
    val aiResponse = _aiResponse.asStateFlow()

    fun fetchAIResponse(message: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.chatWithAI(message)
                if (response.isSuccessful) {
                    Log.d("AI_RESPONSE", "response.body() = ${response.body()}")  // ✅ 로그 찍기

                    _aiResponse.value = response.body()?.message ?: "응답이 비어 있습니다."
                } else {
                    _aiResponse.value = "서버 오류: ${response.code()}"
                }
            } catch (e: Exception) {
                _aiResponse.value = "통신 오류: ${e.message}"
                Log.e("AI_RESPONSE", "예외: ${e.message}", e)
            }
        }

    }
    fun clearAIResponse() {
        _aiResponse.value = "" // ✅ 초기화
    }
}