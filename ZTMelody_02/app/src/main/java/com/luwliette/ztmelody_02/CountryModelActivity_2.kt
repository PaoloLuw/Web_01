package com.luwliette.ztmelody_02

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class CountryAdapterActivity_2(
    private val countryList: List<CountryModelActivity>,
    private val openArtistDetailsActivity: (String) -> Unit
) : RecyclerView.Adapter<CountryAdapterActivity_2.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryName: TextView = itemView.findViewById(R.id.country_name_tv)
        val countryImage: CircleImageView = itemView.findViewById(R.id.country_flag_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_button_layout_2, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countryList[position]
        holder.countryName.text = country.countryName
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
