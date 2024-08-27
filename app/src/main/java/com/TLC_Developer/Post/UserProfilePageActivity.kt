package com.TLC_Developer.Post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.DataManager.DataClass
import com.TLC_Developer.DataManager.currentUserProfileBlogAdapter
import com.TLC_Developer.Post.databinding.ActivityUserProfilePageBinding
import com.TLC_Developer.functions.functionsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserProfilePageActivity : AppCompatActivity() {

    // Binding object to access views in the layout
    private lateinit var binding: ActivityUserProfilePageBinding

    // Firebase authentication and Firestore instances
    private val firebaseAuth = Firebase.auth.currentUser
    val documentName = firebaseAuth?.email.toString()  // User email as document name
    private var db = Firebase.firestore

    // List to store blog data and adapter for RecyclerView
    private var userBlogListData: ArrayList<DataClass> = ArrayList()
    private val TAG = "UserProfileLogs"
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: currentUserProfileBlogAdapter

    // Function instance for additional operations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val documentName = FirebaseAuth.getInstance().currentUser?.email.toString()

        functionsManager().showDataInOnlyViewProfile(this,documentName,binding.ProfilePageProfileImage,binding.ProfilePageUserName,binding.profileInstaImageButton,binding.profileFBImageButton,binding.profileXImageButton,binding.profileYTImageButton)


        //setEmail
        binding.ProfilePageUserEmail.text=documentName

        // Load blog data for the current user
        userProfileBlogData()

        binding.editProfileButton.setOnClickListener {
            val intent =Intent(this,SetupProfilePageActivity::class.java)
            startActivity(intent)
        }


    }



    // Function to load blog data from Firestore for the current user
    private fun userProfileBlogData() {

        Log.d(TAG, "Current User ID: $documentName")  // Log the current user ID

        db.collection("BlogsData")
            .whereEqualTo("userID", documentName) // Filter blogs by current user's ID
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
                        blogDocumentID = document.id,
                        BlogUserName = document.getString("userName").toString()
                    )
                    userBlogListData.add(dataModel)  // Add data to ArrayList

                    // Sort the blog list by date and time in descending order
                    userBlogListData.sortByDescending {
                        functionsManager().convertStringToDate(it.BlogDateAndTime)?.time
                    }
                }
                recyclerViewAdapter.notifyDataSetChanged() // Notify adapter of data changes
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception) // Log any errors
            }
    }
}
