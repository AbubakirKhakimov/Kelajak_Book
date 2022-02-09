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

class AllReviewsFragment : Fragment() {

    lateinit var binding: FragmentAllReviewsBinding
    lateinit var reviewsAdapter: ReviewsAdapter
    lateinit var bookName:String
    companion object {
        var reviewsList:ArrayList<Review>? = null
    }

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

        binding.bookName.text = "Reviews of \" ${bookName} \""
        binding.reviewsCount.text = "${reviewsList!!.size} reviews"

        reviewsAdapter = ReviewsAdapter(reviewsList!!, reviewsList!!.size)
        binding.allReviewsRv.adapter = reviewsAdapter

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        reviewsList = null
    }

}