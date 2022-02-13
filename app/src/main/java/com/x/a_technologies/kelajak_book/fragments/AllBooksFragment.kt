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
import com.x.a_technologies.kelajak_book.models.Book
import java.util.*
import kotlin.collections.ArrayList

class AllBooksFragment : Fragment(), BooksByCategoriesCallBack {

    lateinit var binding: FragmentAllBooksBinding
    lateinit var booksByCategoriesAdapter: BooksByCategoriesAdapter

    lateinit var categoryName:String
    var booksList = ArrayList<Book>()

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
        booksList = arguments?.getParcelableArrayList("booksList")!!

        booksByCategoriesAdapter = BooksByCategoriesAdapter(booksList, this, requireActivity())
        binding.apply {
            booksByCategoriesRv.adapter = booksByCategoriesAdapter
            categoryNameTv.text = getStringRes(categoryName)
            booksCount.text = "${booksList.size} ${getString(R.string.book)}"
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun getStringRes(text:String):String{
        return when(Locale.getDefault().language){
            "ru" -> "${getString(R.string.all_books_in_the_category)} \"$text\""
            "uz" -> "\"$text\" ${getString(R.string.all_books_in_the_category)}"
            else -> "${getString(R.string.all_books_in_the_category)} \"$text\""
        }
    }

    override fun booksByCategoriesItemClickListener(position: Int) {
        findNavController().navigate(
            R.id.action_allBooksFragment_to_bookDetailsFragment, bundleOf(
                "currentBook" to booksList[position]
            )
        )
    }

}