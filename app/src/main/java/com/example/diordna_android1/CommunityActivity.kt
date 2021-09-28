package com.example.diordna_android1

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_community.importantBtn
import kotlinx.android.synthetic.main.activity_community.normalBtn
import kotlinx.android.synthetic.main.activity_community.urgentBtn
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.communityBtn
import kotlinx.android.synthetic.main.activity_main.homeBtn
import kotlinx.android.synthetic.main.activity_main.noteBtn
import kotlinx.android.synthetic.main.activity_main.profileBtn
import kotlin.math.ln

class CommunityActivity : AppCompatActivity() {

    var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        //It will open new Intent with that category
        fun sendMessage(category: String){
            val intent = Intent(this, CategoryActivity::class.java).apply {
                putExtra("EXTRA_MESSAGE", category) //category is message here to next intent
            }
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

        //Play Button function
        playButton.setOnClickListener {
            //When user wants to resume
            if(playButton.text.toString() == "PAUSE"){
                try {
                    mp!!.pause()
                }catch (e:IllegalStateException){
                    Toast.makeText(applicationContext,
                        "Can't be paused",
                        Toast.LENGTH_SHORT).show()
                }
                changePauseToResume()
            }

            //when user wants to pause
            else if(playButton.text.toString() == "RESUME") {
                mp!!.start()
                changeResumeToPause()
            }

            //When user wants to start
            else{
                mp = MediaPlayer()
                try {
                    if (validation()){
                    mp!!.setDataSource(urlEditText.text.toString().trim())
                    mp!!.prepare()
                    mp!!.setVolume(2 * volumeRange(), 2 * volumeRange())
                    mp!!.start()

                    changeResumeToPause()
                }
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            "Audio Can't be played",
                            Toast.LENGTH_SHORT
                        ).show()
                    Log.i(TAG, "Excpetion: $")
                    }

            }

            //When the Song gets ended
            mp!!.setOnCompletionListener {
                changePauseToStart()
            }
        }

        //Stop Button function
        stopButton.setOnClickListener {
            mp!!.stop()
            changePauseToStart()
        }

        //Home navigation
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Note Navigation
        noteBtn.setOnClickListener {
            val intent = Intent(this, RecorderActivity::class.java)
            startActivity(intent)
        }

        //Community Navigation
        communityBtn.setOnClickListener{
            //Community no need to be done here
        }

        //Profile Navigation
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    //it will Change Pause icon to Resume
    private fun changePauseToResume() {
        val imageRes = resources.getIdentifier("@drawable/play_icon", null, packageName)
        val resumeIcon: Drawable? = resources.getDrawable(imageRes)
        playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null,resumeIcon,null,null)
        playButton.text = "RESUME"
    }

    //Change Pause Icon to Start
    private fun changePauseToStart() {
        val imageRes = resources.getIdentifier("@drawable/play_icon", null, packageName)
        val resumeIcon: Drawable? = resources.getDrawable(imageRes)
        playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null,resumeIcon,null,null)
        playButton.text = "START"
    }

    //Change Resume Icon to pause
    private fun changeResumeToPause() {
        val imageRes = resources.getIdentifier(
            "@drawable/pause_blue_icon",
            null,
            packageName
        )
        val pauseIcon = resources.getDrawable(imageRes)
        playButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            pauseIcon,
            null,
            null
        )
        playButton.text = "PAUSE"
    }

    //It will check weather the URL is empty or not
    private fun validation(): Boolean {
        if(urlEditText.text.isNullOrEmpty()){
            urlEditText?.error = "URL can't be empty"
            return false
        }
        return true
    }

    //It returns the current Volume to media player
    private fun volumeRange(): Float {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val soundVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        // Get the device music maximum volume level
        return ( (1 - ln( (ToneGenerator.MAX_VOLUME.toFloat() - soundVolume.toFloat()))) / ln(
            ToneGenerator.MAX_VOLUME.toFloat()) )
    }

    override fun onPause() {
        super.onPause()
        mp!!.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mp!!.stop()
        changePauseToStart()
    }
}