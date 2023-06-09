package com.example.datingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.datingapp.activity.MessageActivity
import com.example.datingapp.databinding.ItemUserLayoutBinding
import com.example.datingapp.model.UserModel

class DatingAdapter(val context: Context, private val list: ArrayList<UserModel>) :
    Adapter<DatingAdapter.DatingViewHolder>() {

    inner class DatingViewHolder(val binding: ItemUserLayoutBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatingViewHolder {
        return DatingViewHolder(
            ItemUserLayoutBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DatingViewHolder, position: Int) {

        holder.binding.text2.text = list[position].name
        holder.binding.text3.text = list[position].email

        Glide.with(context).load(list[position].image).into(holder.binding.userImage)

        holder.binding.Chat.setOnClickListener {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("userId", list[position].number)
            context.startActivity(intent)
        }
    }
}