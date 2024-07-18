package com.luwliette.ztmelody_02

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class SongAdapterClass_3(
    private var countryList: ArrayList<SongModelActivity>,
    private val playSong: (List<SongModelActivity>, Int) -> Unit,
    private val openSongDetailsActivity: () -> Unit
) : RecyclerView.Adapter<SongAdapterClass_3.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryName: TextView = itemView.findViewById(R.id.country_name_tv)
        val countryImage: CircleImageView = itemView.findViewById(R.id.country_flag_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_button_song, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countryList[position]

        holder.countryName.text = country.countryName
        holder.countryImage.setImageResource(country.countryImage)

        holder.itemView.setOnClickListener {
            playSong(countryList, position)
            openSongDetailsActivity()
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    fun updateSongs(newSongs: ArrayList<SongModelActivity>) {
        countryList = newSongs
        notifyDataSetChanged()
    }
}
