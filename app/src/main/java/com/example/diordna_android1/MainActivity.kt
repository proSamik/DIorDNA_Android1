package com.example.diordna_android1

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.Manifest  // This is important
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings


class MainActivity : AppCompatActivity() {

    lateinit var mr: MediaRecorder           // creating mr as MediaRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        var path = getRecordingFilePath()       // Initialising the path to store the recording
        mr = MediaRecorder()                    // Calling the MediaRecorder function and initialising

        //Buttons are disabled at start
        button1.isEnabled = false  //using the synthetic module
        button2.isEnabled = false  //using the synthetic module
        button3.isEnabled = false  //using the synthetic module

        //Checking if record audio permission is given or not
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            //if permission is not given, then requesting permission for write and record audio access
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), 111 )
        }

        //This stage will come if permission is given after creating
        button1.isEnabled = true


        // ----------------- If All Permissions are given ---------------------------- \\

        //code for start recording
        button1.setOnClickListener{
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)                //This is the most important part
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)        //Output Format
            mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)           //Audio Encoder

            mr.setOutputFile(path)                                          //Output File path

            try {
                mr.prepare()                                                //The recorder to begin capturing and encoding data
            } catch ( e: IOException){
                e.printStackTrace()                                         //printStackTrace() is very useful in diagnosing exceptions. For example, if one out of five methods in your code cause an exception, printStackTrace() will pinpoint the exact line in which the method raised the exception.
                Toast.makeText(this, "Recording is not saved",Toast.LENGTH_SHORT ).show()
                return@setOnClickListener
            }

            mr.start()                                                      //Begins capturing and encoding data to the file specified with setOutputFile()
            button2.isEnabled = true
            button1.isEnabled = false
        }

        //Code for Stop Recording
        button2.setOnClickListener{
            mr.stop()                                                       // It stops recording
            button1.isEnabled = true
            button2.isEnabled = false
            button3.isEnabled = true
        }

        //Play Recording
        button3.setOnClickListener{
            var mp = MediaPlayer()                                          // Initialising
            mp.setDataSource(path)                                          // Calling from DataSource
            mp.prepare()                                                    // The recorder to begin capturing and encoding data
            mp.start()                                                      // Begins capturing and encoding data to the file specified with setOutputFile()
        }

    }

    // Function for getting the recorded path
    private fun getRecordingFilePath(): String? {
        val filepath = Environment.getExternalStorageDirectory().path       // getting filepath
        val file = File("$filepath/Music")                         // Creating separate folder
        if (!file.exists()) {
            file.mkdirs()                                                   // if file doesn't exist then make directory
        }
        return file.absolutePath + "/" + "test" + ".mp3"                    //return file path
    }

    // Function for getting permission which is overridden to make start button enabled
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            button1.isEnabled = true        //if record audio permission is given, then enable the start button
    }
}