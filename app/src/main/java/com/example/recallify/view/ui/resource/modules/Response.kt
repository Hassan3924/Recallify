package com.example.recallify.view.ui.resource.modules

/**
 * Response is a generic output or input for data flow from a network source or local.
 * The Response like its name tracks the data sent or retrieved from the database.
 * In this context we return a **SUCCESS**, **FAILED**, and **LOADING**
 *
 *  **parameters**
 *  1. data class Success()
 *  2. data class Failed()
 *  3. object Loading
 *
 * @return The different context of a signal or commute of data.
 *
 * @author enoabasi
 * */
sealed class Response<out T> {

    /**
     * The loading object provides a signal or commute data that is yet to be sent or retrieved and
     * the Response should listen for any changes in the application.
     *
     * @return A loading state of the state context.
     *
     * @author enoabasi
     * */
    object Loading : Response<Nothing>()

    /**
     * The Success class provides a successful signal or commute of data from its given given source.
     * The given source can be **"Any"** as it servers as a generic.
     *
     * @param data The data that has been created or retrieved successfully.
     *
     * @return The data given from the source.
     *
     * @author enoabasi
     * */
    data class Success<out T>(
        val data: T?
    ) : Response<T>()

    /**
     * The failed class provides a failed signal or commute of data from its given source. The given
     * source can be **"Any"** .
     *
     * @param message The error message to be provided for debugging
     *
     * @return The message given from the source.
     *
     * @author enoabasi
     * */
    data class Failure(
        val message: Exception
    ) : Response<Nothing>()
}
