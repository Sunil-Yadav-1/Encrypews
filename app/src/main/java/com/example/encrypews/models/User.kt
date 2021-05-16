package com.example.encrypews.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    var id : String= "",
    var name:String ="",
    var userName : String= "",
    var userImage : String = "",
    var userBio : String = "",
    var email:String="",
    ) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,


    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(userName)
        dest.writeString(userImage)
        dest.writeString(userBio)
        dest.writeString(email)

    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}