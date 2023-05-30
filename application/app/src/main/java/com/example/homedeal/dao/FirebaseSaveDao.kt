package com.example.homedeal.dao

import com.example.homedeal.model.Save
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseSaveDao : SaveDao {

    private const val COLLECTION  = "Save"
    private val DB = FirebaseFirestore.getInstance()

    override fun isSaved(user : String, dealRef : String, onResult : (Boolean, Save) -> (Unit)) {
        DB.collection(COLLECTION)
            .whereEqualTo("dealRef", DB.document(dealRef))
            .whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                if(!documents.isEmpty) {
                    val document = documents.documents[0]
                    onResult(true, Save(document.reference.path, dealRef, user))
                }
                else
                    onResult(false, Save(null, dealRef, user))
            }
    }
    override fun saveDeal(save: Save, onResult: () -> Unit) {
        val dealRef = DB.document(save.dealRef)
        val data = hashMapOf(
            "user" to save.user,
            "dealRef" to dealRef
        )
        DB.collection(COLLECTION)
            .document()
            .set(data)
            .addOnSuccessListener {
                onResult()
            }
    }
    override fun unsaveDeal(save: Save, onResult: () -> Unit) {
        save.reference?.let {
            DB.document(it)
                .delete()
                .addOnSuccessListener {
                    onResult()
                }
        }
    }

    override fun getSaves(user: String?, onResult: (List<Save>) -> Unit) {
        if (user != null) {
            val saves = ArrayList<Save>()
            DB.collection(COLLECTION)
                .whereEqualTo("user", user)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val dealRef = document.data["dealRef"] as DocumentReference
                        saves.add(
                            Save(
                                document.reference.path,
                                dealRef.path,
                                user
                            )
                        )

                    }
                    onResult(saves)
                }
        }

    }

    override fun getSavedDealByIdandUser(user: String?, dealRef: String, onResult: (Save) -> Unit) {
        val saves = ArrayList<Save>()
        DB.collection(COLLECTION)
            .whereEqualTo("user", user)
            .whereEqualTo("dealRef", DB.document(dealRef))
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val dealRef = document.data["dealRef"] as DocumentReference
                    saves.add(
                        Save(
                        document.reference.path,
                            dealRef.path,
                        document["user"] as String
                    )
                    )
                }
                onResult(saves[0])
            }
    }

    override fun deleteSaves(dealRef: String, onResult: () -> Unit) {
        DB.collection(COLLECTION)
            .whereEqualTo("dealRef", DB.document(dealRef))
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
                onResult()
            }
    }
}