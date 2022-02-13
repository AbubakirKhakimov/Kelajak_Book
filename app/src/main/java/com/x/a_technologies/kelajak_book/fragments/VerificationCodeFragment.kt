package com.x.a_technologies.kelajak_book.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.activities.MainActivity
import com.x.a_technologies.kelajak_book.databinding.FragmentVerificationCodeBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.User

class VerificationCodeFragment : Fragment() {

    lateinit var binding: FragmentVerificationCodeBinding
    lateinit var phoneNumber:String
    lateinit var verificationId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVerificationCodeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumber = arguments?.getString("phoneNumber")!!
        verificationId = arguments?.getString("verificationId")!!

        listeners()
    }

    private fun verification(){
        val credential = PhoneAuthProvider.getCredential(verificationId, getCode())
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    checkUserDatabase()
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.invalid_code_please_try_again), Toast.LENGTH_SHORT).show()
                    loading(false)
                    clearEditText()
                }
            }
    }

    private fun checkUserDatabase(){
        DatabaseRef.usersRef.child(Firebase.auth.currentUser!!.phoneNumber!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null){
                        findNavController().navigate(R.id.action_verificationCodeFragment_to_getUserInfoFragment)
                    }else{
                        UserInfo.currentUser = snapshot.getValue(User::class.java)

                        if (AuthorizationNumberFragment.fromInfoFragment){
                            findNavController().navigate(R.id.action_verificationCodeFragment_to_tabsFragment)
                        }else {
                            findNavController().popBackStack(R.id.authorizationNumberFragment, true)
                        }
                        showSnackBar(getString(R.string.you_have_successfully_sign_in_system))
                    }

                    loading(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                    loading(false)
                }
            })
    }

    fun showSnackBar(text:String){
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    private fun loading(bool: Boolean) {
        if (bool){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getCode():String{
        var code = ""
        for (i in 0..5){
            code += getEditText(i).text.toString()
        }
        return code
    }

    private fun clearEditText(){
        for (i in 0..5){
            val editText = getEditText(i)
            editText.text.clear()
            editText.clearFocus()
        }
        getEditText(0).requestFocus()
    }

    private fun editTextController(position: Int, it: String) {
        if (it == "" && position != 0) {
            getEditText(position).clearFocus()
            getEditText(position-1).requestFocus()
        } else if (it != "" && position != 5){
            getEditText(position).clearFocus()
            getEditText(position+1).requestFocus()
        } else if (position == 5){
            val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(getEditText(5).windowToken, 0)
            getEditText(5).clearFocus()

            loading(true)
            verification()
        }
    }

    private fun listeners(){
        binding.oneCode.addTextChangedListener {
            editTextController(0, it.toString())
        }
        binding.twoCode.addTextChangedListener {
            editTextController(1, it.toString())
        }
        binding.threeCode.addTextChangedListener {
            editTextController(2, it.toString())
        }
        binding.fourCode.addTextChangedListener {
            editTextController(3, it.toString())
        }
        binding.fiveCode.addTextChangedListener {
            editTextController(4, it.toString())
        }
        binding.sixCode.addTextChangedListener {
            editTextController(5, it.toString())
        }
    }

    private fun getEditText(position:Int): EditText {
        return when(position){
            0 -> binding.oneCode
            1 -> binding.twoCode
            2 -> binding.threeCode
            3 -> binding.fourCode
            4 -> binding.fiveCode
            5 -> binding.sixCode
            else -> binding.oneCode
        }
    }

}