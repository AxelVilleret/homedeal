package com.example.homedeal.dao

import com.example.homedeal.model.Deal

interface DealDao {
    fun getDeals(onResult: (ArrayList<Deal>) -> Unit)
    fun getDealByRef(ref : String, onResult: (Deal) -> Unit)
    fun addDeal(deal: Deal, onResult: (Deal) -> Unit)
    fun deleteDeal(ref: String, onResult: () -> Unit)
}


