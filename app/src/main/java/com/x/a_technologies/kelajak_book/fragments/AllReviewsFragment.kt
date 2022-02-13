package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.ReviewsAdapter
import com.x.a_technologies.kelajak_book.databinding.FragmentAllReviewsBinding
import com.x.a_technologies.kelajak_book.models.Review
import java.util.*
import kotlin.collections.ArrayList

class AllReviewsFragment : Fragment() {

    lateinit var binding: FragmentAllReviewsBinding
    lateinit var reviewsAdapter: ReviewsAdapter

    lateinit var bookName:String
    var reviewsList = ArrayList<Review>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllReviewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookName = arguments?.getString("bookName")!!
        reviewsList = arguments?.getParcelableArrayList("reviewsList")!!

        binding.bookName.text = getStringRes(bookName)
        binding.reviewsCount.text = "${reviewsList!!.size} ${getString(R.string.reviews)}"

        reviewsAdapter = ReviewsAdapter(reviewsList, true)
        binding.allReviewsRv.adapter = reviewsAdapter

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun getStringRes(text:String):String{
        return when(Locale.getDefault().language){
            "ru" -> "${getString(R.string.all_reviews_of)} \"$text\""
            "uz" -> "\"$text\" ${getString(R.string.all_reviews_of)}"
            else -> "${getString(R.string.all_reviews_of)} \"$text\""
        }
    }

}