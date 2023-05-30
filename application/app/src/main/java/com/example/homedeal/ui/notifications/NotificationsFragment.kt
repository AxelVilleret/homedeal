package com.example.homedeal.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homedeal.R
import com.example.homedeal.adapter.AlerteAdapter
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.dao.AlerteDao
import com.example.homedeal.dao.FirebaseAlerteDao
import com.example.homedeal.databinding.FragmentNotificationsBinding
import com.example.homedeal.model.Alerte
import com.example.homedeal.ui.home.HomeFragment
import com.example.homedeal.ui.profile.CreatedDealsFragment
import com.example.homedeal.utils.ViewUtils

class NotificationsFragment : Fragment() {

    companion object {
        private const val TAG = "NotificationsFragment"
        private const val TITLE = "Alertes"
        private var MESSAGE = "Reste informé en temps réel des deals que tu as lancé 🤩"
        private val AUTH: Auth = AuthWithFirebase
        private val ALERTEDAO: AlerteDao = FirebaseAlerteDao
        private val ICONRECYCLERVIEW = R.drawable.ic_baseline_remove_24
        private const val EMPTYMESSAGE = "Vous êtes à jour dans vos alertes 😎"
    }

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE

        val message = binding.messageAlertes.message
        message.text = MESSAGE

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val mail = AUTH.getCurrentUser()?.email

        ALERTEDAO.getAlertesByUser(mail!!) { alertes ->
            val alerteAdapter = AlerteAdapter(alertes as ArrayList<Alerte>, ICONRECYCLERVIEW)
            recyclerView.adapter = alerteAdapter
            ViewUtils.updateUI(
                recyclerView, binding.emptyTextView,
                EMPTYMESSAGE
            )
            alerteAdapter.onItemClick = {
                val bundle = Bundle()
                bundle.putParcelable("android", it)
                findNavController(this).navigate(R.id.navigation_details, bundle)
            }

            alerteAdapter.onIconClick = {
                ALERTEDAO.deleteAlerte(it.dealRef) {
                    alertes.remove(it)
                    alerteAdapter.notifyDataSetChanged()
                    ViewUtils.updateUI(
                        recyclerView, binding.emptyTextView,
                        EMPTYMESSAGE
                    )
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}