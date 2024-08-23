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
import androidx.core.content.ContextCompat.startActivity
import com.TLC_Developer.Post.OnlyViewProfilePage
import com.TLC_Developer.Post.R
import com.TLC_Developer.Post.UserProfilePageActivity
import com.TLC_Developer.Post.WriteBlogPage
import com.TLC_Developer.Post.databinding.ActivityEditBlogBinding
import com.TLC_Developer.Post.databinding.ActivityWriteBlogPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class function {

    private val firestoreDB = Firebase.firestore
    private val currentUserID=FirebaseAuth.getInstance().currentUser?.uid

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


    fun socialMediaLinks(context: Context,firestore: FirebaseFirestore,userID:String,Insta:ImageButton,facebook:ImageButton,x:ImageButton,youtube:ImageButton,){
//        var links:ArrayList<String> = arrayListOf()

        firestore.collection("usersDetails")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->

                Insta.setOnClickListener{openSociaMedia(document.getString("InstagramLink").toString(),context)}
                facebook.setOnClickListener{openSociaMedia(document.getString("FacebookLink").toString(),context)}
                x.setOnClickListener{openSociaMedia(document.getString("XLink").toString(),context)}
                youtube.setOnClickListener{openSociaMedia(document.getString("YoutubeLink").toString(),context)}


                /*links.add(document.getString("InstagramLink").toString())
                links.add(document.getString("FacebookLink").toString())
                links.add(document.getString("XLink").toString())
                links.add(document.getString("YoutubeLink").toString())*/
            }

    }

    fun getAndSetUserDetails(firestore: FirebaseFirestore,userID:String,userName:TextView,userProfile:ImageView){
        firestore.collection("usersDetails")
            .document(userID)
            .get()
            .addOnSuccessListener { result ->
//                Log.d("OpenProfilePageLogs",result.getString("userName").toString())
//                Log.d("OpenProfilePageLogs",result.getString("UserprofileUrl").toString())

                userName.text=result.getString("userName").toString()
                Picasso.get()
                    .load(result.getString("UserprofileUrl").toString())
                    .error(R.mipmap.profileicon)
                    .placeholder(R.mipmap.profileicon)
                    .into(userProfile)
            }
            .addOnFailureListener { exception ->
                Log.d("OpenProfileLogs", "Error getting documents: ", exception)
            }


    }

    private fun openSociaMedia(link:String,context: Context){
        if(link==""){
            Toast.makeText(context,"Not Available",Toast.LENGTH_LONG).show()
        }else{
            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )
            context.startActivity(urlIntent)
        }

    }

    fun openProfileSection(userID: String,context: Context){
        val currentUserID= FirebaseAuth.getInstance().uid
        if(userID==currentUserID) {
            val intent = Intent(context, UserProfilePageActivity::class.java)
            context.startActivity(intent)
        }else{
            val intent = Intent(context, OnlyViewProfilePage::class.java)
            intent.putExtra("OpenedProfileUserId",userID)
            context.startActivity(intent)
        }
    }

    fun getUserSpecificData(userID: String,dataName:String, callback: (String) -> Unit) {
        firestoreDB.collection("usersDetails")
            .document(userID)
            .get()
            .addOnSuccessListener { result ->
                val userName = result.getString(dataName).toString()
                callback(userName)  // Use the callback to return the result
            }
            .addOnFailureListener { exception ->
                Log.d("OpenProfileLogs", "Error getting documents: ", exception)
                callback("")  // Return an empty string or handle the error as needed
            }
    }

    fun UpdateUserName(userName:String){

        firestoreDB.collection("BlogsData")
            .whereEqualTo("userID", currentUserID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Iterate over each document and update the writerName field
                for (document in querySnapshot) {
                    val documentID = document.id

                    // Update the writerName for each document
                    firestoreDB.collection("BlogsData").document(documentID)
                        .update("writerName", userName)
                        .addOnSuccessListener {
                            Log.d("OpenProfileLogs", "Writer name updated successfully for document: $documentID")
                        }
                        .addOnFailureListener { e ->
                            Log.e("OpenProfileLogs", "Error updating document: $documentID", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("OpenProfileLogs", "Error querying documents", e)
            }
    }

    fun currentUserPersonalDetails(){

    }





}