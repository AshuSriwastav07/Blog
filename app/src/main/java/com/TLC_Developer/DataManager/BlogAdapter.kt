package com.TLC_Developer.DataManager

import android.annotation.SuppressLint
import android.provider.Settings.Global.getString
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.Post.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlogAdapter(private var BlogDataSet:ArrayList<DataClass>):
    RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Initialise all the variable and item
        val blogTitle:TextView=view.findViewById(R.id.blogItemTitleTextview)
        val blogDateAndTime:TextView=view.findViewById(R.id.blogItemDateTimeTextview)
        val blogBgImageView:ImageView=view.findViewById(R.id.blogItemImageView)
        val userProfileImage:ImageView=view.findViewById(R.id.userBlogProfileImage)
        val userName:TextView=view.findViewById(R.id.blogWriterName)
        val blogTags:TextView=view.findViewById(R.id.blogTage
        )



    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.blog_item_view, viewGroup, false)  //set item layout int recyclerview

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val blog = BlogDataSet[position]
        viewHolder.blogTitle.text=blog.BlogTitle
        viewHolder.userName.text=blog.BlogWriterName
        viewHolder.blogTags.text=blog.BlogTags
        stringToDate(blog.BlogDateAndTime,viewHolder)
        Picasso.get().load(blog.BlogImageURL).into(viewHolder.blogBgImageView);
        Picasso.get().load(blog.BlogUserProfileUrl).into(viewHolder.userProfileImage);


    }

    override fun getItemCount(): Int {
        return BlogDataSet.size
    }

    @SuppressLint("SetTextI18n")
    fun stringToDate(dateString: String, viewHolder: ViewHolder){
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val publishDate:Date=formatter.parse(dateString) ?: Date()

        val diff = System.currentTimeMillis() - publishDate.time

        if (diff < DateUtils.DAY_IN_MILLIS) {
            viewHolder.blogDateAndTime.text= "Recently"
        } else if (diff < 2 * DateUtils.DAY_IN_MILLIS) {
            viewHolder.blogDateAndTime.text="Yesterday"
        } else {
            viewHolder.blogDateAndTime.text=dateString
        }
    }




}