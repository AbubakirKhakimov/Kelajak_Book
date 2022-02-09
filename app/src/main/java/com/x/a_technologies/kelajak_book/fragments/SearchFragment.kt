package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.kelajak_book.adapters.SearchResultsAdapter
import com.x.a_technologies.kelajak_book.adapters.TopBookSearchAdapter
import com.x.a_technologies.kelajak_book.databinding.FragmentSearchBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.models.Book

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var searchResultAdapter: SearchResultsAdapter
    lateinit var topBookSearchAdapter: TopBookSearchAdapter
    var isNew = true

    var topBooksList:ArrayList<Book> = ArrayList()

    companion object{
        var allBooksList = ArrayList<Book>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        check()

        binding.searchEdt.addTextChangedListener{
            if (it.toString().isEmpty()){
                binding.toBookSearchRv.visibility = View.VISIBLE
                binding.title.visibility = View.VISIBLE
                binding.searchResultsRv.visibility = View.GONE
            }else {
                binding.toBookSearchRv.visibility = View.GONE
                binding.title.visibility = View.GONE
                binding.searchResultsRv.visibility = View.VISIBLE
            }

            searchResultAdapter.filter.filter(it.toString())
        }

    }

    private fun loadTopBookSearch(){
        binding.progressBar.visibility = View.VISIBLE

        val topSearchQuery = DatabaseRef.booksRef.orderByChild("searchedCount").limitToFirst(16)
        topSearchQuery.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                topBooksList.clear()

                for (item in snapshot.children){
                    topBooksList.add(item.getValue(Book::class.java)!!)
                }

                topBookSearchAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun check(){
        if (isNew){
            isNew = false

            searchResultAdapter = SearchResultsAdapter(allBooksList)
            topBookSearchAdapter = TopBookSearchAdapter(topBooksList)
            initRecycler()

            loadTopBookSearch()
        }else{
            initRecycler()
        }
    }

    private fun initRecycler(){
        binding.searchResultsRv.adapter = searchResultAdapter
        binding.toBookSearchRv.adapter = topBookSearchAdapter
    }

}