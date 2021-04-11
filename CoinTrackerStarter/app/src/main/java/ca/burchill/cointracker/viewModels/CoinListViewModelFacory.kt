package ca.burchill.cointracker.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CoinListViewModelFacory (
    private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoinListViewModel::class.java)) {
                return CoinListViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}