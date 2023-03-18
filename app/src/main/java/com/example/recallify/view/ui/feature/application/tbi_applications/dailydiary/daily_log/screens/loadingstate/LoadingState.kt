package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.loadingstate

data class LoadingState(val status : Status, val message: String? = null) {

    companion object {

        val IDLE = LoadingState(Status.IDLE)
        val SUCCESS = LoadingState(Status.SUCCESS)
        val LOADING = LoadingState(Status.LOADING)
        val FAILED = LoadingState(Status.FAILED)

    }

    enum class Status {

        SUCCESS,
        FAILED,
        LOADING,
        IDLE

    }

}