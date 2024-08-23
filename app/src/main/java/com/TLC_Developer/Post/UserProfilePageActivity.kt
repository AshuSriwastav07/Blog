package com.TLC_Developer.Post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.DataManager.DataClass
import com.TLC_Developer.DataManager.currentUserProfileBlogAdapter
import com.TLC_Developer.Post.databinding.ActivityUserProfilePageBinding
import com.TLC_Developer.functions.function
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class UserProfilePageActivity : AppCompatActivity() {

    // Binding object to access views in the layout
    private lateinit var binding: ActivityUserProfilePageBinding

    // Firebase authentication and Firestore instances
    private val firebaseAuth = Firebase.auth.currentUser
    private val currentUserID = firebaseAuth?.uid.toString()
    private var db = Firebase.firestore

    // List to store blog data and adapter for RecyclerView
    private var userBlogListData: ArrayList<DataClass> = ArrayList()
    private val TAG = "UserProfileLogs"
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: currentUserProfileBlogAdapter

    // Function instance for additional operations
    private val functionCalls = function()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get current user details
        val currentUserProfileImage = firebaseAuth?.photoUrl.toString()
        val profileImageImageView: ImageView = findViewById(R.id.ProfilePageProfileImage)

        // Set user details in UI
        function().getUserSpecificData(currentUserID,"userName") { userName ->
            binding.ProfilePageUserName.text = userName
        }

        binding.ProfilePageUserEmail.text = firebaseAuth?.email


        // Load user profile image using Picasso
        Picasso.get()
            .load(currentUserProfileImage)
            .error(R.mipmap.profileicon)
            .placeholder(R.mipmap.profileicon)
            .into(profileImageImageView)

        // Set up RecyclerView for displaying blogs
        blogRecyclerView = findViewById(R.id.currentUserProfileBlogsRecyclerview)
        val viewManager = LinearLayoutManager(this)

        // Initialize RecyclerView adapter
        recyclerViewAdapter = currentUserProfileBlogAdapter(userBlogListData)
        blogRecyclerView.apply {
            setHasFixedSize(true) // Improves performance if size is constant
            layoutManager = viewManager // Set layout manager for RecyclerView
            adapter = recyclerViewAdapter // Set adapter for RecyclerView
        }




    }

    override fun onStart() {
        super.onStart()
        function().socialMediaLinks(this,db,currentUserID,binding.profileInstaImageButton,binding.profileFBImageButton,binding.profileXImageButton,binding.profileYTImageButton)

        // Load blog data for the current user
        userProfileBlogData()

        binding.editProfileButton.setOnClickListener {
            val intent =Intent(this,SetupProfilePageActivity::class.java)
            startActivity(intent)
        }
    }



    // Function to load blog data from Firestore for the current user
    private fun userProfileBlogData() {
        Log.d(TAG, "Current User ID: $currentUserID")  // Log the current user ID

        db.collection("BlogsData")
            .whereEqualTo("userID", currentUserID) // Filter blogs by current user's ID
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Documents found: ${documents.size()}")  // Log the number of documents found
                userBlogListData.clear()  // Clear previous data
                for (document in documents) {
                    // Create a DataClass object for each document and add it to the list
                    val dataModel = DataClass(
                        blogTitle = document.getString("title") ?: "",
                        blogBody = document.getString("body") ?: "",
                        blogTags = document.getString("tags") ?: "",
                        blogUserID = document.getString("userID") ?: "",
                        blogDateAndTime = document.getString("BlogDateAndTime") ?: "",
                        blogImageURL = document.getString("BlogImageURL") ?: "",
                        blogUserProfileUrl = document.getString("BlogUserProfileUrl") ?: "",
                        blogDocumentID = document.id
                    )
                    userBlogListData.add(dataModel)  // Add data to ArrayList

                    // Sort the blog list by date and time in descending order
                    userBlogListData.sortByDescending {
                        functionCalls.convertStringToDate(it.BlogDateAndTime)?.time
                    }
                }
                recyclerViewAdapter.notifyDataSetChanged() // Notify adapter of data changes
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception) // Log any errors
            }
    }
}
