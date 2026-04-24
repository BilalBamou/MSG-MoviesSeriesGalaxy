package com.msg.msgalaxy

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ChatbotActivity : AppCompatActivity() {

    private lateinit var chatBox: TextView
    private lateinit var inputField: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(generateLayout())

        chatBox = findViewById(1001)
        inputField = findViewById(1002)
        sendButton = findViewById(1003)

        appendMessage("Bot: Hello! Ask me about movies 🎬")

        sendButton.setOnClickListener {
            val userText = inputField.text.toString()
            if (userText.isNotEmpty()) {
                appendMessage("You: $userText")
                val response = getBotResponse(userText)
                appendMessage("Bot: $response")
                inputField.text.clear()
            }
        }
    }

    private fun appendMessage(message: String) {
        chatBox.append("\n$message")
    }

    private fun getBotResponse(input: String): String {
        val text = input.lowercase()

        return when {
            text.contains("action") -> "Try John Wick 🔥"
            text.contains("romance") -> "Try Titanic ❤️"
            text.contains("comedy") -> "Watch The Hangover 😂"
            text.contains("sci-fi") -> "Interstellar is amazing 🚀"
            text.contains("hello") -> "Hello there 👋"
            else -> "Check trending movies in MSG app 🎬"
        }
    }

    private fun generateLayout(): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val chat = TextView(this)
        chat.id = 1001
        chat.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        )

        val input = EditText(this)
        input.id = 1002
        input.hint = "Ask me..."

        val button = Button(this)
        button.id = 1003
        button.text = "Send"

        layout.addView(chat)
        layout.addView(input)
        layout.addView(button)

        return layout
    }
}