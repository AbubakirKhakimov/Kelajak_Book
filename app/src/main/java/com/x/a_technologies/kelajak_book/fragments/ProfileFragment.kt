package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.FragmentProfileBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkCurrentState()

        binding.signIn.setOnClickListener {
            AuthorizationNumberFragment.fromInfoFragment = false
            findTopNavController().navigate(R.id.authorizationNumberFragment)
        }

        binding.signOut.setOnClickListener {
            Firebase.auth.signOut()
            UserInfo.currentUser = null
            checkCurrentState()
        }

    }

    private fun checkCurrentState(){
        val currentUser = UserInfo.currentUser

        binding.apply {
            if (currentUser == null) {
                signOut.visibility = View.GONE
                editProfile.visibility = View.GONE
                signIn.visibility = View.VISIBLE
                userPhoneNumber.visibility = View.GONE
                userName.text = "Anonymous user"
            } else {
                signOut.visibility = View.VISIBLE
                editProfile.visibility = View.VISIBLE
                signIn.visibility = View.GONE
                userPhoneNumber.visibility = View.VISIBLE
                userPhoneNumber.text = currentUser.phoneNumber
                userName.text = "${currentUser.firstName} ${currentUser.lastName}"
            }
        }
    }

}