package com.example.diordna_android1

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_recorder.*
import java.io.File
import java.io.IOException

class RecorderActivity : AppCompatActivity() {

    private lateinit var mr: MediaRecorder           // Creating mr as MediaRecorder
    private lateinit var path: String                // Path Address
    private var running = false                      // Created to check if the chronometer is running or not
    private var pauseOFFset = 0L                     // It is used to subtract the lag in chronometer
    private var mrRunning = false                    // It will check if mediaPlayer is running or not
    private var statusTextMessage = "Status: "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)

        chronometer.base = SystemClock.elapsedRealtime()

        // If SDK Version is greater than R, then this function is called to get access
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Access granted", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri: Uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

        mr = MediaRecorder()                    // Calling the MediaRecorder function and initialising

        //Buttons are disabled at start
        start.isEnabled = false  //using the synthetic module
        stop.isEnabled = false  //using the synthetic module
        pause.isEnabled = false  //using the synthetic module

        //Checking if record audio permission is given or not
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            //if permission is not given, then requesting permission for write and record audio access
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), 111 )
        }

        //This stage will come if permission is given after creating
        start.isEnabled = true

        // ---------------------- If All Permissions are given ---------------------------- \\

        //code for start recording
        start.setOnClickListener{
            startRecording()
            mrRunning = true
        }

        //Pause or Resume Recording
        pause.setOnClickListener{
            pauseRecording()
        }

        //Code for Stop Recording
        stop.setOnClickListener{
            stopRecording()
            mrRunning = false
        }


        //Codes for Navigation
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        noteBtn.setOnClickListener {
            //Create Note no need to be done here
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    // Function for getting permission which is overridden to make start button enabled
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            start.isEnabled = true        //if record audio permission is given, then enable the start button
    }

    //Start Chronometer
    private fun startChronometer(){
        if(!running){
            chronometer.base = SystemClock.elapsedRealtime() - pauseOFFset
            chronometer.start()
            running = true
        }
    }

    //Pause Chronometer
    private fun pauseChronometer(){
        if(running){
            pauseOFFset = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            running = false
        }
    }

    //Reset Chronometer
    private fun resetChronometer(){
        //elapsedRealtime always sets base to 0
        chronometer.stop()
        chronometer.base = SystemClock.elapsedRealtime()
        pauseOFFset = 0
    }

    //Start Recording function
    @SuppressLint("SetTextI18n")
    private fun startRecording() {

        if (validation()) {
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)                //This is the most important part
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)        //Output Format
            mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)           //Audio Encoder

            path = getRecordingFilePath()                               // Initialising the path to store the recording
            mr.setOutputFile(path)                                          //Output File path

            try {
                mr.prepare()                                                //The recorder to begin capturing and encoding data

            } catch ( e: IOException){
                e.printStackTrace()                                         //printStackTrace() is very useful in diagnosing exceptions. For example, if one out of five methods in your code cause an exception, printStackTrace() will pinpoint the exact line in which the method raised the exception.
                statusText.text = statusTextMessage + getString(R.string.start_recording_ioexception)
            }

            mr.start()                                                      //Begins capturing and encoding data to the file specified with setOutputFile()
            startChronometer()
            statusText.text = statusTextMessage + "Recording"
            start.isEnabled = false
            stop.isEnabled = true
            pause.isEnabled = true
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                noteTitleEditText.isFocusable = true
                noteTitleEditText.isFocusableInTouchMode = true
            }
        }
    }

    //It will Pause all the recording
    @SuppressLint("SetTextI18n")
    private fun pauseRecording() {
        if(pause.text.toString() == "PAUSE"){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                try{
                    mr.pause()
                    pauseChronometer()
                }catch (e:IllegalStateException){
                    statusText.text = statusTextMessage + "Recording can't be paused"
                }

                statusText.text = statusTextMessage + "Recording Paused"
                pause.text = getString(R.string.resume)
                changePauseToResume()
            }
            else{
                pause.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
                pause.text = getString(R.string.upgrade_nougat)
                pause.textSize = 12F
                statusText.text = statusTextMessage + "Only Android Nougat users can use this feature"
            }
        }

        else if(pause.text.toString() == "RESUME"){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.i(TAG, "PauseClicked: resume")
                mr.resume()
                startChronometer()
                changeResumeToPause()
                statusText.text = statusTextMessage + "Recording"
            }
        }
    }

    //Stop Recording function
    @SuppressLint("SetTextI18n")
    private fun stopRecording() {
        mr.stop()                                                       // It stops recording
        start.isEnabled = true
        pause.isEnabled = false
        stop.isEnabled = false
        changeResumeToPause()
        resetChronometer()
        statusText.text = statusTextMessage + "Note Saved"
        noteTitleEditText.text.clear()
    }

    //It will check weather the Note title is empty or not
    private fun validation(): Boolean {
        if(noteTitleEditText.text.isNullOrEmpty()){
            noteTitleEditText?.error = "Title can't be empty"
            return false
        }
        if(noteTitleEditText.text.toString().length > 25){
            noteTitleEditText?.error = "Maximum 25 characters"
            return false
        }
        return true
    }

    //It changes Resume Icon and text to Pause
    private fun changeResumeToPause() {
        val imageRes = resources.getIdentifier("@drawable/red_pause_icon", null, packageName)
        val pauseIcon= resources.getDrawable(imageRes)
        pause.setCompoundDrawablesRelativeWithIntrinsicBounds(null,pauseIcon,null,null)
        pause.text = getString(R.string.pause)
    }

    //It changes Pause Icon and text to Resume
    private fun changePauseToResume() {
        val imageRes = resources.getIdentifier("@drawable/red_resume_icon", null, packageName)
        val resumeIcon: Drawable? = resources.getDrawable(imageRes)
        pause.setCompoundDrawablesRelativeWithIntrinsicBounds(null,resumeIcon,null,null)
        pause.text = getString(R.string.resume)
    }

    // Function for getting the recorded path
    private fun getRecordingFilePath(): String {

        val userName: String = firebaseUserName()
        val labelCategory = radioSelection()
        val filepath = Environment.getExternalStorageDirectory().path       // getting filepath
        val file = File("$filepath/NoiceNotes/${userName}/${labelCategory}")                         // Creating separate folder
        if (!file.exists()) {
            file.mkdirs()                                                   // if file doesn't exist then make directory
        }
        val fileName = noteTitle()
        return file.absolutePath + "/" + fileName + ".mp3"                    //return file path
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

    //It returns the radio Button Selected
    private fun radioSelection(): String {
        val selected = labelRadioBtn.checkedRadioButtonId
        val btn = findViewById<RadioButton>(selected)
        return btn.text.toString()
    }

    //It returns the EditText string
    private fun noteTitle(): String {
        val title: String = noteTitleEditText.text.toString().trim()

        val currentTimestamp = System.currentTimeMillis().toString()

        return if (title != "") {
            title
        } else{
            currentTimestamp
        }

    }

    //On stopping the activity the note will save automatically
    override fun onStop(){
        super.onStop()
        if(mrRunning){
            stopRecording()
        }
    }

}