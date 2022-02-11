package com.x.a_technologies.kelajak_book.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.activities.MainActivity
import com.x.a_technologies.kelajak_book.databinding.FragmentSplashBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.User

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Firebase.auth.currentUser == null){
            binding.title.postDelayed({
                if (isFirstRun()){
                    findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
                }else{
                    findNavController().navigate(R.id.action_splashFragment_to_tabsFragment)
                }
            }, 3000)
        }else{
            checkUserDatabase()
        }

    }

    private fun checkUserDatabase(){
        DatabaseRef.usersRef.child(Firebase.auth.currentUser!!.phoneNumber!!)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null){
                        findNavController().navigate(R.id.action_splashFragment_to_getUserInfoFragment)
                    }else{
                        UserInfo.currentUser = snapshot.getValue(User::class.java)

                        findNavController().navigate(R.id.action_splashFragment_to_tabsFragment)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun isFirstRun():Boolean{
        return Hawk.get("isFirstRun", true)
    }

}