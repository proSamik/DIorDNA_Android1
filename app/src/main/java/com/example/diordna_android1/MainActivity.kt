package com.example.diordna_android1

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun sendMessage(category: String){
            val intent = Intent(this, CategoryActivity::class.java).apply {
                putExtra("EXTRA_MESSAGE", category) //category is message here to next intent
            }
            startActivity(intent)
        }

        recordBtn.setOnClickListener {
            //Launch activity of Recording
            val intent = Intent(applicationContext, RecorderActivity::class.java)
            startActivity(intent)
        }

        urgentBtn.setOnClickListener {
            //Intent function to call category with message
            val category = "Urgent"
            sendMessage(category)
        }

        importantBtn.setOnClickListener {
            //Intent function to call category with message
            val category = "Important"
            sendMessage(category)
        }

        normalBtn.setOnClickListener {
            //Intent function to call category with message
            val category = "Normal"
            sendMessage(category)
        }
        Log.i(TAG, "loadAudioFiles: Successful ")
    }

}