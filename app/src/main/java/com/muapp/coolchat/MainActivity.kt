package com.muapp.coolchat

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.muapp.coolchat.data.DataMessage
import com.muapp.coolchat.util.MyListAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var myListAdapter: MyListAdapter
    val list: ArrayList<DataMessage> = arrayListOf()
    val userName = "Default Name"
    val dataBase = Firebase.database
    lateinit var refMessage: DatabaseReference
    val message = DataMessage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refMessage = dataBase.reference.child("message")

        myListAdapter = MyListAdapter(this, R.layout.item_list, list)
        listMessage.adapter = myListAdapter

        progressBar.visibility = ProgressBar.INVISIBLE
        val filter = InputFilter.LengthFilter(500)
        send_text.filters = arrayOf(filter)

        send_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                button_send.isEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                button_send.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                button_send.isEnabled = s.toString().trim().isNotEmpty()
            }

        })

    }

    fun sendImageButton(view: View) {
        message.text = send_text.text.toString()
        message.name = userName
        message.imageuri = null

        refMessage.push().setValue(message)
    }

    fun sendImage(view: View) {}

    val messageListener = object : ChildEventListener{
        override fun onCancelled(p0: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val message: DataMessage? = p0.getValue(DataMessage::class.java)
            myListAdapter.add(message)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO("Not yet implemented")
        }
    }

    override fun onResume() {
        super.onResume()
        refMessage.addChildEventListener(messageListener)
    }

}
