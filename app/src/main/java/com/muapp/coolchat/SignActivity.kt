package com.muapp.coolchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.muapp.coolchat.data.DataUser
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private var loginModeActive: Boolean = false

    private val dataBase = Firebase.database
    lateinit var refUsers: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        refUsers = dataBase.reference.child("users")


        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) startActivity(Intent(this, UserListActivity::class.java))
    }


    private fun registration(email: String, password: String) {
        if (loginModeActive) {
            if (editPassword.text.toString().trim().length < 8) Toast.makeText(
                this,
                "Пароль должен быть больше 8 символов",
                Toast.LENGTH_LONG
            ).show()
            else if (editEmail.text.toString() == "") Toast.makeText(
                this,
                "Введите email",
                Toast.LENGTH_LONG
            ).show()
            else if (editName.text.toString() == "") Toast.makeText(
                this,
                "Введите имя",
                Toast.LENGTH_LONG
            ).show()
            else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success")
                            val user = auth.currentUser
                            createUser(user)
                            val intent = Intent(this, UserListActivity::class.java)
                            intent.putExtra("nameUser", editName.text.toString().trim())
                            startActivity(intent)
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            //updateUI(null)
                            // ...
                        }

                        // ...
                    }
            }
        } else {
            if (editPassword.text.toString().trim().length < 8) Toast.makeText(
                this,
                "Пароль должен быть больше 8 символов",
                Toast.LENGTH_LONG
            ).show()
            else if (editEmail.text.toString() == "") Toast.makeText(
                this,
                "Введите email",
                Toast.LENGTH_LONG
            ).show()
            else if (editName.text.toString() == "") Toast.makeText(
                this,
                "Введите имя",
                Toast.LENGTH_LONG
            ).show()
            else if (editPassword.text.toString().trim() == editRepeatPassword.text.toString()
                    .trim()
            ) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            createUser(user)
                            val intent = Intent(this, UserListActivity::class.java)
                            intent.putExtra("nameUser", editName.text.toString().trim())
                            startActivity(intent)
                            //updateUI(user)
                        } else {
                            Toast.makeText(
                                this, "Ошибка регистрации",
                                Toast.LENGTH_SHORT
                            ).show()
                            //updateUI(null)
                        }

                    }        // ...
            } else Toast.makeText(this, "Пароли не похожи", Toast.LENGTH_LONG).show()
        }
    }

    private fun createUser(fireBaseUser: FirebaseUser?) {
        val dataUser = DataUser()
        dataUser.id = fireBaseUser?.uid
        dataUser.email = fireBaseUser?.email
        dataUser.name = editName.text.toString().trim()

        refUsers.push().setValue(dataUser)
    }

    fun signIn(view: View) {
        registration(
            email = editEmail.text.toString().trim(),
            password = editPassword.text.toString().trim()
        )
    }

    fun tapToLogIn(view: View) {
        if (loginModeActive) {
            loginModeActive = false
            buttonSign.text = "Sing up"
            texLogIn.text = "Or, log in"
            editRepeatPassword.visibility = View.VISIBLE
        } else {
            loginModeActive = true
            buttonSign.text = "Sing in"
            texLogIn.text = "Or, sing up"
            editRepeatPassword.visibility = View.GONE
        }
    }
}
