package com.example.diordna_android1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //Check if User exists
        if(Firebase.auth.currentUser == null){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //Code for UserName
        userNameText.text = "Hi ${ firebaseUserName() }"

        //Code for SignOut
        signOutBtn.setOnClickListener {
            //Firebase Sign Out
            Firebase.auth.signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //Codes for Navigation
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        noteBtn.setOnClickListener {
            val intent = Intent(this, RecorderActivity::class.java)
            startActivity(intent)
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
            //Profile no need to be done here
        }

    }

    //To get the username of the user
    private fun firebaseUserName(): String {
        val user = Firebase.auth.currentUser
        var name = "default"
        user?.let {
            name = user.displayName.toString()
        }
        return name
    }

    override fun onPause() {
        super.onPause()
        //Check if User exists
        if(Firebase.auth.currentUser == null){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        //Check if User exists
        if(Firebase.auth.currentUser == null){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}