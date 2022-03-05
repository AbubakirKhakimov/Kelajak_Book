package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.FragmentAuthorizationNumberBinding
import java.util.concurrent.TimeUnit

interface VerificationCompleted{
    fun verificationCompletedListener(credential: PhoneAuthCredential)
}

class AuthorizationNumberFragment : Fragment() {

    lateinit var binding: FragmentAuthorizationNumberBinding
    lateinit var phoneNumber:String
    companion object {
        var fromInfoFragment = true
        var verificationCompleted:VerificationCompleted? = null
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
            findNavController().navigate(R.id.action_authorizationNumberFragment_to_tabsFragment)
        }

        binding.nextButton.setOnClickListener {
            val countryCode = binding.countryCode.text.toString()
            val number = binding.number.text.toString()
            
            if (countryCode.isEmpty() || number.isEmpty()){
                Toast.makeText(requireActivity(), getString(R.string.please_enter_your_phone_number), Toast.LENGTH_SHORT).show()
            }else {
                Hawk.put("isFirstRun", false)

                loading(true)
                phoneNumber = "+${countryCode}${number}"

                sendSms()
            }
        }

    }

    private fun sendSms(){
        Firebase.auth.useAppLanguage()
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    if (verificationCompleted != null) {
                        verificationCompleted!!.verificationCompletedListener(credential)
                    }
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
        Toast.makeText(requireActivity(), getString(R.string.please_enter_a_valid_phone_number), Toast.LENGTH_SHORT).show()
        loading(false)
    }

    private fun loading(bool:Boolean){
        if (bool){
            binding.nextButton.visibility = View.GONE
            binding.skip.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.nextButton.visibility = View.VISIBLE
            if (fromInfoFragment) {
                binding.skip.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }
    }

}