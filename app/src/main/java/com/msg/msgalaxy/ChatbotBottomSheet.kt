package com.msg.msgalaxy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatbotBottomSheet : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var messageEditText: TextInputLayout
    private lateinit var sendMessageBtn: CardView
    private lateinit var sendMessageBtnIcon: ImageView

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.chatbot_bottom_sheet, container, false)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        messageEditText = view.findViewById(R.id.aboutMovieOrSerieActivity_commentEditTextId)
        sendMessageBtn = view.findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId)
        sendMessageBtnIcon = view.findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId_Icon)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        messages.add(ChatMessage("Hello! this is MSG AI Assistant 😊. Ask me about Cinematic Universe!", false))
        adapter.notifyDataSetChanged()

        messageProcess()

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 90 / 100),
            (resources.displayMetrics.heightPixels * 75 / 100)
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    // ✅ FIXED AI FUNCTION
    private fun askAI(question: String) {

        val recentMessages = messages.takeLast(6)

        val conversationHistory = recentMessages.joinToString("\n") {
            if (it.isUser) "User: ${it.message}" else "Bot: ${it.message}"
        }

        // ✅ CORRECT JSON STRUCTURE
        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put(
                                "text",
                                "You are MSG AI Assistant inside a movie app. Answer shortly.\n$conversationHistory\nUser: $question"
                            )
                        })
                    })
                })
            })

            put("generationConfig", JSONObject().apply {
                put("maxOutputTokens", 200)
            })
        }

        val requestBody = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=AIzaSyBOsIkRJOKWzYCsW_vqwMPT5Ak3r-sJXEQ")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    messages.add(ChatMessage("Connection error: ${e.message}", false))
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string()

                if (!response.isSuccessful || responseText == null) {
                    activity?.runOnUiThread {
                        messages.add(ChatMessage("API Error: ${response.code}\n$responseText", false))
                        adapter.notifyDataSetChanged()
                    }
                    return
                }

                try {
                    val jsonObject = JSONObject(responseText)

                    val reply = jsonObject
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    activity?.runOnUiThread {
                        messages.add(ChatMessage(reply.trim(), false))
                        adapter.notifyDataSetChanged()
                        recyclerView.scrollToPosition(messages.size - 1)
                    }

                } catch (e: Exception) {
                    activity?.runOnUiThread {
                        messages.add(ChatMessage("Parsing Error:\n$responseText", false))
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    // ✅ FIXED MESSAGE PROCESS
    private fun messageProcess() {

        messageEditText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isEmpty = s.toString().trim().isEmpty()

                sendMessageBtn.isEnabled = !isEmpty
                sendMessageBtnIcon.setImageResource(
                    if (isEmpty) R.drawable.send_comment_gray_icon
                    else R.drawable.send_comment_icon
                )
            }
        })

        // ✅ ALWAYS SET CLICK LISTENER (FIXED BUG)
        sendMessageBtn.setOnClickListener {
            val text = messageEditText.editText?.text.toString().trim()

            if (text.isNotEmpty()) {
                messages.add(ChatMessage(text, true))
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)

                messageEditText.editText?.text?.clear()

                askAI(text)
            }
        }
    }
}