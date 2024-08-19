package com.TLC_Developer.DataManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.TLC_Developer.Post.R
import com.squareup.picasso.Picasso

class BlogAdapter(private var BlogDataSet:ArrayList<DataClass>):
    RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //Initialise all the variable and item
        val blogTitle:TextView=view.findViewById(R.id.blogItemTitleTextview)
        val blogDateAndTime:TextView=view.findViewById(R.id.blogItemDateTimeTextview)
        val blogWriterName:TextView=view.findViewById(R.id.blogItemWriterTextview)
        val blogBgImageView:ImageView=view.findViewById(R.id.blogItemImageView)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BlogAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.blog_item_view, viewGroup, false)  //set item layout int recyclerview

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: BlogAdapter.ViewHolder, position: Int) {
        val blog = BlogDataSet[position]
        viewHolder.blogTitle.text=blog.BlogTitle
        viewHolder.blogWriterName.text=blog.BlogWriterName
        viewHolder.blogDateAndTime.text=blog.BlogDateAndTime
        Picasso.get().load(blog.BlogImageURL).into(viewHolder.blogBgImageView);

    }

    override fun getItemCount(): Int {
        return BlogDataSet.size
    }

}