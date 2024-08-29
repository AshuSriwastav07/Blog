package com.TLC_Developer.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.TLC_Developer.Post.OnlyViewProfilePage
import com.TLC_Developer.Post.R
import com.TLC_Developer.Post.UserProfilePageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class functionsManager {

    // Initialize Firebase authentication to get current user's information
    private val firebaseAuthentication = FirebaseAuth.getInstance()

    // Get the current user's email, which is used as their ID in the database
    private val currentUserID =
        firebaseAuthentication.currentUser?.email.toString()  // In data collections, email is set as user ID

    // Function to open the profile page
    fun openProfile(context: Context, profileUserID: String) {
        // If the current user is viewing their own profile, open their full profile page
        if (currentUserID == profileUserID) {
            val intent = Intent(context, UserProfilePageActivity::class.java)
            context.startActivity(intent)
        } else {
            // If the user is viewing someone else's profile, open a different page for that
            val intent = Intent(context, OnlyViewProfilePage::class.java)
            intent.putExtra("OpenedProfileUserId", profileUserID)
            context.startActivity(intent)
        }
    }

    // Function to display a user's data on their profile page
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
        val db = Firebase.firestore // Access Firestore database
        val docRef = db.collection("usersDetails").document(userDocumentID) // Get the user's document

        // Get the user's data from Firestore
        docRef.get()
            .addOnSuccessListener { document ->
                val userName = document.getString("userName") // Get the user's name

                // If the user has not set up their profile, prompt them to complete it
                if (userName.isNullOrEmpty()) {
                    ManageSignUpAndProfile().checkUserProfileIsComplete(context)
                } else {
                    // If the user has a name, display it
                    userProfileName.text = userName
                }

                val profileUrl = document.getString("UserprofileUrl") // Get the profile image URL
                loadProfileImagesImage(profileUrl.toString(), userProfileImage) // Load and display the image
            }
            .addOnFailureListener { exception ->
                Log.d("functionManagerLogs-showDataInOnlyViewProfile", exception.toString()) // Log any errors
            }

        // Get more user details from Firestore
        db.collection("usersDetails")
            .document(userDocumentID)
            .get()
            .addOnSuccessListener { document ->

                // Check if the user has links for social media, if not hide the buttons
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

                // Set click actions for social media buttons
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

    // Function to convert a date from a string format to a Date object
    fun convertStringToDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("dd/M/yyyy HH:mm", Locale.getDefault()) // Set date format
            format.parse(dateString) // Convert string to Date
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    // Function to open a social media link in a web browser
    private fun openSocialMedia(link: String, context: Context) {
        // Check if the link is valid and for a supported social media site
        if (link != "" && link.contains("https://x.com/") || link.contains("https://www.instagram.com/") || link.contains("https://www.facebook.com/") || link.contains("https://www.youtube.com/")) {
            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link) // Create an intent to open the link
            )
            context.startActivity(urlIntent) // Start the intent to open the link
        } else {
            // If the link is not valid, show a message
            Toast.makeText(context, "Not Available", Toast.LENGTH_LONG).show()
        }
    }

    // Function to load and display a profile image using Picasso
    fun loadProfileImagesImage(imageUrl: String, imageView: ImageView) {
        Picasso.get()
            .load(imageUrl) // Load the image from the URL
            .placeholder(R.mipmap.profileicon) // Show this image while the main image is loading
            .error(R.mipmap.profileicon) // Show this image if there's an error loading the main image
            .into(imageView) // Display the image in the ImageView
    }

    // Function to load and display a background image for a blog
    fun loadBlogBGImages(imageUrl: String, imageView: ImageView) {
        Picasso.get()
            .load(imageUrl) // Load the image from the URL
            .placeholder(R.mipmap.noimage) // Show this image while the main image is loading
            .error(R.mipmap.noimage) // Show this image if there's an error loading the main image
            .into(imageView) // Display the image in the ImageView
    }

    // Function to get and display comments on a blog
    fun getComments(
        context: Context,
        blogReadDocumentID: String,
        listview: ListView,
        enterCommentTextBox: EditText,
        submitButton: Button,
        userName: String
    ) {
        val commentsList: ArrayList<String> = arrayListOf() // Create a list to store comments
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1, commentsList
        )

        // Set the adapter for the ListView to display comments
        listview.adapter = arrayAdapter

        // Listen for real-time updates to the comments in Firestore
        FirebaseFirestore.getInstance().collection("BlogsData").document(blogReadDocumentID)
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Log.w("commentsList", "Listen failed.", e) // Log any errors
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.get("comments") != null) {
                        commentsList.clear() // Clear the old list of comments
                        commentsList.addAll(documentSnapshot.get("comments") as ArrayList<String>) // Add the new comments

                        // Notify the adapter that the data has changed so the list updates
                        arrayAdapter.notifyDataSetChanged()

                        Log.d("commentsList", "Number of comments: ${commentsList.size}")
                        if (commentsList.isNotEmpty()) {
                            Log.d("commentsList", commentsList[0])
                        }
                    }
                } else {
                    Log.d("commentsList", "Current data: null") // Log if no data is found
                }
            }

        // Handle the event when the submit button is clicked to add a new comment
        submitButton.setOnClickListener {
            addComments(context, enterCommentTextBox, blogReadDocumentID, commentsList, userName)
        }
    }

    // Function to add a new comment to a blog
    fun addComments(
        context: Context,
        typedComment: EditText,
        blogDocumentId: String,
        allPreviousComments: ArrayList<String>,
        commentUserName: String
    ) {
        val comment: String = typedComment.text.toString() // Get the comment text
        allPreviousComments.add("$comment by $commentUserName") // Add the comment with the username to the list
        Log.d("commentsList", typedComment.text.toString())

        // Update Firestore with the new comment
        FirebaseFirestore.getInstance().collection("BlogsData").document(blogDocumentId)
            .update("comments", FieldValue.arrayUnion("$comment by $commentUserName"))
            .addOnSuccessListener {
                Toast.makeText(context, "Comment Done", Toast.LENGTH_LONG).show() // Show a success message
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Some Issue Faced", Toast.LENGTH_LONG).show() // Show an error message
            }
    }
}
