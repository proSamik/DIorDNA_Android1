package com.example.diordna_android1

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator.MAX_VOLUME
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_category.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ln

class CategoryActivity : AppCompatActivity() {

    lateinit var message: String
    var mp: MediaPlayer? = null
    var adapter : MyAudioAdapter? = null
    companion object{
        private const val TAG = "CategoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Get the Intent that started this activity and extract the string
        message = intent.getStringExtra("EXTRA_MESSAGE").toString()
        category.text = message      // Changing this button's text

        if(ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //if permission is not given, then requesting permission for write and record audio access
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 111 )
        }
        else
            //Toast.makeText(this,"Perfect Till Now", Toast.LENGTH_SHORT).show()
            loadAudioFiles()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadAudioFiles()
    }

    //Creating Custom Adapter for listView
    inner class MyAudioAdapter : BaseAdapter {

        var myListAudio = ArrayList<AudioInfo>()

        constructor(myListSong: ArrayList<AudioInfo>) : super(){
            this.myListAudio = myListSong
        }
        //How many times to operate
        override fun getCount(): Int {
            return myListAudio.size
        }

        //It will get the item
        override fun getItem(position: Int): Any {
            return myListAudio[position]
        }

        //It will get the item ID
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        //All the view related function written here
        @SuppressLint("SetTextI18n", "InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myview = layoutInflater.inflate(R.layout.layout_audio_list, null)

            val audio = myListAudio[position]           //Creating Object of each element

            //Stop Button
            val stopBtn = myview.findViewById<Button>(R.id.stopButton)
            stopBtn.isEnabled = false

            //Delete button
            val deleteBtn = myview.findViewById<Button>(R.id.deleteButton)

            //Share Button
            val shareBtn = myview.findViewById<ImageButton>(R.id.shareButton)

            //URL TextView
            val urlText = myview.findViewById<TextView>(R.id.urlTextView)

            //Copy Button
            val copyBtn = myview.findViewById<ImageButton>(R.id.copyButton)

            //Serial No. of the song
            val serialNo = myview.findViewById<TextView>(R.id.slno)
            serialNo.text = audio.slno.toString() + "."

            //Title of the song
            val textTitle = myview.findViewById<TextView>(R.id.noteTitle)
            textTitle.text = audio.Title

            //Date of the song
            val textDate = myview.findViewById<TextView>(R.id.lastModifiedDate)
            textDate.text = audio.DateModified

            //Play Button and their functions
            val playBtn = myview.findViewById<Button>(R.id.playButton)
            playBtn.setOnClickListener {
                //When user wants to resume
                if(playBtn.text.toString() == "PAUSE"){
                        try {
                            mp!!.pause()
                        }catch (e:IllegalStateException){
                            Toast.makeText(applicationContext,
                                "Can't be paused",
                                Toast.LENGTH_SHORT).show()
                        }
                    //changePauseToResume()
                    val imageRes = resources.getIdentifier("@drawable/play_icon", null, packageName)
                    val resumeIcon: Drawable? = resources.getDrawable(imageRes)
                    playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,resumeIcon,null,null)
                    playBtn.text = "RESUME"

                }

                //when user wants to pause
                else if(playBtn.text.toString() == "RESUME") {
                        mp!!.start()
                    //changeResumeToPause()
                    val imageRes = resources.getIdentifier("@drawable/pause_blue_icon", null, packageName)
                    val pauseIcon= resources.getDrawable(imageRes)
                    playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,pauseIcon,null,null)
                    playBtn.text = "PAUSE"
                }

                //When user wants to start
                else{
                    mp = MediaPlayer()
                    try{
                        mp!!.setDataSource(audio.audioPath)
                        mp!!.prepare()
                        mp!!.setVolume(2*volumeRange(),2*volumeRange())
                        mp!!.start()
                        stopBtn.isEnabled = true

                        //changeResumeToPause()
                        val imageRes = resources.getIdentifier("@drawable/pause_blue_icon", null, packageName)
                        val pauseIcon= resources.getDrawable(imageRes)
                        playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,pauseIcon,null,null)
                        playBtn.text = "PAUSE"

                        //disable the delete button
                        deleteBtn.isEnabled = false

                    } catch (e: Exception) {
                        Toast.makeText(applicationContext,
                            "Audio Can't be played",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                mp!!.setOnCompletionListener {
                    //changePauseToStart()
                    val imageRes = resources.getIdentifier("@drawable/play_icon", null, packageName)
                    val resumeIcon: Drawable? = resources.getDrawable(imageRes)
                    playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,resumeIcon,null,null)
                    playBtn.text = "START"

                    //Enable Delete button after complete
                    deleteBtn.isEnabled = true
                }
            }

            //Stop Button function
            stopBtn.setOnClickListener {
                mp!!.stop()
                //changePauseToStart()
                val imageRes = resources.getIdentifier("@drawable/play_icon", null, packageName)
                val resumeIcon: Drawable? = resources.getDrawable(imageRes)
                playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(null,resumeIcon,null,null)
                playBtn.text = "START"

                deleteBtn.isEnabled = true
            }

            //Delete Button function
            deleteBtn.setOnClickListener {
                playBtn.isEnabled = false
                stopBtn.isEnabled = false
                deleteAudioFiles(audio.slno!!)

                //It will refresh the Activity
                recreate()
            }

            //Copy Button function
            copyBtn.setOnClickListener {

                if (urlText.text.toString() != "URL") {
                    val textToCopy = urlText.text

                    val clipboardManager =
                            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("text", textToCopy)

                    clipboardManager.setPrimaryClip(clipData)

                    Toast.makeText(applicationContext, "URL Copied", Toast.LENGTH_SHORT).show()
                }
            }

            //Share Button function
            shareBtn.setOnClickListener{
                //Storage Bucket
                val storage = Firebase.storage("gs://diordna-android1.appspot.com")
                //Getting current user
                val user = Firebase.auth.currentUser
                //Getting Refrence to upload the file
                val audioRefrence = storage.reference.child("CommunityNotes/${user!!.email.toString()}/Recordings/${audio.Title}.mp3")

                copyBtn.isEnabled = false
                urlText.text = getString(R.string.uploading)

                var file = Uri.fromFile(File(audio.audioPath!!))

                //After getting file uploading it in firebase
                audioRefrence.putFile(file)
                    .addOnSuccessListener {
//                        Toast.makeText(applicationContext,"File Uploaded", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{ p0->
                        Toast.makeText(applicationContext,p0.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener { p0 ->
                        if(p0.isSuccessful){
                            audioRefrence.downloadUrl.addOnSuccessListener {
                                copyBtn.isEnabled = true
                                urlText.text = it.toString()
                            }
                        }
                    }
            }

            return myview
        }
    }

    //It will load audio files
    private fun loadAudioFiles() {

        val userName: String = firebaseUserName()
        val labelCategory = message
        Log.i(TAG, "loadAudioFiles: LabelCategory: $labelCategory")
        val filepath = Environment.getExternalStorageDirectory().path       // getting filepath
        val file = File("$filepath/NoiceNotes/${userName}/${labelCategory}")
        if (!file.exists()) {
            file.mkdirs()                                                   // if file doesn't exist then make directory
        }
        val audioList = ArrayList<AudioInfo>()
        val audioFile = file.listFiles()!!

        //This i will iterate over the files
        var i = 0
        while(i < file.listFiles()!!.size){
            audioList.add(
                AudioInfo(
                    i+1,
                    audioFile[i].nameWithoutExtension,
                    convertLongToTime(audioFile[i].lastModified()),
                    audioFile[i].path
                )
            )
            i += 1
        }

        adapter = MyAudioAdapter(audioList)
        audioView.adapter = adapter
    }

    //It will Delete Audio files
    private fun deleteAudioFiles(serialNumber: Int) {

        val userName: String = firebaseUserName()
        val labelCategory = message
        Log.i(TAG, "loadAudioFiles: LabelCategory: $labelCategory")
        val filepath = Environment.getExternalStorageDirectory().path       // getting filepath
        val file = File("$filepath/NoiceNotes/${userName}/${labelCategory}")
        if (!file.exists()) {
            file.mkdirs()                                                   // if file doesn't exist then make directory
        }
        val audioFile = file.listFiles()!!

        var i = 0
        while(i < file.listFiles()!!.size){

            //if condition is reached then delete the audio file
                if (serialNumber-1 == i){
                    audioFile[i].delete()
                    Log.i(TAG, "loadAudioFiles: ${audioFile[i].nameWithoutExtension} is deleted ")
                }
            i += 1
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

    //Convert Long Int to Time and return String
    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return format.format(date)
    }

    //It returns the current Volume to media player
    private fun volumeRange(): Float {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val soundVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        // Get the device music maximum volume level
        return ( (1 - ln( (MAX_VOLUME.toFloat() - soundVolume.toFloat()))) / ln(MAX_VOLUME.toFloat()) )
    }
}