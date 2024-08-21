package com.TLC_Developer.functions

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.TLC_Developer.Post.R
import com.TLC_Developer.Post.databinding.ActivityEditBlogBinding
import com.TLC_Developer.Post.databinding.ActivityWriteBlogPageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class function {

    // Convert String to Date
    fun convertStringToDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("dd/M/yyyy HH:mm", Locale.getDefault())
            format.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }


}