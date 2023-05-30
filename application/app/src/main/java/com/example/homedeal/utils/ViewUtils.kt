package com.example.homedeal.utils

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homedeal.R
import java.text.SimpleDateFormat
import java.util.Date

object ViewUtils {

    val PASSWORDS_DONT_MATCH = "Les mots de passe ne correspondent pas"
    val FIELD_REQUIRED = "Ce champ ne peut pas être vide"

    fun playAnimation(view: View) {
        view.animate()
            .scaleX(1.5f)
            .scaleY(1.5f)
            .setDuration(300)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
            }
    }
    fun updateUI(recyclerView: RecyclerView, emptyTextView: TextView, message: String) {
        if (recyclerView.adapter?.itemCount == 0) {
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
            emptyTextView.text = message
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
        }
    }
    fun setPasswordInputType(event: MotionEvent, passwordEditText: EditText) {
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[2].bounds.width())) {
                if (passwordEditText.transformationMethod == PasswordTransformationMethod.getInstance()) {
                    // Afficher le mot de passe en texte clair
                    passwordEditText.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_baseline_lock_24,
                        0,
                        R.drawable.ic_baseline_remove_red_eye_24,
                        0
                    )
                } else {
                    // Masquer le mot de passe
                    passwordEditText.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_baseline_lock_24,
                        0,
                        R.drawable.ic_baseline_visibility_off_24,
                        0
                    )
                }
            }
        }
    }
    fun alertEmptyField(hasFocus: Boolean, field: EditText) {
        if (!hasFocus) {
            val text = field.text.toString()
            if (text.isEmpty()) {
                field.error = FIELD_REQUIRED
            }
        }
    }

    fun convertLongToTime(date: Date): String {
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

}
