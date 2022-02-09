package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.FragmentGetUserInfoBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.User

class GetUserInfoFragment : Fragment() {
    
    lateinit var binding: FragmentGetUserInfoBinding
    lateinit var phoneNumber:String
    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGetUserInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumber = Firebase.auth.currentUser!!.phoneNumber!!
        
        binding.nextButton.setOnClickListener { 
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            
            if (firstName.isEmpty()){
                Toast.makeText(requireActivity(), "Please write your first name!", Toast.LENGTH_SHORT).show()
            }else{
                loading(true)
                user = User(phoneNumber, firstName, lastName, null)

                DatabaseRef.usersRef.child(phoneNumber).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful){
                        UserInfo.currentUser = user

                        if (AuthorizationNumberFragment.fromInfoFragment){
                            findNavController().navigate(R.id.action_getUserInfoFragment_to_homeFragment)
                        }else{
                            findNavController().popBackStack(R.id.authorizationNumberFragment, true)
                        }
                        loading(false)
                    }else{
                        Toast.makeText(requireActivity(), "Error!", Toast.LENGTH_SHORT).show()
                        loading(false)
                    }
                }
            }
        }

    }

    fun loading(bool:Boolean){
        if (bool){
            binding.nextButton.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.nextButton.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

}