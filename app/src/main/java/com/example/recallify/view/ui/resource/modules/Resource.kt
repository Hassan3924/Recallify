package com.example.recallify.view.ui.resource.modules

sealed class Resource<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    object Loading : Resource<Nothing>()
    class Success<T>(data: T?) : Resource<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resource<T>(throwable = throwable)
}
