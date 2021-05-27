package com.example.encrypews.models

import android.os.Parcelable
import java.util.*

data class Inbox(
    var msg:String ="",
    var from:String="",
    var name:String="",
    var userName:String="",
    var imageUrl:String="",
    var time: Date = Date(),
    var count:Int = 0,
    var typing:Boolean =false,
    var chatOpen:Boolean=false
){
    constructor():this("","","","","",Date(),0,false,false)
}
