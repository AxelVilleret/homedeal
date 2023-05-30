package com.example.homedeal.dao

import com.example.homedeal.model.Save

interface SaveDao {
    fun isSaved(user: String, dealRef: String, onResult: (Boolean, Save) -> Unit)
    fun saveDeal(save: Save, onResult: () -> Unit)
    fun unsaveDeal(save: Save, onResult: () -> Unit)
    fun getSaves(user: String?, onResult: (List<Save>) -> Unit)
    fun getSavedDealByIdandUser(user: String?, dealRef: String, onResult: (Save) -> Unit)

    fun deleteSaves(dealRef: String, onResult: () -> Unit)
}