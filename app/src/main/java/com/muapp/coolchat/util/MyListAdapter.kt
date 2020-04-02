package com.muapp.coolchat.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.muapp.coolchat.R
import com.muapp.coolchat.data.DataMessage

class MyListAdapter(context: Context, resource: Int, list: ArrayList<DataMessage>) :
    ArrayAdapter<DataMessage>(context, resource, list) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        val textName = view.findViewById<TextView>(R.id.textName)
        val textText = view.findViewById<TextView>(R.id.textText)
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        val message: DataMessage? = getItem(position)
        val isText: Boolean = message?.imageuri == null

        if (isText) {
            imageView.visibility = View.GONE
            textText.visibility = View.VISIBLE
            textText.text = message?.text
        } else {
            imageView.visibility = View.VISIBLE
            textText.visibility = View.GONE
            Glide.with(imageView.context).load(message?.imageuri).into(imageView)
        }

        textName.text = message?.name
        return view
    }
}