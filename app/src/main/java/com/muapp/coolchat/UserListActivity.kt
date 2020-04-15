package com.muapp.coolchat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.muapp.coolchat.data.DataUser
import com.muapp.coolchat.util.MyAdapterUserList
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity() {

    lateinit var refUsers: DatabaseReference
    private val dataBase = Firebase.database
    lateinit var auth: FirebaseAuth

    lateinit var listUsers: ArrayList<DataUser?>
    lateinit var myAdapter: MyAdapterUserList
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        listUsers = arrayListOf()

        val intent = intent
        userName = intent.getStringExtra("nameUser")

        listenerUsers()

        auth = FirebaseAuth.getInstance()

        myAdapter = MyAdapterUserList(listUsers, object : MyAdapterUserList.CallBack {
            override fun onItemClicked(item: DataUser) {
                startChat(item)
            }
        })

        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.adapter = myAdapter

    }

    private fun startChat(item: DataUser) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("idUser", item.id)
        intent.putExtra("nameRecipient", item.name)
        intent.putExtra("nameUser", userName)
        startActivity(intent)
    }

    private fun listenerUsers() {
        val childListenerUsers = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@UserListActivity, "нету пользователей", Toast.LENGTH_LONG).show()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val user: DataUser? = p0.getValue(DataUser::class.java)
                if (!user?.id!!.equals(auth.currentUser?.uid)) {
                    user.avatar = R.drawable.ic_image_black_24dp
                    listUsers.add(user)
                    myAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        }
        refUsers = dataBase.reference.child("users")
        refUsers.addChildEventListener(childListenerUsers)

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
        }
        return super.onOptionsItemSelected(item)
    }

}
