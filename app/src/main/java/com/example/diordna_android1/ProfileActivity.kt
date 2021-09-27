package com.example.diordna_android1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
}