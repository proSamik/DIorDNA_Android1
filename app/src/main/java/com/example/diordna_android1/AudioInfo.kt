package com.example.diordna_android1

class AudioInfo {

    var slno: Int? = null
    var Title : String? = null
    var DateModified : String? = null
    var SongPath: String? = null

    constructor(slno: Int?, Title :String?, DateModified :String?, SongPath: String?){
        this.slno = slno
        this.Title = Title
        this.DateModified = DateModified
        this.SongPath = SongPath
    }

}