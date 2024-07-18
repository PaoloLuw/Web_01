package com.luwliette.ztmelody_02

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class SongAdapterActivity_2(
    private var countryList: List<SongModelActivity>,
    private val openArtistDetailsActivity: (String) -> Unit
) : RecyclerView.Adapter<SongAdapterActivity_2.ViewHolder>() {

    fun update_lista (SongList_o : List<SongModelActivity>){
        countryList=SongList_o
        notifyDataSetChanged()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryName: TextView = itemView.findViewById(R.id.country_name_tv)
        val countryImage: CircleImageView = itemView.findViewById(R.id.country_flag_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_button_album, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countryList[position]

        val MAX_LENGTH = 20 // Definir la constante aquí
        holder.countryName.text = if (country.countryName.length > MAX_LENGTH) {
            "${country.countryName.substring(0, MAX_LENGTH)}..."
        } else {
            country.countryName
        }

        holder.countryImage.setImageResource(country.countryImage)

        holder.itemView.setOnClickListener {
            Log.d("ADAPTERM", "Clicked on item: ${country.countryName}")
            openArtistDetailsActivity(country.countryName)
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }
}
