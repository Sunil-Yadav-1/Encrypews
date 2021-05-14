package com.example.encrypews.models

import android.os.Parcel
import android.os.Parcelable

data class Post(
    val imageUrl: String="",
    val postId:String="",
    val caption:String ="",
    val publishedBy : String =""
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
       return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(imageUrl)
        dest.writeString(postId)
        dest.writeString(caption)
        dest.writeString(publishedBy)
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }

}
