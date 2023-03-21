package com.example.recallify.view.ui.resource.modules

import com.example.recallify.data.application.ActivityState

/**
 * Activity DataState is a parent signal or commute for data flow from a network source or local.
 * The Activity DataState like its name tracks the data sent or retrieved from the database to the
 * Activity. In this context we return a **SUCCESS**, **FAILED**, **LOADING** and **EMPTY**.
 *
 *  **parameters**
 *  1. class Success()
 *  2. class Failed()
 *  3. object Loading
 *  4. object Empty
 *
 * @return The different context of a signal or commute of data.
 *
 * @see ActivityState The state being tracked and referred to in the application.
 *
 * @author enoabasi
 * */
sealed class ActivityDataState {
    /**
     * The Success class provides a successful signal or commute of data from its given given source.
     * The given source can be **"Any"**. In the context we have defined our source as a List of
     * **Activity States**.
     *
     * @param data The data that has been created or retrieved successfully.
     *
     * @return The data in the ActivityState from the source.
     *
     * @author enoabasi
     * */
    class Success(val data: MutableList<ActivityState>) : ActivityDataState()

    /**
     * The failed class provides a failed signal or commute of data from its given source. The given
     * source can be **"Any"** . In the context we have defined our source as the error message of a
     * failed signal or commute in the Activity State.
     *
     * @param message The error message to be provided for debugging
     *
     * @return The message in the ActivityState from the source.
     *
     * @author enoabasi
     * */
    class Failed(val message: String) : ActivityDataState()

    /**
     * The loading object provides a signal or commute data that is yet to be sent or retrieved and
     * the ActivityDataState should listen for any changes in the application.
     *
     * @return A loading state of the state context.
     *
     * @author enoabasi
     * */
    object Loading: ActivityDataState()

    /**
     * The empty object provides a signal or commute data that has been retrieved and can be deemed
     * empty by lexical logic.
     *
     * @return An Empty ActivitySate
     *
     * @author enoabasi
     * */
    object Empty: ActivityDataState()
}
