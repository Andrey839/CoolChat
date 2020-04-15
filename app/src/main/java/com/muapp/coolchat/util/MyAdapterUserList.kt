package com.muapp.coolchat.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muapp.coolchat.R
import com.muapp.coolchat.data.DataUser

class MyAdapterUserList(var listUsers: List<DataUser?>, val callBack: CallBack) :
    RecyclerView.Adapter<MyAdapterUserList.MyListViewHolder>() {
    inner class MyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val name = itemView.findViewById<TextView>(R.id.textCard)
        private val logo = itemView.findViewById<ImageView>(R.id.imageCard)

        fun bind(item: DataUser) {
            name.text = item.name
            logo.setImageResource(item.avatar)
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    callBack.onItemClicked(listUsers[adapterPosition]!!)
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyListViewHolder {
        return MyListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        )
    }

    override fun getItemCount() = listUsers.size

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        holder.bind(listUsers[position]!!)
    }

    interface CallBack {
        fun onItemClicked(item: DataUser)
    }
}