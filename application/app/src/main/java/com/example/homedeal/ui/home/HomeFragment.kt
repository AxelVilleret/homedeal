package com.example.homedeal.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homedeal.R
import com.example.homedeal.adapter.DealAdapter
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.dao.DealDao
import com.example.homedeal.dao.FirebaseDealDao
import com.example.homedeal.dao.FirebaseSaveDao
import com.example.homedeal.dao.SaveDao
import com.example.homedeal.databinding.FragmentHomeBinding
import com.example.homedeal.model.Deal
import com.example.homedeal.model.Save
import java.util.Locale


class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
        private const val TITLE = "En vedette"
        private val ICONRECYCLERVIEW = R.drawable.ic_baseline_bookmark_added_24
        private val AUTH: Auth = AuthWithFirebase
        private val DEALDAO: DealDao = FirebaseDealDao
        private var SAVEDAO: SaveDao = FirebaseSaveDao
        private var MESSAGE = "Mate les pépites du moment ${AUTH.getCurrentUser()?.username} 👋"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<Deal>
    private lateinit var myAdapter: DealAdapter
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<Deal>

    private var _binding: FragmentHomeBinding? = null

    private var isRestoringViewState = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE

        recyclerView = binding.recyclerView
        searchView = binding.search
        val welcomeMessage = binding.messageHome.message
        welcomeMessage.text = MESSAGE
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        dataList = arrayListOf<Deal>()
        searchList = arrayListOf<Deal>()
        DEALDAO.getDeals {
            dataList = it
            searchList.addAll(dataList)
            myAdapter = DealAdapter(searchList, ICONRECYCLERVIEW, recyclerView)
            recyclerView.adapter = myAdapter
            myAdapter.onBinding = { deal ->
                AUTH.getCurrentUser()?.email?.let {
                    SAVEDAO.isSaved(it, deal.reference!!) { isSaved, save ->
                        if (isSaved) {
                            val iconView = myAdapter.getIconView(deal)
                            iconView?.apply {
                                visibility = View.GONE
                                isClickable = false
                            }
                        }
                    }
                }
            }
            myAdapter.onItemClick = {
                val bundle = Bundle()
                bundle.putParcelable("android", it)
                NavHostFragment.findNavController(this).navigate(R.id.navigation_details, bundle)

            }
            myAdapter.onIconClick = {
                AUTH.getCurrentUser()?.email?.let { it1 ->
                    SAVEDAO.saveDeal(Save(null, it.reference!!, it1)) {
                        val iconView = myAdapter.getIconView(it)
                        iconView?.apply {
                            visibility = View.GONE
                            isClickable = false
                        }
                    }
                }
            }

        }
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: $newText")
                searchList.clear()
                val searchText = newText?.lowercase(Locale.getDefault()) ?: ""
                Log.d(TAG, "onQueryTextChange: $searchText")
                if (searchText.isNotEmpty()) {
                    dataList.forEach {
                        if (it.name.lowercase(Locale.getDefault()).contains(searchText)) {
                            searchList.add(it)
                        }
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                } else {
                    searchList.addAll(dataList)
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                return false
            }

        })
        return root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        isRestoringViewState = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isRestoringViewState = false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}