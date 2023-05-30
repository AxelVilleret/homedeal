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
import com.example.homedeal.dao.DealDao
import com.example.homedeal.dao.FirebaseDealDao
import com.example.homedeal.model.Alerte
import com.example.homedeal.model.Deal
import com.example.homedeal.utils.ViewUtils

class AlerteAdapter(private val alerteList: ArrayList<Alerte>, private val icon: Int): RecyclerView.Adapter<AlerteAdapter.ViewHolderClass>() {
    companion object {
        private const val TAG = "AlerteAdapter"
        private val DEALDAO: DealDao = FirebaseDealDao
    }
    var onItemClick: ((Deal) -> Unit)? = null
    var onIconClick: ((Alerte) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.alerte_card_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = alerteList[position]
        DEALDAO.getDealByRef(currentItem.dealRef) { deal ->
            holder.rvIcon?.setImageResource(icon)
            Glide.with(App.applicationContext)
                .load(Uri.parse(deal.image))
                .into(holder.rvImage)
            holder.rvTitle.text = deal.name
            holder.rvPrice.text = String.format("%.2f €", deal.price)
            holder.rvAlerte.text = currentItem.title
            holder.rvBody.text = currentItem.body
            holder.rvTime.text = currentItem.timestamp
            holder.itemView.setOnClickListener{
                onItemClick?.invoke(deal)
            }
            holder.rvIcon?.setOnClickListener{
                onIconClick?.invoke(currentItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return alerteList.size
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvImage:ImageView = itemView.findViewById(R.id.image_deal_card)
        val rvTitle:TextView = itemView.findViewById(R.id.name_deal_card)
        val rvPrice:TextView = itemView.findViewById(R.id.price_card)
        val rvIcon:ImageView? = itemView.findViewById(R.id.ic_deal_card)
        val rvAlerte:TextView = itemView.findViewById(R.id.title_alerte_card)
        val rvTime:TextView = itemView.findViewById(R.id.time_alerte_card)
        val rvBody: TextView = itemView.findViewById(R.id.body_alerte_card)
    }
}