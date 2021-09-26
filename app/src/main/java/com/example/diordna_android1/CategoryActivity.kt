package com.example.diordna_android1

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.ToneGenerator.MAX_VOLUME
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

    inner class MyAudioAdapter : BaseAdapter {

        var myListAudio = ArrayList<AudioInfo>()

        constructor(myListSong: ArrayList<AudioInfo>) : super(){
            this.myListAudio = myListSong
        }
        //How many times to operate
        override fun getCount(): Int {
            return myListAudio.size
        }

        override fun getItem(position: Int): Any {
            return myListAudio[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("SetTextI18n", "InflateParams", "ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myview = layoutInflater.inflate(R.layout.layout_audio_list, null)

            val audio = myListAudio[position]
            val textTitle = myview.findViewById<TextView>(R.id.noteTitle)
            textTitle.text = audio.Title

            val textDate = myview.findViewById<TextView>(R.id.lastModifiedDate)
            textDate.text = audio.DateModified

            val playBtn = myview.findViewById<ImageButton>(R.id.playButton)
            playBtn.setOnClickListener {
//                if(playBtn.text == "STOP"){
//                    mp!!.stop()
//                    playBtn.text = "PLAY"
//                }
//                else{
                    mp = MediaPlayer()
                    try{
                        mp!!.setDataSource(audio.SongPath)
                        mp!!.prepare()
                        mp!!.setVolume(volumeRange(),volumeRange())
                        mp!!.start()
//                        playBtn.text = "STOP"
                    } catch (e: Exception) {}
//                }
            }

            val deleteBtn = myview.findViewById<ImageButton>(R.id.deleteButton)
            deleteBtn.setOnClickListener {
                //TODO: Make a function to delete
            }
            return myview
        }
    }

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
        val audioFile = file.listFiles()

        var i = 0
        Log.i(TAG, "loadAudioFiles: ${file.listFiles().size} ")

        while(i < file.listFiles().size){
            audioList.add(
                AudioInfo(
                    audioFile[i].nameWithoutExtension,
                    convertLongToTime(audioFile[i].lastModified()),
                    audioFile[i].path
                )
            )
            Log.i(TAG, "loadAudioFiles: ${audioFile[i].nameWithoutExtension} ")
            //songFile[i].delete()
            i += 1
        }

        adapter = MyAudioAdapter(audioList)
        val listViewOfAudio: ListView = findViewById<ListView>(R.id.audioView)
        listViewOfAudio.adapter = adapter
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

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
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