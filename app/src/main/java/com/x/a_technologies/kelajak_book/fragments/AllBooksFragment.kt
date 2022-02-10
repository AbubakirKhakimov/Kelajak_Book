package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.BooksByCategoriesAdapter
import com.x.a_technologies.kelajak_book.adapters.BooksByCategoriesCallBack
import com.x.a_technologies.kelajak_book.databinding.FragmentAllBooksBinding

class AllBooksFragment : Fragment(), BooksByCategoriesCallBack {

    lateinit var binding: FragmentAllBooksBinding
    lateinit var booksByCategoriesAdapter: BooksByCategoriesAdapter

    lateinit var categoryName:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllBooksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryName = arguments?.getString("categoryName")!!

        booksByCategoriesAdapter = BooksByCategoriesAdapter(HomeFragment.booksList, this)
        binding.apply {
            booksByCategoriesRv.adapter = booksByCategoriesAdapter
            categoryNameTv.text = "\" $categoryName \""
            booksCount.text = "${HomeFragment.booksList.size} book"
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun booksByCategoriesItemClickListener(position: Int) {
        findNavController().navigate(
            R.id.action_allBooksFragment_to_bookDetailsFragment, bundleOf(
                "currentBook" to HomeFragment.booksList[position]
            )
        )
    }

}