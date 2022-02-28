package com.x.a_technologies.kelajak_book.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.databinding.AboutProgramDialogLayoutBinding
import com.x.a_technologies.kelajak_book.databinding.ContactUsDialogLayoutBinding
import com.x.a_technologies.kelajak_book.databinding.FragmentProfileBinding
import com.x.a_technologies.kelajak_book.databinding.SignOutDialogLayoutBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.SocialMediaReferences

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
            showSignOutDialog()
        }

        binding.editProfile.setOnClickListener {
            findTopNavController().navigate(R.id.editProfileFragment)
        }

        binding.aboutProgram.setOnClickListener {
            showAboutProgramDialog()
        }

        binding.language.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeLanguageFragment)
        }

        binding.termsTrade.setOnClickListener {
            findTopNavController().navigate(R.id.termsOfTradeFragment)
        }

        binding.contactUs.setOnClickListener {
            showContactUsDialog()
        }

    }

    private fun showContactUsDialog(){
        var socialMedia:SocialMediaReferences? = null
        val customDialog = AlertDialog.Builder(requireActivity()).create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = ContactUsDialogLayoutBinding.inflate(layoutInflater)
        customDialog.setView(dialogBinding.root)

        dialogBinding.progressBar.visibility = View.VISIBLE
        dialogBinding.constraintLayout.visibility = View.GONE
        DatabaseRef.socialMediaRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                socialMedia = snapshot.getValue(SocialMediaReferences::class.java)

                if (socialMedia == null){
                    Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                    customDialog.dismiss()
                }
                dialogBinding.progressBar.visibility = View.GONE
                dialogBinding.constraintLayout.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        })

        dialogBinding.phoneNumber.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${socialMedia!!.phoneNumber}"))
            startActivity(callIntent)
        }

        dialogBinding.telegram.setOnClickListener {
            val callUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(socialMedia!!.telegramUrl))
            startActivity(callUrlIntent)
        }

        dialogBinding.instagram.setOnClickListener {
            val callUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(socialMedia!!.instagramUrl))
            startActivity(callUrlIntent)
        }

        customDialog.show()
    }

    private fun showAboutProgramDialog(){
        val customDialog = AlertDialog.Builder(requireActivity()).create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = AboutProgramDialogLayoutBinding.inflate(layoutInflater)
        customDialog.setView(dialogBinding.root)

        dialogBinding.ok.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun showSignOutDialog(){
        val customDialog = AlertDialog.Builder(requireActivity()).create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = SignOutDialogLayoutBinding.inflate(layoutInflater)
        customDialog.setView(dialogBinding.root)

        dialogBinding.noButton.setOnClickListener {
            customDialog.dismiss()
        }
        dialogBinding.yesButton.setOnClickListener {
            Firebase.auth.signOut()
            UserInfo.currentUser = null
            checkCurrentState()
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun checkCurrentState(){
        val currentUser = UserInfo.currentUser

        binding.apply {
            if (currentUser == null) {
                signOut.visibility = View.GONE
                editProfile.visibility = View.GONE
                signIn.visibility = View.VISIBLE
                userPhoneNumber.visibility = View.GONE
                userName.text = getString(R.string.anonymous_user)
                userImage.setImageResource(R.drawable.user_profile_human)
            } else {
                signOut.visibility = View.VISIBLE
                editProfile.visibility = View.VISIBLE
                signIn.visibility = View.GONE
                userPhoneNumber.visibility = View.VISIBLE
                userPhoneNumber.text = currentUser.phoneNumber
                userName.text = "${currentUser.firstName} ${currentUser.lastName}"
                if (currentUser.imageUrl != null){
                    Glide.with(requireActivity()).load(currentUser.imageUrl).into(userImage)
                }else{
                    userImage.setImageResource(R.drawable.user_profile_human)
                }
            }
        }
    }

}