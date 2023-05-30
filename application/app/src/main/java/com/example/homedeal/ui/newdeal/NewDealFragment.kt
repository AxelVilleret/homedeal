package com.example.homedeal.ui.newdeal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.homedeal.R
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.dao.DealDao
import com.example.homedeal.dao.FirebaseDealDao
import com.example.homedeal.databinding.FragmentNewdealBinding
import com.example.homedeal.model.Deal
import com.example.homedeal.notifs.FirebaseNotification
import com.example.homedeal.notifs.Notification
import com.example.homedeal.utils.ViewUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewDealFragment : Fragment() {

    companion object {
        private const val TAG = "NewDealFragment"
        private const val TITLE = "Poster un deal"
        private const val MESSAGE = "Ajoute ton meilleur deal 🔥"
        private val DEALDAO: DealDao = FirebaseDealDao
        private val AUTH: Auth = AuthWithFirebase
        private val NOTIF : Notification = FirebaseNotification()
    }

    private var _binding: FragmentNewdealBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewdealBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE

        val buttonSubmit = binding.newdealButton
        val nameField = binding.newdealName
        val descriptionField = binding.newdealDescription
        val priceField = binding.newdealPrice
        val imageField = binding.newdealImage
        val linkField = binding.newdealLink
        val expirationField = binding.newdealDate

        val message = binding.messageNewdeal.message
        message.text = MESSAGE

        expirationField.text = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(System.currentTimeMillis())

        val calendarBox = Calendar.getInstance()
        val dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            calendarBox.set(Calendar.YEAR, year)
            calendarBox.set(Calendar.MONTH, month)
            calendarBox.set(Calendar.DAY_OF_MONTH, day)
            updateText(calendarBox, expirationField)
        }

        expirationField.setOnClickListener {
            context?.let { it1 ->
                val datePickerDialog = DatePickerDialog(
                    it1,

                    dateBox,
                    calendarBox.get(Calendar.YEAR),
                    calendarBox.get(Calendar.MONTH),
                    calendarBox.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                datePickerDialog.show()
            }
        }


        buttonSubmit.setOnClickListener {
            val name = nameField.text.toString()
            val description = descriptionField.text.toString()
            val priceString = priceField.text.toString()
            val image = imageField.text.toString()
            val link = linkField.text.toString()
            val expiration = expirationField.text.toString()
            if (name.isEmpty() || description.isEmpty() || priceString.isEmpty() || image.isEmpty() || link.isEmpty()) {
                if (name.isEmpty()) {
                    nameField.error = ViewUtils.FIELD_REQUIRED
                    nameField.requestFocus()
                }
                if (description.isEmpty()) {
                    descriptionField.error = ViewUtils.FIELD_REQUIRED
                    descriptionField.requestFocus()
                }
                if (priceString.isEmpty()) {
                    priceField.error = ViewUtils.FIELD_REQUIRED
                    priceField.requestFocus()
                }
                if (image.isEmpty()) {
                    imageField.error = ViewUtils.FIELD_REQUIRED
                    imageField.requestFocus()
                }
                if (link.isEmpty()) {
                    linkField.error = ViewUtils.FIELD_REQUIRED
                    linkField.requestFocus()
                }
                return@setOnClickListener

            }
            val price = priceString.toDouble()
            DEALDAO.addDeal(Deal(name, expiration, price, link, description, image, AUTH.getCurrentUser()!!.username!!, null)) {
                NOTIF.subscribeToTopic("add_${it.creator}_${it.name}_1like")
                NOTIF.subscribeToTopic("add_${it.creator}_${it.name}_10like")
                NOTIF.subscribeToTopic("add_${it.creator}_${it.name}_100like")
                val bundle = Bundle()
                bundle.putParcelable("android", it)
                findNavController(this).navigate(R.id.navigation_details, bundle)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        _binding = null
    }

    private fun updateText(calendar: Calendar, textDate: TextView) {
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.FRANCE)
        textDate.setText(sdf.format(calendar.time))
    }
}