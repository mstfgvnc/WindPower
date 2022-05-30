package com.mustafaguvenc.windpower.ui.enterfragment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.mustafaguvenc.windpower.model.*
import com.mustafaguvenc.windpower.repository.Repository
import com.mustafaguvenc.windpower.ui.BaseViewModel
import com.mustafaguvenc.windpower.util.CustomSharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterFragmentViewModel
@Inject constructor(private val repository: Repository, application : Application):BaseViewModel(application){
    val turbines = MutableLiveData<List<TurbineModel>>()
    val windSpeeds = MutableLiveData<List<Data>>()
    private val disposable = CompositeDisposable()
    val customPreferences = CustomSharedPreferences(getApplication())
    val elevation = MutableLiveData<ElevationModel>()

    fun getTurbines(){
        getTurbineFromSQLite()
    }

    private fun getTurbineFromSQLite(){
        launch {
            val turbinesFromApi = repository.getTurbineFromDatabase()
            if(turbinesFromApi.size==0){
                getTurbineFromAPI()
            }else{
                showView(turbinesFromApi)
            }


        }
    }

    private fun getTurbineFromAPI(){
        disposable.add(
            repository.getTurbineFromApi()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<TurbineModel>>() {
                    override fun onSuccess(t : List<TurbineModel>) {
                        storeInSQLiteForTurbines(t)
                    }
                    override fun onError(e : Throwable) {

                        e.printStackTrace()

                    }

                })
        )
    }


    private fun storeInSQLiteForTurbines (list : List<TurbineModel>){
        launch {

            repository.deleteAllTurbinesFromDatabase()
            val listLong =  repository.insertAll(*list.toTypedArray())
            var i = 0
            while(i<list.size){
                list[i].uuid = listLong[i].toInt()
                i = i + 1
            }
            showView(list)
        }

    }

    private fun showView(turbinList : List<TurbineModel>){
        turbines.value= turbinList
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


}