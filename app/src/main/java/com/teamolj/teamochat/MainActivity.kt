package com.teamolj.teamochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.teamolj.teamochat.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val rvAdapter = ChatLogAdapter()

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val topAppBar = binding.toolbar
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    auth.signOut()
                    App.prefs.clear()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }

        val userName = App.prefs.getString("userID", "")
        binding.rvChatLog.adapter = rvAdapter

        // "Database lives in a different region" 오류 -> DB url 직접 지정하여 해결
        dbRef = Firebase.database("https://teamchat-ac44f-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        dbRef.child("message").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val uploaded = snapshot.getValue(ChatData::class.java)
                if (uploaded != null) {
                    rvAdapter.listData.add(uploaded)
                    binding.rvChatLog.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onChildRemoved(snapshot: DataSnapshot) { }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onCancelled(error: DatabaseError) { }
        })

        binding.btnSend.setOnClickListener {
            val timeNow = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date())
            val newChat = ChatData(userName, timeNow, binding.editChat.text.toString().trim())
            dbRef.child("message").push().setValue(newChat)
            binding.editChat.setText("")
        }
    }
}