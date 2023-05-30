package com.example.homedeal.dao

import com.example.homedeal.model.Alerte

interface AlerteDao {

    fun getAlertesByUser(user: String, onResult: (List<Alerte>) -> Unit)

    fun deleteAlerte(dealRef: String, onResult: () -> Unit)
}