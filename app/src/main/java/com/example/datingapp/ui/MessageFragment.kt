package com.example.datingapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.datingapp.adapter.MessageUserAdapter
import com.example.datingapp.databinding.FragmentMessageBinding
import com.example.datingapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(layoutInflater)

        getData()

        return binding.root
    }

    private fun getData() {

        Config.showDialog(requireContext())
        val currentId = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        FirebaseDatabase.getInstance().getReference("chats")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var list = arrayListOf<String>()
                    var newList = arrayListOf<String>()
                    for (data in snapshot.children) {
                        if (data.key!!.contains(currentId!!)) {
                            list.add(data.key!!.replace(currentId, ""))
                            newList.add(data.key!!)
                        }
                    }
                    try {
                        binding.recyclerview.adapter =
                            MessageUserAdapter(requireContext(), list, newList)
                    } catch (_: java.lang.Exception) {

                    }

                    Config.hideDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}