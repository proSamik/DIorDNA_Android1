package com.example.diordna_android1

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaRecorder
import android.media.ToneGenerator.MAX_VOLUME
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.diordna_android1.R.drawable.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_recorder.*
import java.io.File
import java.io.IOException
import kotlin.math.ln

class RecorderActivity : AppCompatActivity() {

    private lateinit var mr: MediaRecorder           // creating mr as MediaRecorder
    private lateinit var path: String               // Path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)


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

            mr.setAudioSource(MediaRecorder.AudioSource.MIC)                //This is the most important part
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)        //Output Format
            mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)           //Audio Encoder

            path = getRecordingFilePath()                               // Initialising the path to store the recording
            mr.setOutputFile(path)                                          //Output File path

            try {
                mr.prepare()                                                //The recorder to begin capturing and encoding data

            } catch ( e: IOException){
                e.printStackTrace()                                         //printStackTrace() is very useful in diagnosing exceptions. For example, if one out of five methods in your code cause an exception, printStackTrace() will pinpoint the exact line in which the method raised the exception.
                Toast.makeText(this, "Recording is not saved", Toast.LENGTH_SHORT ).show()
                return@setOnClickListener
            }

            mr.start()                                                      //Begins capturing and encoding data to the file specified with setOutputFile()
            Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show()
            start.isEnabled = false
            stop.isEnabled = true
            pause.isEnabled = true
        }

        //Play Recording
        pause.setOnClickListener{

            if(pause.text.toString() == "PAUSE"){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try{
                        mr.pause()
                    }catch (e:IllegalStateException){
                        Log.i(TAG, "PauseClicked: Can't pause")
                    }
                    pause.text = getString(R.string.resume)
                    changePauseToResume()
                }
                else{
                    pause.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
                    pause.text = getString(R.string.upgrade_nougat)
                    pause.textSize = 12F
                }

            }

            else if(pause.text.toString() == "RESUME"){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.i(TAG, "PauseClicked: resume")
                    mr.resume()
                    changeResumeToPause()
                }
            }

        }

        //Code for Stop Recording
        stop.setOnClickListener{
            mr.stop()                                                       // It stops recording
            Toast.makeText(this, "Recording Saved ${pause.text}", Toast.LENGTH_SHORT).show()
            start.isEnabled = true
            pause.isEnabled = false
            stop.isEnabled = false
            changeResumeToPause()

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

    //It changes Resume Icon and text to Pause
    private fun changeResumeToPause() {
        val imageRes = resources.getIdentifier("@drawable/red_pause_icon", null, packageName)
        val pauseIcon= resources.getDrawable(imageRes)
        pause.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
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
        return file.absolutePath + "/" + "$fileName" + ".mp3"                    //return file path
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
    private fun noteTitle(): String? {
        val title: String? = noteTitleEditText.text.toString().trim()

        val currentTimestamp = System.currentTimeMillis().toString()

        if (title ==null){
            return "$currentTimestamp"
        }

        return if (title != "") {
            title
        } else{
            "$currentTimestamp"
        }

    }


}