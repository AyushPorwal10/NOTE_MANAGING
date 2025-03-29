package com.example.novanotes


import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.novanotes.geminichatbot.Constants
import com.example.novanotes.geminichatbot.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }
    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = Constants.apiKey
    )
    @RequiresApi(35)
    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing....", "model"))

                val response = chat.sendMessage(question)

                Log.d("AIResponse","AI Response is ${response.text}")

                messageList.removeLast()
                messageList.add(MessageModel(response.text.toString(), "model"))
            } catch (e: Exception) {
                messageList.removeLast()
                Log.d("ErrorInMessage", e.message.toString())
                messageList.add(MessageModel("Error : " + e.message.toString(), "model"))
            }
        }
    }
}
