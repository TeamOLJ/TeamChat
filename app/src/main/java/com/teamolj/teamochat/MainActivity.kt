package com.teamolj.teamochat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.teamolj.teamochat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val rvAdapter = ChatLogAdapter()

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = "testUser"
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
            val newChat = ChatData(userName, binding.editChat.text.toString().trim())
            dbRef.child("message").push().setValue(newChat)
            binding.editChat.setText("")
        }
    }
}