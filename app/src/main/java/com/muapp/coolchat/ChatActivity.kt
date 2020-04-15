package com.muapp.coolchat

import android.app.Activity
import android.content.Intent
import android.icu.text.CaseMap
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.muapp.coolchat.data.DataMessage
import com.muapp.coolchat.data.DataUser
import com.muapp.coolchat.util.MyListAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    lateinit var myListAdapter: MyListAdapter
    private val list: ArrayList<DataMessage> = arrayListOf()
    var userName: String? = null
    var recipient: String? = null
    private var recipientName: String? = null

    private val dataBase = Firebase.database
    lateinit var refMessage: DatabaseReference
    private lateinit var refUsers: DatabaseReference
    lateinit var auth: FirebaseAuth

    private val storage = Firebase.storage
    private var storageRef = storage.reference.child("message")

    private val message = DataMessage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = intent
        if (intent != null) {
            userName = intent.getStringExtra("NameUser")
            recipient = intent.getStringExtra("idUser")
            recipientName = intent.getStringExtra("nameRecipient")
        } else "Default name"

        title = "Чат с $recipientName"


        refMessage = dataBase.reference.child("message")
        refUsers = dataBase.reference.child("users")
        auth = FirebaseAuth.getInstance()

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
        send_text.text.clear()
        message.name = userName
        message.imageuri = null
        message.sender = auth.currentUser!!.uid
        message.recipient = recipient

        refMessage.push().setValue(message)
    }

    fun sendImage(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Выберете изображение"), From_Result)
    }

    val usersListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Toast.makeText(this@ChatActivity, "Ошибка имя $p0", Toast.LENGTH_LONG).show()
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val user: DataUser? = p0.getValue(DataUser::class.java)
            if (user?.id!!.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
                userName = user.name
            }

        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO()
        }
    }

    private val messageListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Toast.makeText(baseContext, "Ошибка удаления", Toast.LENGTH_LONG).show()
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val message: DataMessage? = p0.getValue(DataMessage::class.java)
            if ((message?.sender.equals(auth.currentUser?.uid) && message?.recipient.equals(recipient))
            ) {
                message?.isMy = true
                list.add(message!!)
                myListAdapter.notifyDataSetChanged()
            } else if (message?.recipient.equals(
                    auth.currentUser?.uid
                ) && message?.sender.equals(recipient)
            ) {
                message?.isMy = false
                list.add(message!!)
                myListAdapter.notifyDataSetChanged()
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO("Not yet implemented")
        }
    }

    override fun onResume() {
        super.onResume()
        refMessage.addChildEventListener(messageListener)
        refUsers.addChildEventListener(usersListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val myMenu: MenuInflater = menuInflater
        myMenu.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteUser -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignActivity::class.java))
                return true
            }
            R.id.addAvatar -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                startActivityForResult(
                    Intent.createChooser(intent, "Выберете изображение"),
                    From_Avatar
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == From_Result && resultCode == Activity.RESULT_OK -> {
                val uriImage = data?.data
                val imageStorage: StorageReference =
                    uriImage?.lastPathSegment?.let { storageRef.child(it) }!!
                val uploadTask = imageStorage.putFile(uriImage)


                val uploadTaskUri = storageRef.putFile(uriImage)

                val urlTask = uploadTaskUri.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val dataMessage = DataMessage()
                        dataMessage.sender = auth.currentUser!!.uid
                        dataMessage.recipient = recipient
                        dataMessage.name = userName
                        dataMessage.imageuri = downloadUri?.toString()
                        refMessage.push().setValue(dataMessage)

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }
        }
    }

    companion object {
        private const val From_Result = 255
        private const val From_Avatar = 256
    }
}
