package com.example.homedeal.dao

import com.example.homedeal.model.Creation

interface CreationDao {

    fun addCreation(dealRef: String, user: String, onResult: () -> Unit)

    fun getCreations(user: String?, onResult: (List<Creation>) -> Unit)

    fun deleteCreation(creation: Creation, onResult: () -> Unit)

    fun getCreation(dealRef: String, onResult: (Creation) -> Unit)

}