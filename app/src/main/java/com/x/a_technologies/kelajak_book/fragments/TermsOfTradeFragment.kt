package com.x.a_technologies.kelajak_book.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.x.a_technologies.kelajak_book.R
import com.x.a_technologies.kelajak_book.adapters.TermsOfTradeAdapter
import com.x.a_technologies.kelajak_book.databinding.FragmentTermsOfTradeBinding
import com.x.a_technologies.kelajak_book.datas.DatabaseRef
import com.x.a_technologies.kelajak_book.models.TermsOfTrade

class TermsOfTradeFragment : Fragment() {

    lateinit var binding: FragmentTermsOfTradeBinding
    lateinit var termsOfTradeAdapter: TermsOfTradeAdapter
    var termsList = ArrayList<TermsOfTrade>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTermsOfTradeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        termsOfTradeAdapter = TermsOfTradeAdapter(termsList)
        binding.termsRv.adapter = termsOfTradeAdapter
        loadTerms()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun loadTerms(){
        binding.progressBar.visibility = View.VISIBLE
        DatabaseRef.termsOfTradeRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children){
                    termsList.add(item.getValue(TermsOfTrade::class.java)!!)
                }

                termsOfTradeAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

}