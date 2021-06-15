package com.example.encrypews.offlineDatabase

import androidx.room.TypeConverter
import com.example.encrypews.models.Source

//room database can store only primitive type data structure so we use type converters to store
//some data which are not primitive in the table in form of (string here)


class Converters{
    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name
    }

    @TypeConverter
    fun toSource(name:String):Source{
        return Source(name,name)
    }
}