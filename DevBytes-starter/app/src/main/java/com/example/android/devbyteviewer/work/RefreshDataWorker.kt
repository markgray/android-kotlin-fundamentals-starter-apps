package com.example.android.devbyteviewer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.getDatabase
import com.example.android.devbyteviewer.repository.VideosRepository
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException

/**
 * A `ListenableWorker` implementation that provides interop with Kotlin Coroutines. We override
 * the [doWork] function to do our suspending work. This is used in the `setupRecurringWork` method
 * of `DevByteApplication` where it schedules it to run on a daily basis using `WorkManager`.
 *
 * @param appContext this [Context] parameter will be supplied by the `WorkManager` when we are run.
 * @param params this instance of [WorkerParameters] comes from the `WorkManager` when we are run.
 */
class RefreshDataWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    /**
     * A suspending method to do our work. This function runs on the coroutine context specified
     * by the [coroutineContext] we are run on, which is by default [Dispatchers.Default].
     * A CoroutineWorker is given a maximum of ten minutes to finish its execution and return a
     * [ListenableWorker.Result]. After this time has expired, the worker will be signalled to
     * stop.
     *
     * We initialize our [VideosDatabase] variable `val database` with a handle to the singleton
     * instance of our database. We then initialize our [VideosRepository] variable `val repository`
     * with an instance constructed to use `database` as its [VideosDatabase]. Then in a `try` block
     * intended to catch [HttpException] and return a [ListenableWorker.Result.retry] as our
     * [ListenableWorker.Result] if such an exception occurs, we call the suspend function
     * `refreshVideos` of `repository` in order to have it refresh the videos stored in the offline
     * cache from the network. If there is no exception we return [ListenableWorker.Result.success]
     * as our [ListenableWorker.Result].
     *
     * @return The [ListenableWorker.Result] of the result of the background work, one of
     * [ListenableWorker.Result.success], [ListenableWorker.Result.retry], or
     * [ListenableWorker.Result.failure] Note that dependent work will not execute if you return
     * [ListenableWorker.Result.failure]
     */
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = VideosRepository(database)
        try {
            repository.refreshVideos()
        } catch (_: HttpException) {
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        /**
         * The unique name for this operation, used in the call to `enqueueUniquePeriodicWork`
         * in the `setupRecurringWork` method of `DevByteApplication
         */
        const val WORK_NAME: String = "com.example.android.devbyteviewer.work.RefreshDataWorker"
    }
}