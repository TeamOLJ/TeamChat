package com.teamolj.teamochat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.teamochat.databinding.ViewRecyclerChatBinding

class ChatLogAdapter: RecyclerView.Adapter<Holder>() {
    var listData = mutableListOf<ChatData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ViewRecyclerChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val chat = listData[position]
        with(holder) {
            serChatLog(chat)
        }
    }

    override fun getItemCount(): Int = listData.size
}

class Holder(private val binding: ViewRecyclerChatBinding): RecyclerView.ViewHolder(binding.root) {
    fun serChatLog(chat: ChatData) {
        binding.txtChatOwner.text = chat.userName
        binding.txtChatTime.text = chat.sendTime
        binding.txtChatContext.text = chat.message
    }
}