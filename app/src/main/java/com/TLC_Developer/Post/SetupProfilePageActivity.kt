package com.TLC_Developer.Post

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SetupProfilePageActivity : AppCompatActivity() {

    private lateinit var firebaseFireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val TAG = "ProfileLogs"

    // UI Items
    private lateinit var userNameEditText: TextInputEditText
    private lateinit var InstagramURLEditText: TextInputEditText
    private lateinit var FacebookEditText: TextInputEditText
    private lateinit var XEditText: TextInputEditText
    private lateinit var YouTubeEditText: TextInputEditText
    private lateinit var saveProfileButton: Button

    //Pre-filed data and no Empty
    private lateinit var lastUserName:String
    private lateinit var lastInstagramLink:String
    private lateinit var lastFBLink:String
    private lateinit var lastXLink:String
    private lateinit var lastYTLink:String

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile_page)

        // Initialize Firebase
        firebaseFireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val documentName = auth.currentUser?.email ?: return  // User email as document name
        val userProfileUrl = auth.currentUser?.photoUrl

        // Initialize UI items
        userNameEditText = findViewById(R.id.profileSetupEnterUserName)
        InstagramURLEditText = findViewById(R.id.profileSetupEnterInstaLink)
        FacebookEditText = findViewById(R.id.profileSetupEnterFBLink)
        XEditText = findViewById(R.id.profileSetupEnterXLink)
        YouTubeEditText = findViewById(R.id.profileSetupEnterYTLink)
        saveProfileButton = findViewById(R.id.saveProfileButton)


        //Check and Fill Profile Data
        // Check if fields are empty and if so, retain the previous data
        firebaseFireStore.collection("usersDetails").document(documentName)
            .get()
            .addOnSuccessListener { document ->

                lastUserName=document.getString("userName").toString()
                lastInstagramLink=document.getString("InstagramLink").toString()
                lastFBLink=document.getString("FacebookLink").toString()
                lastXLink=document.getString("XLink").toString()
                lastYTLink=document.getString("YoutubeLink").toString()


                userNameEditText.setText(lastUserName)
                InstagramURLEditText.setText(lastInstagramLink)
                FacebookEditText.setText(lastFBLink)
                XEditText.setText(lastXLink)
                YouTubeEditText.setText(lastYTLink)

            }


        // Save profile button click listener
        saveProfileButton.setOnClickListener {

            val userName = if (userNameEditText.text.toString().isEmpty()) lastUserName else userNameEditText.text.toString()
            val instaUrl = InstagramURLEditText.text.toString()
            val facebookUrl = FacebookEditText.text.toString()
            val xUrl = XEditText.text.toString()
            val ytUrl = YouTubeEditText.text.toString()

                        // Update the user's profile data
                        firebaseFireStore.collection("usersDetails").document(documentName)
                            .update(
                                "userName", userName,
                                "InstagramLink", instaUrl,
                                "FacebookLink", facebookUrl,
                                "XLink", xUrl,
                                "YoutubeLink", ytUrl,
                                "ProfileSetupStatus", "Done",
                                "UserprofileUrl", userProfileUrl.toString()
                            )
                            .addOnSuccessListener {

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "Error saving profile data: ", e)
                            }

            // Update the user's profile data
            firebaseFireStore.collection("BlogsData").whereEqualTo("userID", documentName)
                .get() // Fetch the documents matching the query
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Update each document individually
                        firebaseFireStore.collection("BlogsData").document(document.id)
                            .update("userName", userName)
                            .addOnSuccessListener {
                                // Navigate to MainActivity after completing the profile
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "Error saving profile data: ", e)
                            }

                    }

                    Toast.makeText(this, "Profile is Complete", Toast.LENGTH_LONG).show()

                }

                .addOnFailureListener { e ->
                    Log.d(TAG, "Error fetching documents: ", e)
                }

        }
    }
}

