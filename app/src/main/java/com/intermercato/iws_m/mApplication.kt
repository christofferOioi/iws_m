package com.intermercato.iws_m

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.intermercato.iws_m.Constants.CHANNEL_NAME
import com.intermercato.iws_m.Constants.SERVICE_CHANNEL
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration


class mApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: mApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }


    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        /// PrimaryKeyFactory.hasInited(Realm.getDefaultInstance());
        val myConfig: RealmConfiguration = RealmConfiguration.Builder()
            .name("iwsble.realm")
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .schemaVersion(1)
            .build()
        Realm.setDefaultConfiguration(myConfig)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}