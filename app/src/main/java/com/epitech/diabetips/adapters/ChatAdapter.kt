package com.epitech.diabetips.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.holders.ChatItemViewHolder
import com.epitech.diabetips.storages.ChatObject

class ChatAdapter(private val userId: String = "", private val messages: ArrayList<ChatObject> = arrayListOf())
    : RecyclerView.Adapter<ChatItemViewHolder>() {

    fun setMessages(messageList: Array<ChatObject>) {
        messages.clear()
        addMessages(messageList)
    }

    fun addMessages(messageList: Array<ChatObject>) {
        messageList.forEach {
            messages.add(0, it)
        }
        notifyDataSetChanged()
    }

    fun addMessage(message: ChatObject) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun getItemCount(): Int = messages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChatItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(messages[position], messages[position].from == userId)
    }

}

