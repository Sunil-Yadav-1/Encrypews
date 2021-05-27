package com.example.encrypews.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.*


class ResizeImage(val context:Context) {

    fun getCompressedImageUriFromUri(imageUri: Uri, maxSize: Int,intUser:Boolean):Uri?{
        var selectedImage : Bitmap
        try {
            val inputStream:InputStream? = context.contentResolver.openInputStream(imageUri)
             selectedImage = BitmapFactory.decodeStream(inputStream)
            selectedImage = getResizedBitmap(selectedImage,maxSize)
            return getImageUri(selectedImage,intUser)
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }
        return null
    }


    private fun getResizedBitmap(image:Bitmap,maxSize:Int):Bitmap{
        var width = image.width
        var height = image.height

        val bitMapRatio : Float = width.toFloat()/height.toFloat()
        if(bitMapRatio>1){
            width = maxSize
            height = (width/bitMapRatio).toInt()

        }else{
            height = maxSize
            width = (height*bitMapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }
    fun getImageUri(inImage: Bitmap,intUser:Boolean): Uri? {
        val file = if(!intUser){File(context.getExternalFilesDir(null),"/Images")}
                        else{File(context.getExternalFilesDir(null),"/Profile_Pics")}
        if(!file.exists()){
            file.mkdirs()
        }
        val fname = "Image"+System.currentTimeMillis()+".jpg"
        val newFile = File(file,fname)
        if(newFile.exists()){
            newFile.delete()
        }
        try {
//            val bytes = ByteArrayOutputStream()
//
//            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//            val byteArray = bytes.toByteArray()

            val outputStream = FileOutputStream(newFile)
            inImage.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
            outputStream.flush()
            outputStream.close()
            return Uri.fromFile(newFile)
        }catch(e:IOException){
            Log.w("External Storage","Error writing $file",e)
        }
        return null
    }
}