package com.example.encrypews.constants

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import com.example.encrypews.models.User
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import com.theartofdev.edmodo.cropper.CropImage

object Constants {
    const val USERIMURL ="user_image_url"
    const val USERS = "Users"
    const val POSTS = "Posts"
    const val PERMISSION_REQUEST_CODE_STORAGE = 1
    const val IMAGE_PICK_REQUEST_CODE = 2
    const val USER_ID = "userId"
    const val USEREP = "user_editProfile"
    const val USERNAMEEP = "user_name_Ep"
    const val BIOEP = "bio_ep"

    const val NAME = "name"
    const val USERNAME = "userName"
    const val USERBIO = "userBio"
    const val USERIMAGE = "userImage"

    const val FOLLOW = "Follow"
    const val FOLLOWERS = "Followers"
    const val FOLLOWING = "Following"


    const val IMAGEURL = "imageUrl"
    const val POSTID = "postId"
    const val  PUBLISHEDBY = "publishedBy"
    const val CAPTION = "caption"






}