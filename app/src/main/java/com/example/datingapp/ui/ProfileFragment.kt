package com.example.datingapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.datingapp.R
import com.example.datingapp.activity.EditProfileActivity
import com.example.datingapp.auth.LoginActivity
import com.example.datingapp.databinding.FragmentProfileBinding
import com.example.datingapp.model.UserModel
import com.example.datingapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        Config.showDialog(requireContext())

        binding = FragmentProfileBinding.inflate(layoutInflater)

        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val data = it.getValue(UserModel::class.java)

                    binding.name.setText(data!!.name.toString())
                    binding.city.setText(data.city.toString())
                    binding.email.setText(data.email.toString())
                    binding.number.setText(data.number.toString())

                    Glide.with(requireContext()).load(data.image).placeholder(R.drawable.profile)
                        .into(binding.userImage)

                    Config.hideDialog()
                }
            }

        binding.Logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.editProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        return binding.root
    }
}