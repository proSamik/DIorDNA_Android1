package com.example.diordna_android1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //It will open new Intent with that category
        fun sendMessage(category: String){
            val intent = Intent(this, CategoryActivity::class.java).apply {
                putExtra("EXTRA_MESSAGE", category) //category is message here to next intent
            }
            startActivity(intent)
        }

        //Navigate to Record Note Activity
        recordBtn.setOnClickListener {
            //Launch activity of Recording
            val intent = Intent(applicationContext, RecorderActivity::class.java)
            startActivity(intent)
        }

        //Call the category Activity with intent message as Urgent
        urgentBtn.setOnClickListener {
            //Intent function to call category with message
            val category = "Urgent"
            sendMessage(category)
        }

        //Call the important Activity with intent message as Urgent
        importantBtn.setOnClickListener {
            //Intent function to call category with message
            val category = "Important"
            sendMessage(category)
        }

        //Call the normal Activity with intent message as Urgent
        normalBtn.setOnClickListener {
            //Intent function to call category with message
            val category = "Normal"
            sendMessage(category)
        }

        //Home navigation
        homeBtn.setOnClickListener {
            //Home no need to be done here
        }

        //Note Navigation
        noteBtn.setOnClickListener {
            val intent = Intent(this, RecorderActivity::class.java)
            startActivity(intent)
        }

        //Community Navigation
        communityBtn.setOnClickListener{
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        //Profile Navigation
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}