package com.example.nativepasskeys

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        val greeting = findViewById<TextView>(R.id.home_greeting_txt)
        greeting.text = (getString(R.string.home_welcome, "Carla!"))

        val logoutBtn = findViewById<Button>(R.id.home_logout_btn)
        logoutBtn.setOnClickListener {
            finish()
//            TODO("implement clearing auth0 data")
        }

    }
}