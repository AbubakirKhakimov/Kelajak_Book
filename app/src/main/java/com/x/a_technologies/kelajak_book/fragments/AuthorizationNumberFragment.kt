package com.x.a_technologies.kelajak_book.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.activities.MainActivity
import com.x.a_technologies.kelajak_book.databinding.FragmentAuthorizationNumberBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.User
import java.util.concurrent.TimeUnit

class AuthorizationNumberFragment : Fragment() {

    lateinit var binding: FragmentAuthorizationNumberBinding
    lateinit var phoneNumber:String
    companion object {
        var fromInfoFragment = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().window.statusBarColor = resources.getColor(R.color.main_color)
        binding = FragmentAuthorizationNumberBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!fromInfoFragment){
            binding.skip.visibility = View.GONE
        }

        binding.skip.setOnClickListener {
            Hawk.put("isFirstRun", false)
            findNavController().navigate(R.id.action_authorizationNumberFragment_to_homeFragment)
        }

        binding.nextButton.setOnClickListener {
            val countryCode = binding.countryCode.text.toString()
            val number = binding.number.text.toString()
            
            if (countryCode.isEmpty() || number.isEmpty()){
                Toast.makeText(requireActivity(), "Please enter your phone number!", Toast.LENGTH_SHORT).show()
            }else {
                Hawk.put("isFirstRun", false)

                loading(true)
                phoneNumber = "+${countryCode}${number}"

                sendSms()
            }
        }

    }

    private fun sendSms(){
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    verificationFailed()
                }

                override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, p1)
                    codeSend(verificationId)
                }

            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    checkUserDatabase()
                } else {
                    Toast.makeText(requireActivity(), "Error!", Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            }
    }

    private fun checkUserDatabase(){
        DatabaseRef.usersRef.child(Firebase.auth.currentUser!!.phoneNumber!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null){
                        findNavController().navigate(R.id.action_authorizationNumberFragment_to_getUserInfoFragment)
                    }else{
                        UserInfo.currentUser = snapshot.getValue(User::class.java)

                        if (fromInfoFragment){
                            findNavController().navigate(R.id.action_authorizationNumberFragment_to_homeFragment)
                        }else {
                            findNavController().popBackStack()
                        }
                    }

                    loading(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    loading(false)
                }
            })
    }

    private fun codeSend(verificationId: String) {
        findNavController().navigate(R.id.action_authorizationNumberFragment_to_verificationCodeFragment,
            bundleOf(
                "verificationId" to verificationId,
                "phoneNumber" to phoneNumber
            )
        )
        loading(false)
    }

    private fun verificationFailed() {
        Toast.makeText(requireActivity(), "Please enter a valid phone number!", Toast.LENGTH_SHORT).show()
        loading(false)
    }

    fun loading(bool:Boolean){
        if (bool){
            binding.nextButton.visibility = View.GONE
            binding.skip.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.nextButton.visibility = View.VISIBLE
            binding.skip.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

}