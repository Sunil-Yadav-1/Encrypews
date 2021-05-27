package com.example.encrypews.models

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.encrypews.Utils.formatAsHeader
import java.util.*

interface ChatEvent{
    val sentAt : Date
}

data class Messages (

    var msg:String = "",
    var msgId: String= "",
    var senderId : String = "",
    val status : Int = 1,
    var type:String = "text",
    var liked:Boolean = false,
    override val sentAt: Date = Date()
        ):ChatEvent{
            constructor():this("","","",1,"text",false)
        }


data class DateHeader(
    override val sentAt:Date = Date(),val context: Context
):ChatEvent{
        @RequiresApi(Build.VERSION_CODES.N)
        val date =sentAt.formatAsHeader(context)
}