package com.example.musicapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.data.model.SongListItem
import com.squareup.picasso.Picasso

class HomeListAdapter(private val dataSet: List<SongListItem>) :
        RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTitle: TextView = view.findViewById(R.id.header_title)
        val subTitle: TextView = view.findViewById(R.id.sub_title)
        val image: ImageView = view.findViewById(R.id.imageView)

        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.song_detail, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.headerTitle.text = dataSet[position].name
        viewHolder.subTitle.text = dataSet[position].artist

        dataSet[position].imageUrl.let {
            if(it.isNotEmpty()) {
                Picasso.get().load(dataSet[position].imageUrl)
                    .into(viewHolder.image)
            }
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}