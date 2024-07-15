package com.luwliette.ztmelody_02.ArtistRecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luwliette.ztmelody_02.R

class SongV_Adapter():RecyclerView.Adapter<SongV_Adapter.ViewHolder>() {

    var lstSongName: List<SongV_Model> = emptyList()
    fun actualizarLsta(lst: List<SongV_Model>){
        lstSongName = lst
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        val ItemImage = view.findViewById<TextView>(R.id.item_image)
        val ItemName = view.findViewById<TextView>(R.id.item_name)
        val ItemDate = view.findViewById<TextView>(R.id.item_date)

        fun setValues(MyModel: SongV_Model){
            ItemName.setText(MyModel.item_name.toString())
            ItemDate.setText(MyModel.item_date.toString())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_displacement_custom_layout,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lstSongName.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setValues(lstSongName[position])
    }
}