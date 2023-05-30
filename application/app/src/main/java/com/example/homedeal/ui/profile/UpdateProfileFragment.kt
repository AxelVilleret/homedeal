package com.example.homedeal.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.homedeal.R
import com.example.homedeal.adapter.DealAdapter
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.auth.TaskResult
import com.example.homedeal.databinding.FragmentSavedDealsBinding
import com.example.homedeal.databinding.FragmentUpdateProfileBinding
import com.example.homedeal.model.Deal
import com.example.homedeal.utils.ViewUtils
import java.lang.Exception

class UpdateProfileFragment : Fragment() {

    companion object {
        private const val TAG = "UpdateProfileFragment"
        private const val TITLE = "Modifier mon profil"
        private const val MESSAGE = "Tu peux modifier ton nom ou ton mot de passe ici 😎"
        private val AUTH: Auth = AuthWithFirebase
    }

    private var _binding: FragmentUpdateProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val updateNameField = binding.updateName
        val updatePasswordField = binding.updatePassword
        val updateConfirmField = binding.updateConfirm
        val updateButton = binding.updateButton

        val message = binding.messageUpdate.message
        message.text = MESSAGE
        updateNameField.setText(AUTH.getCurrentUser()?.username)

        updateButton.setOnClickListener {
            val username = updateNameField.text.toString()
            val password = updatePasswordField.text.toString()
            val confirm = updateConfirmField.text.toString()
            if (password == confirm && !password.isEmpty()) {
                AUTH.updatePassword(password, updatePasswordField, updateNameField, object: TaskResult {
                    override fun onSuccess() {
                        if (username != AUTH.getCurrentUser()?.username && !username.isEmpty()) {
                            AUTH.updateUsername(username) {
                                findNavController(this@UpdateProfileFragment).navigate(R.id.navigation_profile)
                            }
                        }
                        findNavController(this@UpdateProfileFragment).navigate(R.id.navigation_profile)
                    }
                    override fun onFailure(exception: Exception) {}
                })
            } else if (password.isEmpty()) {
                if (username != AUTH.getCurrentUser()?.username && !username.isEmpty()) {
                    AUTH.updateUsername(username) {
                        findNavController(this).navigate(R.id.navigation_profile)
                    }
                }
            } else {
                updatePasswordField.error = ViewUtils.PASSWORDS_DONT_MATCH
                updateConfirmField.error = ViewUtils.PASSWORDS_DONT_MATCH
            }
        }

        updatePasswordField.setOnTouchListener { v, event ->
            ViewUtils.setPasswordInputType(event, v as EditText)
            false
        }

        updateConfirmField.setOnTouchListener { v, event ->
            ViewUtils.setPasswordInputType(event, v as EditText)
            false
        }

        updatePasswordField.setOnFocusChangeListener { v, hasFocus ->
            ViewUtils.alertEmptyField(hasFocus, v as EditText)
        }

        updateConfirmField.setOnFocusChangeListener { v, hasFocus ->
            ViewUtils.alertEmptyField(hasFocus, v as EditText)
        }

        return root
    }



}