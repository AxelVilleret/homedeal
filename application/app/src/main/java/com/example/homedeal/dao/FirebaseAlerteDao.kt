package com.example.homedeal.dao

import com.example.homedeal.model.Alerte
import com.example.homedeal.utils.ViewUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseAlerteDao: AlerteDao {

    private const val TAG = "FirebaseAlerteDao"
    private const val COLLECTION = "Alerte"
    private val DB = FirebaseFirestore.getInstance()

    override fun getAlertesByUser(user: String, onResult: (List<Alerte>) -> Unit) {
        DB.collection(COLLECTION)
            .whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val alertes = ArrayList<Alerte>()
                for (document in querySnapshot) {
                    val dealRef = document.data["dealRef"] as DocumentReference
                    val title = document.data["title"] as String
                    val timestamp = document.data["timestamp"] as Timestamp
                    val body = document.data["body"] as String
                    alertes.add(
                        Alerte(
                            document.reference.path,
                            dealRef.path,
                            user,
                            ViewUtils.convertLongToTime(timestamp.toDate()),
                            title,
                            body
                        )
                    )
                }
                onResult(alertes)
            }
    }


    override fun deleteAlerte(dealRef: String, onResult: () -> Unit) {
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