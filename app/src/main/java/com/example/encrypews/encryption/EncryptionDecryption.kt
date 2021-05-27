package com.example.encrypews.encryption

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.encrypews.BuildConfig
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class EncryptionDecryption {
  private  fun generateKey() : SecretKeySpec{
        val keyString = BuildConfig.KEY_FOR_ENCRYPTION
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = keyString.toByteArray(Charsets.UTF_8)
        digest.update(bytes,0,bytes.size)
        val key = digest.digest()
        return SecretKeySpec(key,"AES")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(text: String): String {
        val key = generateKey()

        val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encBytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(text: String):String{
        val key = generateKey()
        val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE,key)
        val decBytes = Base64.getDecoder().decode(text)
        val decStringBytes = cipher.doFinal(decBytes)
        return  decStringBytes.toString(Charsets.UTF_8)
    }
}