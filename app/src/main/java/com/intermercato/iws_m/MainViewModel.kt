package com.intermercato.iws_m

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intermercato.iws_m.services.BleConnectionService

class MainViewModel : ViewModel() {


    private var mBinder: MutableLiveData<BleConnectionService.mBinder> = MutableLiveData()

    init {
        Log.d("Main", "ViewModel has been init")

    }



    fun getBinder(): LiveData<BleConnectionService.mBinder> {
        return mBinder
    }


    /***  Connection service **/

    fun getServiceConnection(): ServiceConnection {
        return serviceConnection
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BleConnectionService.mBinder
            mBinder.postValue(binder)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBinder.postValue(null)
        }
    }

}