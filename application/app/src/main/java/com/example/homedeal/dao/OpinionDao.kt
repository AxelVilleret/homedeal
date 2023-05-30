package com.example.homedeal.dao

import com.example.homedeal.model.Evaluation
import com.example.homedeal.model.EvaluationsCounter

interface OpinionDao {
    fun isEvaluated(user: String, dealRef: String, onResult: (Boolean, Evaluation) -> Unit)
    fun evalueDeal(opinion: Evaluation, isLike: Boolean, onResult: () -> Unit)
    fun changeEvaluation(opinion: Evaluation, isLike: Boolean, onResult: () -> Unit)
    fun countEvaluations(dealRef: String, onResult: (EvaluationsCounter) -> Unit)
    fun deleteEvaluations(dealRef: String, onResult: () -> Unit)
}