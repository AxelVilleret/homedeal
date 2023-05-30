package com.example.homedeal.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.homedeal.App
import com.example.homedeal.R
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.dao.FirebaseOpinionDao
import com.example.homedeal.dao.FirebaseSaveDao
import com.example.homedeal.dao.SaveDao
import com.example.homedeal.databinding.FragmentDetailsBinding
import com.example.homedeal.model.Deal
import com.example.homedeal.utils.ViewUtils
import com.google.android.material.button.MaterialButton

class DetailsFragment : Fragment() {

    companion object {
        private const val TITLE = "Détails"
        private val TAG = "DetailActivity"
        private val AUTH: Auth = AuthWithFirebase
        private val SAVEDAO: SaveDao = FirebaseSaveDao
        private val OPINIONDAO = FirebaseOpinionDao
        private val SAVE = "Sauvegarder"
        private val UNSAVE = "Retirer"
        private val ICSAVE = R.drawable.ic_baseline_bookmark_added_24
        private val ICUNSAVE = R.drawable.ic_baseline_bookmark_remove_24
    }

    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.title = TITLE

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (arguments != null) {
            val getData: Deal = arguments?.getParcelable("android")!!
            Log.d(TAG, "onCreateView: $getData")
            val detailName: TextView = binding.nameDetails
            val detailDesc: TextView = binding.descriptionDetails
            val detailImage: ImageView = binding.imageDetails
            val detailPrice: TextView = binding.priceDetails
            val detailAuthor: TextView = binding.authorDetails
            val detailExp: TextView = binding.expirationDetails
            val openButton: MaterialButton = binding.buttonOpenDetails
            val saveButton: MaterialButton = binding.buttonSaveDetails
            val icShare: ImageButton = binding.shareDetails
            val likeText: TextView = binding.likeItemTextDetails
            val unlikeText: TextView = binding.unlikeItemTextDetails
            val likeView: LinearLayout = binding.likeItemDetails
            val unlikeView: LinearLayout = binding.unlikeItemDetails
            val likeIc: ImageView = binding.likeItemIcDetails
            val unlikeIc: ImageView = binding.unlikeItemIcDetails



            val mail = AUTH.getCurrentUser()?.email

            SAVEDAO.isSaved(mail!!, getData.reference!!) { isSaved, save ->
                setSaveButtonUi(isSaved, saveButton){}
            }

            setLikeUnlikeItem(getData.reference!!, mail!!, likeText, unlikeText)

            detailName.text = getData.name
            detailDesc.text = getData.description
            Glide.with(App.applicationContext)
                .load(Uri.parse(getData.image))
                .into(detailImage)
            detailPrice.text = String.format("%.2f €", getData.price)
            detailAuthor.text = String.format("par %s", getData.creator)
            detailExp.text = String.format("expire le %s", getData.expiration)

            icShare.setOnClickListener {
                shareDeal(getData)
            }

            openButton.setOnClickListener {
                val url = getData.link
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
            saveButton.setOnClickListener {
                SAVEDAO.isSaved(mail!!, getData.reference!!) { isSaved, save ->
                    setSaveButtonUi(!isSaved,saveButton){
                        if(isSaved){
                            SAVEDAO.unsaveDeal(save){}
                        }
                        else
                            SAVEDAO.saveDeal(save) {}
                    }
                }
            }

            likeView.setOnClickListener {
                ViewUtils.playAnimation(likeIc)
                updateLikeUnlikeItem(getData.reference!!, mail!!, likeText, unlikeText, likeText)
            }

            unlikeView.setOnClickListener {
                ViewUtils.playAnimation(unlikeIc)
                updateLikeUnlikeItem(getData.reference!!, mail!!, likeText, unlikeText, unlikeText)
            }

        }
        return root
    }

    fun shareDeal(deal: Deal) {
        val shareString = "Regarde comment c'est le feu : ${deal.name} à seulement ${deal.price} €.\n${deal.link}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareString)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun setSaveButtonUi(isDealSaved: Boolean, saveButton: MaterialButton, onUiReady: () -> Unit) {
        if (isDealSaved) {
            saveButton.text = UNSAVE
            saveButton.setIconResource(ICUNSAVE)
        } else {
            saveButton.text = SAVE
            saveButton.setIconResource(ICSAVE)
        }
        onUiReady()
    }


    fun setLikeUnlikeItem(dealRef: String, user: String, likeView: TextView, unlikeView: TextView) {
        OPINIONDAO.countEvaluations(dealRef) {
            likeView.text = it.nbLikes.toString()
            unlikeView.text = it.nbUnlikes.toString()
            OPINIONDAO.isEvaluated(user, dealRef) { isEvaluated, opinion ->
                if (isEvaluated) {
                    var highlightedView : TextView = if (opinion.isLike!!) likeView else unlikeView
                    highlightedView.setTypeface(null, Typeface.BOLD)
                }
            }
        }
    }

    fun updateLikeUnlikeItem(dealRef: String, user: String, likeView: TextView, unlikeView: TextView, clicked: View) {
        val isLiked = clicked.id == likeView.id
        var highlightedView : TextView = if (isLiked) likeView else unlikeView
        var otherView : TextView = if (!isLiked) likeView else unlikeView
        OPINIONDAO.isEvaluated(user, dealRef) { isEvaluated, opinion ->
            if (!isEvaluated) {
                highlightedView.setTypeface(null, Typeface.BOLD)
                var count = highlightedView.text.toString().toInt() + 1
                highlightedView.text = count.toString()
                OPINIONDAO.evalueDeal(opinion, isLiked){}
            } else {
                val isSame = isLiked == opinion.isLike
                if (!isSame) {
                    highlightedView.setTypeface(null, Typeface.BOLD)
                    var count = highlightedView.text.toString().toInt() + 1
                    highlightedView.text = count.toString()
                    otherView.setTypeface(null, Typeface.NORMAL)
                    count = otherView.text.toString().toInt() - 1
                    otherView.text = count.toString()
                    OPINIONDAO.changeEvaluation(opinion, isLiked) {}
                }
            }
        }
    }

}