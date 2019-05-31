package com.epitech.diabetips.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.epitech.diabetips.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
