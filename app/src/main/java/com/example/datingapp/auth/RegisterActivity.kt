package com.example.datingapp.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.datingapp.MainActivity
import com.example.datingapp.R
import com.example.datingapp.databinding.ActivityRegisterBinding
import com.example.datingapp.model.UserModel
import com.example.datingapp.utils.Config
import com.example.datingapp.utils.Config.hideDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var imageUri: Uri? = null

    private val selectedImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it

        binding.userImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userImage.setOnClickListener {
            selectedImage.launch("image/*")
        }

        binding.saveData.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if (binding.userName.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.enterName), Toast.LENGTH_SHORT).show()
        } else if (binding.userEmail.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.enterEmail), Toast.LENGTH_SHORT).show()
        } else if (binding.userCity.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.enterCity), Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(this, getString(R.string.uploadurphoto), Toast.LENGTH_SHORT).show()
        } else if (!binding.termsCondition.isChecked) {
            Toast.makeText(this, getString(R.string.acceptTermsConditions), Toast.LENGTH_SHORT)
                .show()
        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        Config.showDialog(this)

        val storageReference = FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("profile.jpg")


        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    storeData(it)
                }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                hideDialog()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(imageUrl: Uri?) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result

            val data = UserModel(
                name = binding.userName.text.toString(),
                email = binding.userEmail.text.toString(),
                city = binding.userCity.text.toString(),
                image = imageUrl.toString(),
                fcmToken = token
            )

            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                .setValue(data).addOnCompleteListener {
                    hideDialog()
                    if (it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        Toast.makeText(
                            this,
                            getString(R.string.registerSuccess),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }

        })

    }
}