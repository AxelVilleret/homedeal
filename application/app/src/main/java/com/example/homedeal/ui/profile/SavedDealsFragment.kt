package com.example.homedeal.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homedeal.R
import com.example.homedeal.adapter.DealAdapter
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.dao.DealDao
import com.example.homedeal.dao.FirebaseDealDao
import com.example.homedeal.dao.FirebaseSaveDao
import com.example.homedeal.dao.SaveDao
import com.example.homedeal.databinding.FragmentSavedDealsBinding
import com.example.homedeal.model.Deal
import com.example.homedeal.utils.ViewUtils

class SavedDealsFragment : Fragment() {

    companion object {
        private const val TAG = "SavedDealsFragment"
        private const val TITLE = "Deals sauvegardés"
        private const val MESSAGE = "Consulte tes deals sauvegardés ici 😀"
        private const val EMPTYMESSAGE = "Vous n'avez pas encore de deals sauvegardés 😥"
        private val ICONRECYCLERVIEW = R.drawable.ic_baseline_bookmark_remove_24
        private val AUTH: Auth = AuthWithFirebase
        private val SAVEDAO: SaveDao = FirebaseSaveDao
        private val DEALDAO: DealDao = FirebaseDealDao
    }

    private var _binding: FragmentSavedDealsBinding? = null

    private lateinit var savesAdapter: DealAdapter
    private var dealsList = ArrayList<Deal>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE
        _binding = FragmentSavedDealsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val mail = AUTH.getCurrentUser()?.email

        val message = binding.messageSaveddeals.message
        message.text = MESSAGE


        SAVEDAO.getSaves(mail) { saves ->
            for (save in saves) {
                Log.d(TAG, "onCreateView: ${save.dealRef}")
                DEALDAO.getDealByRef(save.dealRef) { deal ->
                    dealsList.add(deal)
                    if (dealsList.size == saves.size) {
                        savesAdapter = DealAdapter(dealsList, ICONRECYCLERVIEW, recyclerView)
                        recyclerView.adapter = savesAdapter
                        ViewUtils.updateUI(recyclerView, binding.emptyTextView, EMPTYMESSAGE)
                        savesAdapter.onItemClick = {
                            val bundle = Bundle()
                            bundle.putParcelable("android", it)
                            NavHostFragment.findNavController(this).navigate(R.id.navigation_details, bundle)
                        }

                        savesAdapter.onIconClick = {
                            dealsList.remove(it)
                            recyclerView.adapter?.notifyDataSetChanged()
                            ViewUtils.updateUI(recyclerView, binding.emptyTextView, EMPTYMESSAGE)
                            SAVEDAO.getSavedDealByIdandUser(mail, it.reference!!){ saveToDelete ->
                                SAVEDAO.unsaveDeal(saveToDelete) {}
                            }
                        }
                    }
                }
            }

            if (saves.isEmpty()) {
                savesAdapter = DealAdapter(dealsList, ICONRECYCLERVIEW, recyclerView)
                recyclerView.adapter = savesAdapter
                ViewUtils.updateUI(recyclerView, binding.emptyTextView, EMPTYMESSAGE)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}