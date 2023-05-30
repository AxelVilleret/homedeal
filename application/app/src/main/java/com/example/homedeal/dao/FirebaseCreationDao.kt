package com.example.homedeal.dao

import com.example.homedeal.auth.Auth
import com.example.homedeal.auth.AuthWithFirebase
import com.example.homedeal.model.Creation
import com.example.homedeal.model.Save
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseCreationDao : CreationDao {

    private val TAG = "FirebaseCreationDao"
    private const val COLLECTION = "Creation"
    private val DB = FirebaseFirestore.getInstance()
    private val AUTH: Auth = AuthWithFirebase

    override fun addCreation(dealRef: String, user: String, onResult: () -> Unit) {
        DB.collection(COLLECTION)
            .add(mapOf(
                "dealRef" to DB.document(dealRef),
                "user" to AUTH.getCurrentUser()!!.email
            ))
            .addOnSuccessListener {
                onResult()
            }
    }

    override fun getCreations(user: String?, onResult: (List<Creation>) -> Unit) {
        if (user != null) {
            val creations = ArrayList<Creation>()
            DB.collection(COLLECTION)
                .whereEqualTo("user", user)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val dealRef = document.data["dealRef"] as DocumentReference
                        creations.add(
                            Creation(
                                document.reference.path,
                                dealRef.path,
                                user
                            )
                        )

                    }
                    onResult(creations)
                }
        }
    }

    override fun deleteCreation(creation: Creation, onResult: () -> Unit) {
        creation.reference?.let {
            DB.document(it)
                .delete()
                .addOnSuccessListener {
                    onResult()
                }
        }
    }

    override fun getCreation(dealRef: String, onResult: (Creation) -> Unit) {
        DB.collection(COLLECTION)
            .whereEqualTo("dealRef", DB.document(dealRef))
            .whereEqualTo("user", AUTH.getCurrentUser()!!.email)
            .get()
            .addOnSuccessListener { documents ->
                if(!documents.isEmpty) {
                    val document = documents.documents[0]
                    onResult(Creation(document.reference.path, dealRef, AUTH.getCurrentUser()!!.email!!))
                }
            }
    }

}