package com.luwliette.ztmelody_02

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class SongSuggestedAdapterActivity (
    
    private val countryList: ArrayList<SongModelActivity>,
    private val songPaths: List<String>,
    private val playSong: (List<String>, Int) -> Unit,
    private val openSongDetailsActivity: () -> Unit,

    ) : RecyclerView.Adapter<SongSuggestedAdapterActivity.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryName: TextView = itemView.findViewById(R.id.country_name_tv)
        val countryImage: CircleImageView = itemView.findViewById(R.id.country_flag_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_button_suggested, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countryList[position]

        holder.countryName.text = country.countryName
        holder.countryImage.setImageResource(country.countryImage)

        holder.itemView.setOnClickListener {
            playSong(songPaths, position)
            openSongDetailsActivity()
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }
}