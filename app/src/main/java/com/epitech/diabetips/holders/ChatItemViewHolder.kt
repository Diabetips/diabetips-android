package com.epitech.diabetips.holders

import android.content.Context
import android.text.Layout
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.ChatObject
import com.epitech.diabetips.utils.ImageHandler
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_chat, parent, false)) {

    private val myChatLayout: ConstraintLayout = itemView.myChatLayout
    private val myChatText: TextView = itemView.myChatText
    private val myChatTime: TextView = itemView.myChatTime
    private val otherChatLayout: ConstraintLayout = itemView.otherChatLayout
    private val otherChatText: TextView = itemView.otherChatText
    private val otherChatTime: TextView = itemView.otherChatTime
    private val otherChatImage: ImageView = itemView.otherChatImage
    private var context: Context = inflater.context
    private val dateFormatAPI: String
    private val dateFormatHour: String

    init {
        dateFormatAPI = context.getString(R.string.format_time_api)
        dateFormatHour = context.getString(if (DateFormat.is24HourFormat(context)) R.string.format_date_simple_24 else R.string.format_date_simple_12)
    }

    fun bind(chat: ChatObject, myMessage: Boolean = false) {
        if (myMessage) {
            otherChatLayout.visibility = View.GONE
            myChatLayout.visibility = View.VISIBLE
            myChatText.text = chat.content
            myChatTime.text = TimeHandler.instance.changeTimeFormat(chat.time, dateFormatAPI, dateFormatHour)
        } else {
            myChatLayout.visibility = View.GONE
            otherChatLayout.visibility = View.VISIBLE
            otherChatText.text = chat.content
            otherChatTime.text = TimeHandler.instance.changeTimeFormat(chat.time, dateFormatAPI, dateFormatHour)
            ImageHandler.instance.loadImage(otherChatImage, context, UserService.instance.getPictureUrl(chat.from), R.drawable.ic_person)
        }
    }
}