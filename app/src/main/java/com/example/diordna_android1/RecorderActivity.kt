package com.example.diordna_android1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_recorder.*
import java.io.File
import java.io.IOException

class RecorderActivity : AppCompatActivity() {

    private lateinit var mr: MediaRecorder           // creating mr as MediaRecorder
    private var defaultCount = 0                    // Setting default count if no text given in note title
    private lateinit var path: String

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
        play.isEnabled = false  //using the synthetic module

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
            stop.isEnabled = true
            start.isEnabled = false
        }

        //Code for Stop Recording
        stop.setOnClickListener{
            mr.stop()                                                       // It stops recording
            start.isEnabled = true
            stop.isEnabled = false
            play.isEnabled = true
        }

        //Play Recording
        play.setOnClickListener{
            var mp = MediaPlayer()                                          // Initialising
            mp.setDataSource(path)                                          // Calling from DataSource
            mp.prepare()                                                    // The recorder to begin capturing and encoding data
            mp.start()                                                      // Begins capturing and encoding data to the file specified with setOutputFile()
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
        Toast.makeText(this, btn.text, Toast.LENGTH_SHORT).show()
        return btn.text.toString()
    }

    //It returns the EditText string
    private fun noteTitle(): String? {
        val title: String? = noteTitleEditText.text.toString().trim()

        if (title ==null){
            defaultCount += 1
            return "default${defaultCount}"
        }

        return if (title != "") {
            title
        } else{
            defaultCount += 1
            "default${defaultCount}"
        }

    }
}