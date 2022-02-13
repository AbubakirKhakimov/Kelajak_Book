package com.x.a_technologies.kelajak_book.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.FragmentEditProfileBinding
import com.x.a_technologies.kelajak_book.datas.ImageTracker
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.ImageSelectedCallBack
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.Keys
import com.x.a_technologies.kelajak_book.models.User
import com.x.a_technologies.kelajak_book.models.UserReviewIds
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment(), ImageSelectedCallBack {

    lateinit var binding: FragmentEditProfileBinding
    var userReviewsIdsList = ArrayList<UserReviewIds>()
    var isImageChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserReviewsIds()
        init()

        binding.userImage.setOnClickListener {
            chooseImage()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveChanged.setOnClickListener {
            binding.apply {
                if (firstName.text.toString() == UserInfo.currentUser!!.firstName
                    && lastName.text.toString() == UserInfo.currentUser!!.lastName && !isImageChanged){
                    findNavController().popBackStack()
                }else{
                    if (isImageChanged){
                        isLoading(true)
                        writeImageDatabase()
                    }else{
                        isLoading(true)
                        saveChangedDatabase(UserInfo.currentUser!!.imageUrl)
                    }
                }
            }
        }

    }

    private fun saveChangedDatabase(imageUrl:String?){
        val user = User(
            UserInfo.currentUser!!.phoneNumber,
            binding.firstName.text.toString(),
            binding.lastName.text.toString(),
            imageUrl
        )

        val queryData = HashMap<String, Any>()
        for (item in userReviewsIdsList){
            queryData["${Keys.REVIEWS_KEY}/${item.bookId}/${item.reviewId}/senderUser"] = user
        }
        queryData["${Keys.USERS_KEY}/${user.phoneNumber}"] = user

        DatabaseRef.rootRef.updateChildren(queryData).addOnCompleteListener {
            if (it.isSuccessful){
                UserInfo.currentUser = user
                findNavController().popBackStack()
            }else{
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
            isLoading(false)
        }
    }

    private fun writeImageDatabase(){
        val bitmap: Bitmap = binding.userImage.drawable.toBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val imageRef = DatabaseRef.storageRef.child("${UserInfo.currentUser!!.phoneNumber}_user_avatar")
        val uploadTask = imageRef.putBytes(byteArray)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveChangedDatabase(task.result.toString())
            } else {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                isLoading(false)
            }
        }
    }

    private fun loadUserReviewsIds(){
        isLoading(true)
        DatabaseRef.usersReviewsIdsRef.child(UserInfo.currentUser!!.phoneNumber)
            .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userReviewsIdsList.clear()

                for (item in snapshot.children){
                    userReviewsIdsList.add(item.getValue(UserReviewIds::class.java)!!)
                }

                isLoading(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                isLoading(false)
            }
        })
    }

    private fun init(){
        val currentUser = UserInfo.currentUser!!

        binding.apply {
            if (currentUser.imageUrl != null) {
                Glide.with(requireActivity()).load(currentUser.imageUrl).into(userImage)
            }
            firstName.setText(currentUser.firstName)
            lastName.setText(currentUser.lastName)
        }
    }

    private fun chooseImage(){
        val intentChooser = Intent()
        intentChooser.type = "image/"
        intentChooser.action = Intent.ACTION_GET_CONTENT
        ImageTracker.imageSelectedCallBack = this
        startActivityForResult(intentChooser, 1)
    }

    override fun imageSelectedListener(uri: Uri) {
        Glide.with(requireActivity()).load(uri).into(binding.userImage)
        isImageChanged = true
    }

    private fun isLoading(bool:Boolean){
        if (bool){
            binding.saveChanged.visibility = View.GONE
            binding.saveChangedProgressBar.visibility = View.VISIBLE
        }else{
            binding.saveChanged.visibility = View.VISIBLE
            binding.saveChangedProgressBar.visibility = View.GONE
        }
    }

}