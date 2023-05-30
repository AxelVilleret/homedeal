package com.example.homedeal.ui.profile

import android.os.Bundle
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
import com.example.homedeal.dao.AlerteDao
import com.example.homedeal.dao.CreationDao
import com.example.homedeal.dao.DealDao
import com.example.homedeal.dao.FirebaseAlerteDao
import com.example.homedeal.dao.FirebaseCreationDao
import com.example.homedeal.dao.FirebaseDealDao
import com.example.homedeal.dao.FirebaseOpinionDao
import com.example.homedeal.dao.FirebaseSaveDao
import com.example.homedeal.dao.SaveDao
import com.example.homedeal.databinding.FragmentCreatedDealsBinding
import com.example.homedeal.model.Deal
import com.example.homedeal.utils.ViewUtils

class CreatedDealsFragment : Fragment() {

    companion object {
        private const val TAG = "CreatedDealsFragment"
        private const val TITLE = "Mes deals"
        private val ICONRECYCLERVIEW = R.drawable.ic_baseline_remove_24
        private val AUTH: Auth = AuthWithFirebase
        private val DEALDAO: DealDao = FirebaseDealDao
        private var CREATIONDAO: CreationDao = FirebaseCreationDao
        private val SAVEDAO: SaveDao = FirebaseSaveDao
        private val OPINIONDAO = FirebaseOpinionDao
        private val ALERTEDAO: AlerteDao = FirebaseAlerteDao
        private var MESSAGE = "Une vue sur tous tes deals 🔥"
        private const val EMPTYMESSAGE = "Vous n'avez pas encore créé de deal 😥"
    }

    private lateinit var creationsAdapter: DealAdapter
    private var dealsList = ArrayList<Deal>()

    private var _binding: FragmentCreatedDealsBinding? = null

    private var isRestoringViewState = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatedDealsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val message = binding.messageCreateddeals.message
        message.text = MESSAGE

        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val mail = AUTH.getCurrentUser()?.email

        CREATIONDAO.getCreations(mail) { creations ->
            for (creation in creations) {
                DEALDAO.getDealByRef(creation.dealRef) { deal ->
                    dealsList.add(deal)
                    if (dealsList.size == creations.size) {
                        creationsAdapter = DealAdapter(dealsList, ICONRECYCLERVIEW, recyclerView)
                        recyclerView.adapter = creationsAdapter
                        ViewUtils.updateUI(recyclerView, binding.emptyTextView,
                            EMPTYMESSAGE
                        )
                        creationsAdapter.onItemClick = {
                            val bundle = Bundle()
                            bundle.putParcelable("android", it)
                            NavHostFragment.findNavController(this).navigate(R.id.navigation_details, bundle)
                        }

                        creationsAdapter.onIconClick = {
                            dealsList.remove(it)
                            recyclerView.adapter?.notifyDataSetChanged()
                            ViewUtils.updateUI(recyclerView, binding.emptyTextView,
                                EMPTYMESSAGE
                            )
                            CREATIONDAO.getCreation(it.reference!!) { creation ->
                                CREATIONDAO.deleteCreation(creation) {
                                    DEALDAO.deleteDeal(it.reference!!) {
                                        OPINIONDAO.deleteEvaluations(it.reference!!) {
                                            SAVEDAO.deleteSaves(it.reference!!) {
                                                ALERTEDAO.deleteAlerte(it.reference!!) {
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (creations.isEmpty()) {
                creationsAdapter = DealAdapter(dealsList, ICONRECYCLERVIEW, recyclerView)
                recyclerView.adapter = creationsAdapter
                ViewUtils.updateUI(recyclerView, binding.emptyTextView,
                    EMPTYMESSAGE
                )
            }
        }

        return root
    }

}