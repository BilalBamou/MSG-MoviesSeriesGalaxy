package com.msg.msgalaxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ChatbotBottomSheet : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputField: EditText
    private lateinit var sendButton: MaterialButton
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.chatbot_bottom_sheet, container, false)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        inputField = view.findViewById(R.id.inputField)
        sendButton = view.findViewById(R.id.sendButton)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        messages.add(ChatMessage("Hello! this is MSG AI Assistant. Ask me about movies.", false))
        adapter.notifyDataSetChanged()

        sendButton.setOnClickListener {
            val text = inputField.text.toString().trim()

            if (text.isNotEmpty()) {
                messages.add(ChatMessage(text, true))
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)
                inputField.text.clear()

                askAI(text)
            }
        }

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

    private fun askAI(question: String) {
        val client = OkHttpClient()

        val json = JSONObject("""
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "$question"
                }
              ]
            }
          ]
        }
        """.trimIndent())

        val body = json.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=AIzaSyApWHBJ1bmdDgnpcrKu63_5F4icsypu4eM")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    messages.add(ChatMessage("Error: ${e.message}", false))
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string() ?: "No response"

                activity?.runOnUiThread {
                    messages.add(ChatMessage(responseText, false))
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
        })
    }
}