package com.intermercato.iws_m.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.intermercato.iws_m.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import se.oioi.intelweighblelib.events.scale.CommandEvent
import se.oioi.intelweighblelib.events.scale.StartComboxEvent
import se.oioi.intelweighblelib.helpers.ClassLauncher

class BleConnectionService : Service() {
    private val SERVICE_CHANNEL = "ble.notification.channel"
    private val TAG: String = "service"
    private var binder: IBinder? = mBinder()
    private var presenter: BleConnectionContract.Presenter? = null

    fun BleConnectionService() {
        Log.d(TAG, "constructor")
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate BleConnectionService")
        EventBus.getDefault().register(this)
        presenter = BleConnectionPresenter(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val actname = intent!!.getStringExtra("ACT")
        Log.d(TAG, "onStartCommand from  $actname")

        val c = ClassLauncher(this)
        var ca: Class<*>? = null
        try {
            ca = c.getActivityClass(actname)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val notIntent = Intent(this, ca)
        val pIntent = PendingIntent.getActivity(this, 0, notIntent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, " notification -> startForeground")
            val not = NotificationCompat.Builder(this, SERVICE_CHANNEL).setContentTitle("IWS")
                    .setContentText("ble service is running")
                    .setSmallIcon(R.drawable.btn_icon_about_intermercato)
                    .setContentIntent(pIntent).build()
            startForeground(19, not)
        }

        presenter?.doBind()

        return START_NOT_STICKY
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(data: CommandEvent) {
        Log.d(TAG, "onEvent " + data.command + " HAS BACKLASH: " + data.command.contains("\r\n"))
        if (presenter != null) {
            presenter?.doCommand(data.command)

        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: StartComboxEvent) {
        Log.d(TAG, "onEvent StartComboxEvent " + event.isStartCombox)


        presenter?.connect()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        presenter?.disconnect()
        presenter?.unBind()
        stopSelf()
        Log.d(TAG, "onTaskRemoved")
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnBind")
        return super.onUnbind(intent)
    }

    inner class mBinder : Binder() {
        fun getService(): BleConnectionService? {
            return this@BleConnectionService
        }
    }

    override fun onDestroy() {

        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }


}
