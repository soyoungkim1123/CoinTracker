package ca.burchill.cointracker.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ca.burchill.cointracker.database.getDatabase
import ca.burchill.cointracker.repository.CoinsRepository
import retrofit2.HttpException

class RefreshDataWorker (appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    // TODO - implement delayedInit() that ensures workmanager is refreshing the database regularly
    companion object {
        const val WORK_NAME = "ca.burchill.cointracker.work.RefreshDataWorker"
    }
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = CoinsRepository(database)
        try {
            repository.refreshCoins()
        } catch (e: HttpException) {
            return Result.retry()
        }
        return Result.success()
    }
}