package ca.burchill.cointracker.viewModels

import android.app.Application
import androidx.lifecycle.*
import ca.burchill.cointracker.database.getDatabase
import ca.burchill.cointracker.domain.Coin
import ca.burchill.cointracker.network.*
import ca.burchill.cointracker.repository.CoinsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import timber.log.Timber
import java.io.IOException


enum class CoinApiStatus { LOADING, ERROR, DONE }


class CoinListViewModel(application: Application) : AndroidViewModel(application) {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<CoinApiStatus>()
    val status: LiveData<CoinApiStatus>
        get() = _status


//    private val _coins = MutableLiveData<List<NetworkCoin>>()
//    val coins: LiveData<List<NetworkCoin>>
//        get() = _coins



    // or use viewModelScope
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val coinsRepository = CoinsRepository(getDatabase(application))
    val coins = Transformations.map(coinsRepository.coins) {
        list -> list.map {
            val tags: List<String> = listOf()
            val networkQ = NetworkQuote(NetworkUSD(it.date_added, it.market_cap, it.percent_change_1h, it.percent_change_24h, it.percent_change_30d, it.percent_change_60d, it.percent_change_7d, it.percent_change_90d, it.price, it.volume_24h))
            NetworkCoin(it.circulating_supply, it.cmcRank, it.date_added, it.id, it.date_added, it.max_supply, it.name, it.num_market_pairs, "", networkQ, it.slug, it.symbol, tags, it.total_supply)
        }
    }

    init {
        //getCoins()
        refreshDataFromRepository()
    }

    private fun refreshDataFromRepository() = viewModelScope.launch {
        try {
            coinsRepository.refreshCoins()
            _status.value = CoinApiStatus.DONE
        }catch (networkError: IOException){
            if(coins.value.isNullOrEmpty())
                _status.value = CoinApiStatus.ERROR
        }
    }
//    private fun getCoins() {
//
//       coroutineScope.launch {
//            try {
//                var coinResult = CoinApi.retrofitService.getCoins()
//                if (coinResult.coins.size > 0) {
//                    _coins.value = coinResult.coins
//                }
//            } catch (t: Throwable) {
//               _status.value = CoinApiStatus.ERROR
//            }
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing CoinListViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoinListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CoinListViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}