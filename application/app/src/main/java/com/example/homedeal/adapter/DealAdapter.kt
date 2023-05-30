package com.example.homedeal.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homedeal.App
import com.example.homedeal.R
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.dao.FirebaseSaveDao
import com.example.homedeal.dao.SaveDao
import com.example.homedeal.model.Deal

class DealAdapter(private val dataList: ArrayList<Deal>, private val icon: Int, private val recyclerView: RecyclerView): RecyclerView.Adapter<DealAdapter.ViewHolderClass>() {
    companion object {
        private const val TAG = "DealAdapter"
    }
    var onItemClick: ((Deal) -> Unit)? = null
    var onIconClick: ((Deal) -> Unit)? = null
    var onBinding: ((Deal) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.deal_card_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        onBinding?.invoke(currentItem)
        holder.rvIcon?.setImageResource(icon)
        Glide.with(App.applicationContext)
            .load(Uri.parse(currentItem.image))
            .into(holder.rvImage)
        holder.rvTitle.text = currentItem.name
        holder.rvPrice.text = String.format("%.2f €", currentItem.price)
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
        holder.rvIcon?.setOnClickListener{
            onIconClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getIconView(item: Deal): View? {
        val position = dataList.indexOf(item)
        return recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById(R.id.ic_deal_card)
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvImage:ImageView = itemView.findViewById(R.id.image_deal_card)
        val rvTitle:TextView = itemView.findViewById(R.id.name_deal_card)
        val rvPrice:TextView = itemView.findViewById(R.id.price_card)
        val rvIcon:ImageView? = itemView.findViewById(R.id.ic_deal_card)
    }
}