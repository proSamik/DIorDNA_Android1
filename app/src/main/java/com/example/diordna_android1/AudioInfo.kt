package com.example.diordna_android1

class AudioInfo {

    //To Store the serial no. of the audio
    var slno: Int? = null

    // Store the title of the audio from the file name
    var Title : String? = null

    //Store the last date it was modified, it is beneficial if a file is overwritten
    var DateModified : String? = null

    //It will store the audio Path
    var audioPath: String? = null

    constructor(slno: Int?, Title :String?, DateModified :String?, SongPath: String?){
        this.slno = slno
        this.Title = Title
        this.DateModified = DateModified
        this.audioPath = SongPath
    }

}