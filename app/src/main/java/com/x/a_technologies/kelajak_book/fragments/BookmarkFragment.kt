package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.BookmarkAdapter
import com.x.a_technologies.kelajak_book.adapters.BookmarkCallBack
import com.x.a_technologies.kelajak_book.databinding.FragmentBookmarkBinding
import com.x.a_technologies.kelajak_book.models.Book

class BookmarkFragment : Fragment(), BookmarkCallBack {

    var bookmarkList = ArrayList<Book>()
    lateinit var bookMarkAdapter:BookmarkAdapter
    lateinit var binding: FragmentBookmarkBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        read()
        bookMarkAdapter = BookmarkAdapter(bookmarkList, this)
        binding.booksListRv.adapter = bookMarkAdapter

    }

    private fun read(){
        bookmarkList = Hawk.get("bookmarkList", ArrayList())
    }

    override fun bookmarkItemClickListener(position: Int) {
        findNavController().navigate(R.id.action_bookmarkFragment_to_bookDetailsFragment, bundleOf(
            "currentBook" to bookmarkList[position]
        ))
    }

}