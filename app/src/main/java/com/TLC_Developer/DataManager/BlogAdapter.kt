package com.TLC_Developer.DataManager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.Post.R
import com.TLC_Developer.Post.readBlogPageActivity
import com.TLC_Developer.functions.functionsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adapter class for displaying blog posts in a RecyclerView
class BlogAdapter(private var blogDataSet: ArrayList<DataClass>,private var context: Context) :
    RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    // ViewHolder class to hold and recycle views
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val blogTitle: TextView = view.findViewById(R.id.blogItemTitleTextview) // Title of the blog
        val blogDateAndTime: TextView = view.findViewById(R.id.blogItemDateTimeTextview) // Date and time of the blog
        val blogBgImageView: ImageView = view.findViewById(R.id.blogItemImageView) // Background image of the blog
        val userProfileImage: ImageView = view.findViewById(R.id.userBlogProfileImage) // User's profile image
        val userName: TextView = view.findViewById(R.id.blogWriterName) // Writer's name
        val blogTags: TextView = view.findViewById(R.id.blogTage) // Tags associated with the blog
        val blogWriterMiniProfileCardView:CardView=view.findViewById(R.id.blogWriterMiniProfileCardview)
        val ImageCardView:CardView=view.findViewById(R.id.HomePageBlogImageCardView)

    }

    // Create new ViewHolder instances
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each blog item in the RecyclerView
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.blog_item_view, viewGroup, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the blog data for the current position
        val blog = blogDataSet[position]

        val currentLoginUserId=FirebaseAuth.getInstance().currentUser?.email.toString()
        val currentLoginUserGoogleName=FirebaseAuth.getInstance().currentUser?.displayName.toString()

        // Set the blog title and writer's name
        viewHolder.blogTitle.text = blog.BlogTitle

        //add data to send in next activity reading
        val dataForReadingBlog:ArrayList<String> = arrayListOf(
            blog.BlogTitle,
            blog.BlogDateAndTime,
            blog.BlogImageURL,
            blog.BlogBody,
            blog.BlogUserProfileUrl,
            blog.BlogUserName,
            blog.BlogDocumentID,
            blog.BlogUserID,
        )

        FirebaseFirestore.getInstance().collection("usersDetails").document(currentLoginUserId)
            .get()
            .addOnSuccessListener { document ->
                    dataForReadingBlog.add(document.getString("userName").toString())
            }.addOnFailureListener {
                dataForReadingBlog.add(currentLoginUserGoogleName)
            }
        //get and Set User name for each Item

        viewHolder.userName.text=blog.BlogUserName


        viewHolder.blogTags.text = blog.BlogTags


        // Format and set the date and time
        stringToDate(blog.BlogDateAndTime, viewHolder)

        // Load the blog's background image and user's profile image using Picasso
        functionsManager().loadBlogBGImages(blog.BlogImageURL,viewHolder.blogBgImageView)

        //load Profile Image
        functionsManager().loadProfileImagesImage(blog.BlogUserProfileUrl,viewHolder.userProfileImage)

        //Open specific use profile
        viewHolder.blogWriterMiniProfileCardView.setOnClickListener {
            functionsManager().openProfile(context,blog.BlogUserID)
        }


        //Open Blog for Reading
        viewHolder.blogTitle.setOnClickListener{
            readBlog(context,dataForReadingBlog)
        }

        viewHolder.ImageCardView.setOnClickListener{
            readBlog(context,dataForReadingBlog)
        }

    }

    // Return the total number of items in the data set
    override fun getItemCount(): Int {
        return blogDataSet.size
    }

    // Format the date and time to a readable format
    @SuppressLint("SetTextI18n")
    private fun stringToDate(dateString: String, viewHolder: ViewHolder) {
        // Define the original date format used in the blog's date string
        val originalFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        // Parse the date string to a Date object
        val publishDate: Date = originalFormat.parse(dateString) ?: Date()
        // Calculate the time difference between now and the publication date
        val diff = System.currentTimeMillis() - publishDate.time

        // Define the new date format with AM/PM
        val newFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        // Format the publishDate to the new format
        val formattedDate = newFormat.format(publishDate)

        // Set the appropriate text based on the time difference
        viewHolder.blogDateAndTime.text = when {
            diff < DateUtils.DAY_IN_MILLIS -> "Recently" // Less than a day ago
            diff < 2 * DateUtils.DAY_IN_MILLIS -> "Yesterday" // Less than two days ago
            else -> formattedDate // Display the formatted date with AM/PM
        }
    }

    fun readBlog(context: Context,blogReadingData:ArrayList<String>){
        val intent = Intent(context,readBlogPageActivity::class.java)
        intent.putStringArrayListExtra("blogData",blogReadingData)
        context.startActivity(intent)
    }

}
