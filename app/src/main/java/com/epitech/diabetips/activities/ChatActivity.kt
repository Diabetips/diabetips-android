package com.epitech.diabetips.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.ChatAdapter
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.services.ChatService
import com.epitech.diabetips.storages.ChatObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.textWatchers.TextChangedWatcher
import com.epitech.diabetips.utils.ADiabetipsActivity
import com.epitech.diabetips.utils.PaginationInvertedScrollListener
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : ADiabetipsActivity(R.layout.activity_chat) {

    private lateinit var page: PaginationObject
    private lateinit var chatUserUid: String
    private var loading = false

    companion object {
        var instance: ChatActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        chatUserUid = UserManager.instance.getChatUser(this).uid
        closeChatButton.setOnClickListener {
            finish()
        }
        sendChatButton.setOnClickListener {
            sendMessage()
        }
        chatInput.addTextChangedListener(TextChangedWatcher {
            sendChatButton.visibility = (if (it.isNullOrEmpty()) View.GONE else View.VISIBLE)
        })
        chatList.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = ChatAdapter(UserManager.instance.getUser(context).uid)
        }
        chatList.addOnScrollListener(object : PaginationInvertedScrollListener(chatList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return page.isLast()
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun loadMoreItems() {
                getMessage(false)
            }
        })
        getMessage()
        instance = this
    }

    private fun getMessage(resetPage: Boolean = true) {
        loading = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        ChatService.instance.getAllMessage(chatUserUid, page).doOnSuccess {
            if (it.second.component2() == null) {
                page.updateFromHeader(it.first.headers[getString(R.string.pagination_header)]?.get(0))
                if (resetPage) {
                    (chatList.adapter as ChatAdapter).setMessages(it.second.component1()!!)
                    scrollToPosition()
                } else {
                    (chatList.adapter as ChatAdapter).addMessages(it.second.component1()!!)
                }
            }
            loading = false
        }.subscribe()
    }

    private fun sendMessage() {
        loading = true
        ChatService.instance.addMessage(ChatObject(content = chatInput.text.toString(), to = chatUserUid)).doOnSuccess {
            if (it.second.component2() == null) {
                (chatList.adapter as ChatAdapter).addMessage(it.second.component1()!!)
                scrollToPosition()
            } else {
                Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
            loading = false
        }.subscribe()
        chatInput.setText("")
    }

    fun getLastMessage() {
        loading = true
        ChatService.instance.getAllMessage(chatUserUid, PaginationObject(1, resources.getInteger(R.integer.pagination_default))).doOnSuccess {
            if (it.second.component2() == null && !it.second.component1().isNullOrEmpty()) {
                (chatList.adapter as ChatAdapter).addMessage(it.second.component1()!!.first())
                scrollToPosition()
            }
            loading = false
        }.subscribe()
    }

    private fun scrollToPosition(pos: Int = (chatList.adapter?.itemCount ?: 1) - 1) {
        chatList.layoutManager?.scrollToPosition(pos.coerceIn(0, (chatList.adapter?.itemCount ?: 1) - 1))
    }

    override fun onPause() {
        instance = null
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        instance = this
        getMessage()
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }
}
