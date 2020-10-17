package com.epitech.diabetips.activities

import android.os.Bundle
import com.epitech.diabetips.R
import com.epitech.diabetips.utils.ADiabetipsActivity
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : ADiabetipsActivity(R.layout.activity_chat) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeChatGlucoseButton.setOnClickListener {
            finish()
        }
        //TODO chat
    }
}
