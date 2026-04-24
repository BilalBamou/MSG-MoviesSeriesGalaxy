package com.msg.msgalaxy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER_VIEW = 1
    private val BOT_VIEW = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) USER_VIEW else BOT_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER_VIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_message, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bot_message, parent, false)
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is UserViewHolder) {
            holder.textMessage.text = message.message
        } else if (holder is BotViewHolder) {
            holder.textMessage.text = message.message
        }
    }

    override fun getItemCount(): Int = messages.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.userMessageText)
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.botMessageText)
    }
}