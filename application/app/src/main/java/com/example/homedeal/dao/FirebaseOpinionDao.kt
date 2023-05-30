package com.example.homedeal.dao

import com.example.homedeal.model.Deal
import com.example.homedeal.model.Evaluation
import com.example.homedeal.model.EvaluationsCounter
import com.example.homedeal.model.Save
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseOpinionDao : OpinionDao {

    private const val COLLECTION  = "Opinion"
    private val DB = FirebaseFirestore.getInstance()

    override fun isEvaluated(user: String, dealRef: String, onResult: (Boolean, Evaluation) -> Unit) {
        DB.collection(COLLECTION)
            .whereEqualTo("dealRef", DB.document(dealRef))
            .whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                if(!documents.isEmpty) {
                    val document = documents.documents[0]
                    onResult(true, Evaluation(document.reference.path, dealRef, user, document.data?.get("isLike") as Boolean))
                }
                else
                    onResult(false, Evaluation(null, dealRef, user, null))
            }
    }

    override fun evalueDeal(opinion: Evaluation, isLike: Boolean, onResult: () -> Unit) {
        val dealRef = DB.document(opinion.dealRef)
        val data = hashMapOf(
            "user" to opinion.user,
            "dealRef" to dealRef,
            "isLike" to isLike
        )
        DB.collection(COLLECTION)
            .document()
            .set(data)
            .addOnSuccessListener {
                onResult()
            }
    }

    override fun changeEvaluation(opinion: Evaluation, isLike: Boolean, onResult: () -> Unit) {
        val dealRef = DB.document(opinion.reference!!)
        val updates = mapOf(
            "isLike" to isLike
        )
        dealRef
            .update(updates)
            .addOnSuccessListener {
                onResult()
            }
    }

    override fun countEvaluations(dealRef: String, onResult: (EvaluationsCounter) -> Unit) {
        var nbLikes = 0
        var nbUnlikes = 0
        DB.collection(COLLECTION)
            .whereEqualTo("dealRef", DB.document(dealRef))
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val isLike = document["isLike"] as Boolean
                    if (isLike)
                        nbLikes++
                    else
                        nbUnlikes++
                }
                onResult(EvaluationsCounter(nbLikes, nbUnlikes))
            }
    }

    override fun deleteEvaluations(dealRef: String, onResult: () -> Unit) {
        DB.collection(COLLECTION)
            .whereEqualTo("dealRef", DB.document(dealRef))
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.delete()
                }
                onResult()
            }
    }
}