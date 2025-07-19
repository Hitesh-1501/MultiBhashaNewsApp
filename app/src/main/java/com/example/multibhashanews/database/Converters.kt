package com.example.multibhashanews.database

import androidx.room.TypeConverter
import com.example.multibhashanews.model.Source

class Converters{
    // Converts a Source object into a String (Room will store this in the DB).
    @TypeConverter
    fun fromSource(source: Source):String?{
        return source.name
    }
    // Converts a String (retrieved from DB) back into a Source object.
    @TypeConverter
    fun toSource(name:String):Source{
        return Source(name,name)
    }
}