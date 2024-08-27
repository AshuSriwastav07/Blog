package com.TLC_Developer.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.TLC_Developer.Post.OnlyViewProfilePage
import com.TLC_Developer.Post.R
import com.TLC_Developer.Post.UserProfilePageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class functionsManager {
    private val firebaseAuthentication = FirebaseAuth.getInstance()
    private val currentUserID =
        firebaseAuthentication.currentUser?.email.toString()  //in Data Collections email is set as user id


    fun openProfile(context: Context, profileUserID: String) {
        if (currentUserID == profileUserID) {
            val intent = Intent(context, UserProfilePageActivity::class.java)
            context.startActivity(intent)
        } else {
            val intent = Intent(context, OnlyViewProfilePage::class.java)
            intent.putExtra("OpenedProfileUserId", profileUserID)
            context.startActivity(intent)
        }

    }

    fun showDataInOnlyViewProfile(
        context: Context,
        userDocumentID: String,
        userProfileImage: ImageView,
        userProfileName: TextView,
        openInstagram: ImageButton,
        openYoutube: ImageButton,
        openX: ImageButton,
        openFacebook: ImageButton
    ) {
        val db = Firebase.firestore
        val docRef = db.collection("usersDetails").document(userDocumentID)
        docRef.get()
            .addOnSuccessListener { document ->
                val userName = document.getString("userName")

                if (userName.isNullOrEmpty()) {
                    // If userName is null or empty, prompt to complete profile
                    ManageSignUpAndProfile().checkUserProfileIsComplete(context)
                } else {
                    // If userName is not empty, set the text
                    userProfileName.text = userName
                }

                val profileUrl = document.getString("UserprofileUrl")

                loadProfileImagesImage(profileUrl.toString(),userProfileImage)
            }
            .addOnFailureListener { exception ->
                Log.d("functionManagerLogs-showDataInOnlyViewProfile", exception.toString())
            }

        db.collection("usersDetails")
            .document(userDocumentID)
            .get()
            .addOnSuccessListener { document ->

                if (document.getString("InstagramLink").toString() == "") {
                    openInstagram.visibility = View.GONE
                }
                if (document.getString("FacebookLink").toString() == "") {
                    openFacebook.visibility = View.GONE
                }
                if (document.getString("XLink").toString() == "") {
                    openX.visibility = View.GONE
                }
                if (document.getString("YoutubeLink").toString() == "") {
                    openYoutube.visibility = View.GONE
                }

                openInstagram.setOnClickListener {
                    openSocialMedia(
                        document.getString("InstagramLink").toString(), context
                    )
                }
                openFacebook.setOnClickListener {
                    openSocialMedia(
                        document.getString("FacebookLink").toString(), context
                    )
                }
                openX.setOnClickListener {
                    openSocialMedia(
                        document.getString("XLink").toString(),
                        context
                    )
                }
                openYoutube.setOnClickListener {
                    openSocialMedia(
                        document.getString("YoutubeLink").toString(), context
                    )
                }


            }

    }

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

    private fun openSocialMedia(link: String, context: Context) {
        if (link != "") {
            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )
            context.startActivity(urlIntent)
        } else {
            Toast.makeText(context, "Not Available", Toast.LENGTH_LONG).show()

        }
    }

    fun loadProfileImagesImage(imageUrl:String,imageView: ImageView){
        Picasso.get()
            .load(imageUrl) // Use default image if URL is null
            .placeholder(R.mipmap.profileicon) // Placeholder image while loading
            .error(R.mipmap.profileicon) // Error image if loading fails
            .into(imageView)
    }
    fun loadBlogImagesImage(imageUrl:String,imageView: ImageView){
        Picasso.get()
            .load(imageUrl) // Use default image if URL is null
            .placeholder(R.mipmap.noimage) // Placeholder image while loading
            .error(R.mipmap.noimage) // Error image if loading fails
            .into(imageView)
    }

    fun getComments(blogReadDocumentID:String){

        FirebaseFirestore.getInstance().collection("BlogsData").document(blogReadDocumentID)
            .get()
            .addOnSuccessListener {  }

    }


}