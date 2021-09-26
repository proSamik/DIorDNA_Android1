package com.example.diordna_android1

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        //Login or Sign In
        signinBtn.setOnClickListener {
            val emailOfUser: String = emailText.text.toString()
            val passwordOfUser = passwordText.text.toString()

            if(validation()){
                //function for signInWithEmailAndPassword
                signInWithEmailAndPassword(emailOfUser, passwordOfUser)
            }


        }

        //Already Have an account Clicked
        register.setOnClickListener{
            val intent = Intent(applicationContext,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //Override onstart to launch recorderActivity
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //Toast.makeText(this,"User account is already created", Toast.LENGTH_SHORT).show()
            updateUI()
        }
    }

    // It will verify that if all fields are filled are not
    private fun validation(): Boolean {

        var flag = true

        if(emailText.text.isNullOrEmpty()){
            emailText?.error = "Enter email"
            flag = false
        }
        if(passwordText.text.isNullOrEmpty()){
            passwordText?.error = "Enter password"
            flag = false
        }

        return flag
    }

    //It will redirect to MainActivity
    private fun updateUI(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
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
                    Toast.makeText(baseContext, "Incorrect email/password",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
}