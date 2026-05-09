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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
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
    private lateinit var chatHistoryRef: DatabaseReference

    private lateinit var newChatBtn: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        chatHistoryRef = FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .child(currentUser.uid)
            .child("chatBotHistory")
            .child("messages")

        recyclerView = findViewById(R.id.chatBotActivity_recyclerViewId)
        messageEditText = findViewById(R.id.aboutMovieOrSerieActivity_commentEditTextId)
        sendMessageBtn = findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId)
        sendMessageBtnIcon = findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId_Icon)
        relativeLayout = findViewById(R.id.chatBotActivity_relativeViewId)
        newChatBtn = findViewById(R.id.chatBotActivity_newChat)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateViewsVisibility()
        getUsername()
        loadChatHistory()
        messageProcess()
        arrowBack()
        setupNewChatButton()
    }

    private fun setupNewChatButton() {
        newChatBtn.setOnClickListener {
            if (messages.isEmpty()) {
                return@setOnClickListener
            }

            showNewChatConfirmationDialog()
        }
    }

    private fun showNewChatConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("New Chat")
            .setMessage("Start a new conversation?\nCurrent chat will be deleted.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Clear Chat") { dialog, _ ->
                startNewChat()
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun startNewChat() {
        chatHistoryRef.removeValue()

        messages.clear()

        adapter.notifyDataSetChanged()
        updateViewsVisibility()
    }
    private fun loadChatHistory() {
        chatHistoryRef.orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()

                for (child in snapshot.children) {
                    val text = child.child("message").getValue(String::class.java) ?: ""
                    val isUser = child.child("isUser").getValue(Boolean::class.java) ?: false
                    messages.add(ChatMessage(text, isUser))
                }

                adapter.notifyDataSetChanged()
                updateViewsVisibility()

                // Scroll to bottom
                if (messages.isNotEmpty()) {
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional: Show toast
            }
        })
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
                val userMessage = ChatMessage(text, true)
                messages.add(userMessage)
                updateViewsVisibility()
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)
                messageEditText.editText?.text?.clear()

                saveMessageToFirebase(userMessage)
                askAI(text)
            }
        }
    }


    private fun saveMessageToFirebase(chatMessage: ChatMessage) {
        val messageId = chatHistoryRef.push().key ?: return

        val messageMap = mapOf(
            "message" to chatMessage.message,
            "isUser" to chatMessage.isUser
        )

        chatHistoryRef.child(messageId).setValue(messageMap)
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
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=AIzaSyBMof6y6Z6oicBsEdOvWKm8dVoTcI3u8Xw")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                this@ChatBotActivity.runOnUiThread {
                    messages.remove(typingPlaceholder)
                    val errorMsg = ChatMessage("Please try again", false)
                    messages.add(errorMsg)
                    adapter.notifyDataSetChanged()
                    saveMessageToFirebase(errorMsg)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string()
                this@ChatBotActivity.runOnUiThread {
                    messages.remove(typingPlaceholder)

                    val botMessage = if (!response.isSuccessful || responseText == null) {
                        ChatMessage("API Error: ${response.code}", false)
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
                            ChatMessage(reply.trim(), false)
                        } catch (e: Exception) {
                            ChatMessage("Parsing Error", false)
                        }
                    }

                    messages.add(botMessage)
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)

                    saveMessageToFirebase(botMessage)
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