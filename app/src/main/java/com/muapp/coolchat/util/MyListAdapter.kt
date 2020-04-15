package com.muapp.coolchat.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide.with
import com.google.firebase.auth.FirebaseAuth
import com.muapp.coolchat.R
import com.muapp.coolchat.data.DataMessage
import com.muapp.coolchat.data.DataUser
import com.squareup.picasso.Picasso

class MyListAdapter(context: Activity, resource: Int, list: ArrayList<DataMessage>) :
    ArrayAdapter<DataMessage>(context, resource, list) {

    private val list = list
    private val context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var viewHolder: ViewHolder? = null
        var convert = convertView
        val layoutInflater =
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val dataMessage: DataMessage? = getItem(position)
        var layoutResource: Int
        var viewType = getItemViewType(position)

        layoutResource = if (viewType == 0) R.layout.my_message_item
        else R.layout.your_message_item

        if (convert != null) viewHolder = convert.tag as ViewHolder
        else {
            convert = layoutInflater.inflate(layoutResource, parent, false)
            val viewHolder = ViewHolder(convert)
            convert.tag = viewHolder
        }
        var isText: Boolean = dataMessage?.imageuri == null

        if (isText) {
            viewHolder?.myMessageView?.visibility = View.VISIBLE
            viewHolder?.myImageView?.visibility = View.GONE
            viewHolder?.myMessageView?.text = dataMessage?.text
        } else {
            viewHolder?.myMessageView?.visibility = View.GONE
            viewHolder?.myImageView?.visibility = View.VISIBLE
            Picasso.get().load(dataMessage?.imageuri).into(viewHolder?.myImageView)
        }

        return convert!!
    }

    override fun getItemViewType(position: Int): Int {
        val message = list[position]
        return  if (message.isMy!!) 0
        else 1
    }

    override fun getViewTypeCount() = 2

    class ViewHolder(view: View) {
        var myMessageView: TextView = view.findViewById(R.id.myMessageView)
        var myImageView: ImageView = view.findViewById(R.id.myImageView)
    }
}