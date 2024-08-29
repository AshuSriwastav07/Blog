package com.TLC_Developer.DataManager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.Post.EditBlogActivity
import com.TLC_Developer.Post.R
import com.TLC_Developer.Post.readBlogPageActivity
import com.TLC_Developer.functions.functionsManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adapter class for displaying blogs in the user's profile
class currentUserProfileBlogAdapter(private var blogDataSet: ArrayList<DataClass>) :
    RecyclerView.Adapter<currentUserProfileBlogAdapter.ViewHolder>() {


    // ViewHolder class to hold and recycle views
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfileImage: ImageView = view.findViewById(R.id.currentUser_userProfile)
        val userName: TextView = view.findViewById(R.id.currentUser_UserName)
        val blogTitle: TextView = view.findViewById(R.id.currentUser_ProfileBlogTitle)
        val blogDateAndTime: TextView = view.findViewById(R.id.currentUser_ProfileBlogDateTime)
        val blogEditAndReadButton: ImageButton = view.findViewById(R.id.currentUser_BlogEditButton)
        val blogDeleteButton: ImageButton = view.findViewById(R.id.currentUser_BlogDeleteButton)
        val BGImage:ImageView=view.findViewById(R.id.profileVersionBlogImageView)
        val completeLayout:CardView=view.findViewById(R.id.profileBlogListCompleteLayoutCardView)

        // Optional views commented out as they are not used

    }

    // Create new ViewHolder instances
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each item in the RecyclerView
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.blogitem_profile_version, viewGroup, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the blog data for the current position

        val blog = blogDataSet[position]

        // Set the blog title and writer's name
            viewHolder.userName.text = blog.BlogTags

        viewHolder.blogTitle.text = blog.BlogTitle
        // Format and set the date and time
        stringToDate(blog.BlogDateAndTime, viewHolder)

        // Load the user's profile image using Picasso
//        Log.d("profileImageData",blog.BlogUserProfileUrl)

        functionsManager().loadProfileImagesImage(blog.BlogUserProfileUrl,viewHolder.userProfileImage)
        functionsManager().loadBlogBGImages(blog.BlogImageURL,viewHolder.BGImage)

        // if current user is on profile or other user
        val user = Firebase.auth.currentUser
        val context=viewHolder.itemView.context
        val dataForReadingBlog:java.util.ArrayList<String> = arrayListOf(
            blog.BlogTitle,
            blog.BlogDateAndTime,
            blog.BlogImageURL,
            blog.BlogBody,
            blog.BlogUserProfileUrl,
            blog.BlogUserName,
            blog.BlogDocumentID,
            blog.BlogUserID

        )



        //show buttons on ling click
        viewHolder.completeLayout.setOnLongClickListener{
            if(user?.email.toString()==blog.BlogUserID) {
                viewHolder.blogDeleteButton.visibility = View.VISIBLE
                viewHolder.blogEditAndReadButton.visibility = View.VISIBLE

            }else if(user?.email.toString()!=blog.BlogUserID){
                viewHolder.blogEditAndReadButton.setImageResource(R.mipmap.read)
                viewHolder.blogEditAndReadButton.visibility = View.VISIBLE
            }

            //hide buttons auto
            Handler(Looper.getMainLooper()).postDelayed({
                viewHolder.blogDeleteButton.visibility = View.GONE
                viewHolder.blogEditAndReadButton.visibility = View.GONE
            }, 30000)

            true
        }


        //Delete The Blog
        viewHolder.blogDeleteButton.setOnClickListener{
            showDeleteConfirmationDialog(context,blog.BlogDocumentID,blog.BlogImageURL)
        }


        viewHolder.blogEditAndReadButton.setOnClickListener {
            if(user?.email.toString()!=blog.BlogUserID){
                readBlog(context,dataForReadingBlog)
            }else if(user?.email.toString()==blog.BlogUserID) {
                openBlogToEdit(blog.BlogDocumentID, viewHolder)
            }
        }


    }

    // Return the total number of items in the data set
    override fun getItemCount(): Int {
        return blogDataSet.size
    }

    // Format the date and time to a readable format
    @SuppressLint("SetTextI18n")
    private fun stringToDate(dateString: String, viewHolder: ViewHolder) {
        // Define the date format
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        // Parse the date string to a Date object
        val publishDate: Date = formatter.parse(dateString) ?: Date()
        // Calculate the time difference between now and the publication date
        val diff = System.currentTimeMillis() - publishDate.time

        // Set the appropriate text based on the time difference
        viewHolder.blogDateAndTime.text = when {
            diff < DateUtils.DAY_IN_MILLIS -> "Recently" // Less than a day ago
            diff < 2 * DateUtils.DAY_IN_MILLIS -> "Yesterday" // Less than two days ago
            else -> dateString // Default to the original date string
        }
    }

    // Open the EditBlogActivity to edit the blog post
    private fun openBlogToEdit(blogDocumentID: String, holder: ViewHolder) {
        val context = holder.itemView.context
        val intent = Intent(context, EditBlogActivity::class.java)
        // Pass the blog document ID to the EditBlogActivity
        intent.putExtra("documentID_forDataToEdit", blogDocumentID)
        context.startActivity(intent) // Start the EditBlogActivity
    }

    fun readBlog(context: Context,blogReadingData:ArrayList<String>){
        val intent = Intent(context, readBlogPageActivity::class.java)
        intent.putStringArrayListExtra("blogData",blogReadingData)
        context.startActivity(intent)
    }

    fun deleteBlog(documentID:String,imageURL:String,context: Context){
        val db=FirebaseFirestore.getInstance()

        //Image url is empty so delete only blog data
        if(!imageURL.contains("https://firebasestorage.googleapis.com")){
            db.collection("BlogsData").document(documentID)
                .delete()
                .addOnSuccessListener { Toast.makeText(context,"Blog Deleted Successfully!",Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { e -> Toast.makeText(context,"Blog not Deleted! ${e}",Toast.LENGTH_LONG).show() }
        }else{  //Image url is not empty so delete only blog data and user uploaded image from RTDB
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL)
        storageRef.delete()
            .addOnSuccessListener {
                db.collection("BlogsData").document(documentID)
                    .delete()
                    .addOnSuccessListener { Toast.makeText(context,"Blog Deleted Successfully!",Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { e -> Toast.makeText(context,"Blog not Deleted! ${e}",Toast.LENGTH_LONG).show() }

            }
            .addOnFailureListener { e ->
                Toast.makeText(context,"Blog not Deleted! ${e}",Toast.LENGTH_SHORT).show()
            }

     }
    }

    private fun showDeleteConfirmationDialog(context: Context,documentID:String,imageURL:String) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this blog? This action cannot be undone.")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteBlog(documentID,imageURL,context) // User confirmed deletion
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                 // User canceled deletion
                dialog.dismiss()
            }
            .create()
            .show()
    }


}
