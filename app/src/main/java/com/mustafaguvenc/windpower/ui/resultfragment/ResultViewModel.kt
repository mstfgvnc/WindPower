package com.mustafaguvenc.windpower.ui.resultfragment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.mustafaguvenc.windpower.model.*
import com.mustafaguvenc.windpower.repository.Repository
import com.mustafaguvenc.windpower.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel
@Inject constructor(private val repository: Repository, application : Application):BaseViewModel(application){
    private val disposable = CompositeDisposable()
    val elevation = MutableLiveData<ElevationModel>()
    val unixTime = MutableLiveData<Int>()
    val windModel = MutableLiveData<WindModel>()
    val turbineModel = MutableLiveData<List<TurbineModel>>()
    val loading = MutableLiveData<Boolean>()
    var windDataList = arrayListOf<Double>()


    fun getWindSpeedsFromAPI(lat:String,lon:String,start:String,end:String,key:String){
        disposable.add(
            repository.getWindSpeedsFromApi(lat, lon, start, end, key)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WindModel>() {
                    override fun onSuccess(t : WindModel) {

                        for(i in t.list){
                            windDataList.add(i.wind.speed)
                        }
                        windModel.value=t

                    }
                    override fun onError(e : Throwable) {
                        println("Hata Hızlarda")

                        e.printStackTrace()

                    }

                })
        )
    }


    fun getElevationFromAPI(lat :String,lon: String){
        loading.value=true
        disposable.add(
            repository.getElevationFromApi(lat+","+lon)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ElevationModel>() {
                    override fun onSuccess(t : ElevationModel) {
                        elevation.value=t
                    }

                    override fun onError(e : Throwable) {
                        e.printStackTrace()
                        println("Hata Elevation")
                    }


                })
        )



    }

    fun getTimeFromAPI(){

        disposable.add(
            repository.getTimeFromApi()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<TimeModel>() {
                    override fun onSuccess(t : TimeModel) {
                        unixTime.value=t.unixtime
                    }

                    override fun onError(e : Throwable) {
                        println("Hata Zamanda")
                        unixTime.value = (System.currentTimeMillis()/1000).toInt()
                        e.printStackTrace()
                    }


                })
        )

    }


    fun getTurbineFromSQLite(){
        launch {
             turbineModel.value = repository.getTurbineFromDatabase()
        }
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}