package com.example.homedeal.dao

import android.util.Log
import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.model.Deal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object FirebaseDealDao : DealDao {

    private const val COLLECTION = "Deal"
    private const val TAG = "FirebaseDealDao"
    private val DB = FirebaseFirestore.getInstance()
    private val AUTH: Auth = AuthWithFirebase
    private val CREATIONDAO: CreationDao = FirebaseCreationDao

    override fun getDeals(onResult: (ArrayList<Deal>) -> Unit) {
        val datas = ArrayList<Deal>()
        DB.collection(COLLECTION)
            .orderBy("likes", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    datas.add(
                        Deal(
                            document.data["name"] as String,
                            document.data["expiration"] as String,
                            document.data["price"] as Double,
                            document.data["link"] as String,
                            document.data["description"] as String,
                            document.data["image"] as String,
                            document.data["creator"] as String,
                            document.reference.path
                        )
                    )
                }
                onResult(datas)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    override fun getDealByRef(ref : String, onResult: (Deal) -> Unit) {
        val listData = ArrayList<Deal>()
        DB.document(ref)
            .get()
            .addOnSuccessListener { document ->
                onResult(
                    Deal(
                        document.data?.get("name") as String,
                        document.data!!["expiration"] as String,
                        document.data!!["price"] as Double,
                        document.data!!["link"] as String,
                        document.data?.get("description") as String,
                        document.data!!["image"] as String,
                        document.data!!["creator"] as String,
                        document.reference.path
                )
                )
            }
    }

    override fun addDeal(deal: Deal, onResult: (Deal) -> Unit) {
        DB.collection(COLLECTION)
            .add(deal)
            .addOnSuccessListener { documentReference ->
                CREATIONDAO.addCreation(documentReference.path, AUTH.getCurrentUser()!!.email!!) {
                    deal.reference = documentReference.path
                    onResult(deal)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    override fun deleteDeal(ref: String, onResult: () -> Unit) {
        DB.document(ref)
            .delete()
            .addOnSuccessListener {
                onResult()
            }
    }


}