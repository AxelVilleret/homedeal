package com.example.homedeal.ui.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.homedeal.LoginActivity
import com.example.homedeal.R
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.databinding.FragmentProfileBinding
import com.example.homedeal.utils.ThemeUtils

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "ProfileFragment"
        private const val TITLE = ""
        private val AUTH: Auth = AuthWithFirebase
    }

    private var _binding: FragmentProfileBinding? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val user = AUTH.getCurrentUser()
        val usernameTitle = binding.usernameProfile
        val emailTitle = binding.emailProfile
        val buttonLogout = binding.buttonLogoutDetails
        val buttonSavedDeals = binding.buttonSavedDealsDetails
        val buttonUpdateProfile = binding.buttonUpdateProfileDetails
        val buttonCreatedDeals = binding.buttonCreatedDealsDetails


//        val themeSwitchButton = binding.themeSwitchButton
//        themeSwitchButton.setOnClickListener {
//            val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//            if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
//                // Le thème sombre est actuellement activé, basculer vers le thème clair
//                ThemeUtils.setTheme(requireActivity(), "light")
//            } else {
//                // Le thème clair est actuellement activé, basculer vers le thème sombre
//                ThemeUtils.setTheme(requireActivity(), "dark")
//            }
//            // Redémarrez l'activité pour appliquer le nouveau thème
//            requireActivity().recreate()
//        }

        buttonLogout.setOnClickListener {
            AUTH.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        buttonSavedDeals.setOnClickListener {
            findNavController(this).navigate(R.id.navigation_saveddeals)
        }

        buttonUpdateProfile.setOnClickListener {
            findNavController(this).navigate(R.id.navigation_update_profile)
        }

        buttonCreatedDeals.setOnClickListener {
            findNavController(this).navigate(R.id.navigation_created_deals)
        }

        if (user != null) {
            usernameTitle.text = user.username
        }
        if (user != null) {
            emailTitle.text = user.email
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}