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

    lateinit var bookMarkAdapter:BookmarkAdapter
    lateinit var binding: FragmentBookmarkBinding

    var bookmarkList = ArrayList<String>()
    var booksList = ArrayList<Book>()

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
        bookMarkAdapter = BookmarkAdapter(booksList, this, requireActivity())
        binding.booksListRv.adapter = bookMarkAdapter

        binding.swipeRefresh.setOnRefreshListener {
            searchInAllBooks()
        }

    }

    private fun read(){
        bookmarkList = Hawk.get("bookmarkList", ArrayList())
        searchInAllBooks()
    }

    private fun write(){
        Hawk.put("bookmarkList", bookmarkList)
    }

    private fun searchInAllBooks(){
        if (SearchFragment.allBooksList.isNotEmpty()) {
            binding.swipeRefresh.isRefreshing = true
            booksList.clear()
            Thread {
                val allBooksList = SearchFragment.allBooksList
                for (id in bookmarkList) {
                    for (i in allBooksList.indices) {
                        if (id == allBooksList[i].bookId) {
                            booksList.add(allBooksList[i])
                            break
                        }else if (i == allBooksList.size-1){
                            bookmarkList.remove(id)
                            write()
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    bookMarkAdapter.notifyDataSetChanged()
                    binding.swipeRefresh.isRefreshing = false
                }
            }.start()
        }else{
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun bookmarkItemClickListener(position: Int) {
        findNavController().navigate(R.id.action_bookmarkFragment_to_bookDetailsFragment, bundleOf(
            "currentBook" to booksList[position]
        ))
    }

    override fun bookmarkItemRemovedListener(position: Int) {
        bookmarkList.removeAt(position)
        write()
    }

}