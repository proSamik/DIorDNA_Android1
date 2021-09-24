package com.example.diordna_android1

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth


        //Register or Sign Up
        signupBtn.setOnClickListener {
            val emailOfUser: String = emailText.text.toString()
            val passwordOfUser = passwordText.text.toString()
            val userName = userNameText.text.toString()

            //function for createUserWithEmailAndPassword
            createUserWithEmailAndPassword(emailOfUser, passwordOfUser, userName)
        }

        //Login or Sign In
        signinBtn.setOnClickListener {
            val emailOfUser: String = emailText.text.toString()
            val passwordOfUser = passwordText.text.toString()

            //function for signInWithEmailAndPassword
            signInWithEmailAndPassword(emailOfUser, passwordOfUser)

        }
    }

    //Override onstart to launch recorderActivity
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Toast.makeText(this,"User account is already created", Toast.LENGTH_SHORT).show()
            updateUI()
        }
    }

    //It will redirect to recordUI
    private fun updateUI(){
        val intent = Intent(applicationContext, RecorderActivity::class.java)
        startActivity(intent)
    }

    //Create email and password
    private fun createUserWithEmailAndPassword(email :String, password : String, userName: String?){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    Toast.makeText(this,"User account created", Toast.LENGTH_SHORT).show()

                    updateProfile(userName)
                    updateUI()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    Toast.makeText(this,"User account not created", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Login with email and password
    private fun signInWithEmailAndPassword(email :String, password : String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    //Update User profile
    private fun updateProfile(userName: String?){
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = "$userName"
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Username added", Toast.LENGTH_SHORT).show()
                }
            }

    }
}