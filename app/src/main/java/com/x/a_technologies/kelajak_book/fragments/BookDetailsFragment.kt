package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.ReviewsAdapter
import com.x.a_technologies.kelajak_book.databinding.FragmentBookDetailsBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.datas.UserInfo
import com.x.a_technologies.kelajak_book.models.Book
import com.x.a_technologies.kelajak_book.models.Review
import com.x.a_technologies.kelajak_book.models.UserReviewIds
import java.util.*
import kotlin.collections.ArrayList

class BookDetailsFragment : Fragment() {

    lateinit var binding: FragmentBookDetailsBinding
    lateinit var reviewsAdapter: ReviewsAdapter
    lateinit var currentBook:Book
    var reviewsList = ArrayList<Review>()

    var isNew = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        check()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.reviewEdt.addTextChangedListener {
            if (it!!.isEmpty()){
                binding.sendReview.visibility = View.GONE
            }else{
                binding.sendReview.visibility = View.VISIBLE
            }
        }
        
        binding.sendReview.setOnClickListener { 
            if (UserInfo.currentUser == null){
                Toast.makeText(requireActivity(), "You must register to leave a review.", Toast.LENGTH_SHORT).show()
                AuthorizationNumberFragment.fromInfoFragment = false
                findNavController().navigate(R.id.action_bookDetailsFragment_to_authorizationNumberFragment)
            }else{
                sendReview()
            }
        }

        binding.reviewsCount.setOnClickListener {
            AllReviewsFragment.reviewsList = reviewsList
            findNavController().navigate(R.id.action_bookDetailsFragment_to_allReviewsFragment, bundleOf(
                "bookName" to currentBook.name
            ))
        }

    }

    private fun check(){
        if (isNew){
            isNew = false
            currentBook = arguments?.getSerializable("currentBook") as Book

            reviewsAdapter = ReviewsAdapter(reviewsList, getItemCount())
            init()
            loadCurrentBookReviews()
        }else{
            init()
        }
    }

    private fun init(){
        binding.reviewsRv.adapter = reviewsAdapter
        Glide.with(requireActivity()).load(currentBook.imageUrl).into(binding.bookImage)
        binding.bookName.text = currentBook.name
        binding.bookAuthorName.text = currentBook.author
        binding.bookCount.text = currentBook.count.toString()
        binding.bookPagesCount.text = currentBook.pagesCount.toString()
        binding.bookLanguage.text = currentBook.language
        binding.bookAlphabet.text = currentBook.alphabetType
        binding.bookCoatingType.text = currentBook.coatingType
        binding.bookManufacturingCompany.text = currentBook.manufacturingCompany
        binding.bookRentPrice.text = currentBook.rentPrice
        binding.bookSellingPrice.text = currentBook.sellingPrice
        binding.bookMoreInfo.text = currentBook.moreInformation
        reviewsCountManager()

        if(UserInfo.currentUser != null){
            if (UserInfo.currentUser!!.imageUrl != null){
                Glide.with(requireActivity()).load(UserInfo.currentUser!!.imageUrl).into(binding.currentUserImage)
            }
        }
    }
    
    private fun sendReview(){
        isLoading(true)
        val reviewText = binding.reviewEdt.text.toString()

        val reviewRef = DatabaseRef.reviewsRef.child(currentBook.bookId).push()
        val userReviewIdsRef = DatabaseRef.usersReviewsIdsRef
            .child(UserInfo.currentUser!!.phoneNumber).child(reviewRef.key!!)

        val review = Review(
            reviewRef.key!!
            ,UserInfo.currentUser!!
            ,reviewText
            ,Date().time
            ,null
            ,null)

        val userReviewIds = UserReviewIds(
            currentBook.bookId
            ,reviewRef.key!!)

        userReviewIdsRef.setValue(userReviewIds)

        reviewRef.setValue(review).addOnCompleteListener {
            if (it.isSuccessful){
                binding.reviewEdt.text.clear()
                reviewsList.add(0, review)

                reviewsCountManager()
                reviewsAdapter.setItemCount = getItemCount()
                reviewsAdapter.notifyDataSetChanged()
                binding.noReviewsTitle.visibility = View.GONE
            }else{
                Toast.makeText(requireActivity(), "Error!", Toast.LENGTH_SHORT).show()
            }
            isLoading(false)
        }
    }

    private fun loadCurrentBookReviews(){
        binding.reviewsProgressBar.visibility = View.VISIBLE

        val reviewsQuery = DatabaseRef.reviewsRef.child(currentBook.bookId)
            .orderByChild("reviewSendTimeMillis")

        reviewsQuery.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null){
                    binding.noReviewsTitle.visibility = View.VISIBLE
                }else{
                    for (item in snapshot.children){
                        reviewsList.add(item.getValue(Review::class.java)!!)
                    }

                    if (reviewsList.isEmpty()){
                        binding.noReviewsTitle.visibility = View.VISIBLE
                    }else{
                        reviewsList.reverse()
                    }
                }

                reviewsCountManager()
                reviewsAdapter.setItemCount = getItemCount()
                reviewsAdapter.notifyDataSetChanged()
                binding.reviewsProgressBar.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                binding.reviewsProgressBar.visibility = View.GONE
                binding.noReviewsTitle.visibility = View.VISIBLE
            }
        })
    }

    fun reviewsCountManager(){
        if (reviewsList.size <= 3){
            binding.reviewsCount.visibility = View.GONE
        }else{
            binding.reviewsCount.visibility = View.VISIBLE
            binding.reviewsCount.text = "All ${reviewsList.size} reviews"
        }
    }

    fun getItemCount():Int{
        return if (reviewsList.size <= 3){
            reviewsList.size
        }else{
            3
        }
    }
    
    private fun isLoading(bool:Boolean){
        if (bool){
            binding.sendReview.visibility = View.GONE
            binding.sendingProgressBar.visibility = View.VISIBLE
        }else{
            binding.sendReview.visibility = View.VISIBLE
            binding.sendingProgressBar.visibility = View.GONE
        }
    }

}