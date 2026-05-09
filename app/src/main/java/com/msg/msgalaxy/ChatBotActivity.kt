package com.msg.msgalaxy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.com.msg.msgalaxy.ChatAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatBotActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var messageEditText: TextInputLayout
    private lateinit var sendMessageBtn: CardView
    private lateinit var sendMessageBtnIcon: ImageView

    private lateinit var userNamerTxtview: TextView
    private lateinit var userName: String

    private val client = OkHttpClient()
    private lateinit var relativeLayout: RelativeLayout

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        recyclerView = findViewById(R.id.chatBotActivity_recyclerViewId)
        messageEditText = findViewById(R.id.aboutMovieOrSerieActivity_commentEditTextId)
        sendMessageBtn = findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId)
        sendMessageBtnIcon = findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId_Icon)
        relativeLayout = findViewById(R.id.chatBotActivity_relativeViewId)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateViewsVisibility()

        getUsername()

        messageProcess()

        arrowBack()
    }

    private fun updateViewsVisibility() {
        if (messages.isEmpty()) {
            relativeLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            relativeLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    private fun getUsername() {
        userNamerTxtview = findViewById(R.id.chatBotActivity_userName)

        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser.uid)

        reference.child("username").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.getValue(String::class.java)!!
                userNamerTxtview.text = "Hello, " + userName
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
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

        sendMessageBtn.setOnClickListener {
            val text = messageEditText.editText?.text.toString().trim()

            if (text.isNotEmpty()) {
                messages.add(ChatMessage(text, true))
                updateViewsVisibility()

                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)

                messageEditText.editText?.text?.clear()

                askAI(text)
            }
        }
    }

    private fun askAI(question: String) {

        val typingPlaceholder = ChatMessage("Thinking...", false)
        messages.add(typingPlaceholder)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)

        val recentMessages = messages.takeLast(7)

        val conversationHistory = recentMessages.joinToString("\n") {
            if (it.isUser) "User: ${it.message}" else "${it.message}"
        }

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
                put("maxOutputTokens", 150)
            })
        }

        val requestBody = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=AIzaSyAuabBpHwhzphv3AmwlbD8RD-0bHFtxFPQ")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                this@ChatBotActivity.runOnUiThread {
                    messages.remove(typingPlaceholder)
                    messages.add(ChatMessage("Please try again", false))
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string()

                this@ChatBotActivity.runOnUiThread {
                    messages.remove(typingPlaceholder)

                    if (!response.isSuccessful || responseText == null) {
                        messages.add(ChatMessage("API Error: ${response.code}", false))
                    } else {
                        try {
                            val jsonObject = JSONObject(responseText)
                            val reply = jsonObject
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text")

                            messages.add(ChatMessage(reply.trim(), false))
                        } catch (e: Exception) {
                            messages.add(ChatMessage("Parsing Error", false))
                        }
                    }
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
        })
    }

    private fun arrowBack() {
        var arrowBack: CardView = findViewById(R.id.chatBotActivity_arrowBackId)
        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}