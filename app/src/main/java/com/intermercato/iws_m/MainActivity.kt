package com.intermercato.iws_m


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.intermercato.iws_m.Constants.CHANNEL_NAME
import com.intermercato.iws_m.Constants.SERVICE_CHANNEL
import com.intermercato.iws_m.databinding.ActivityMainBinding
import com.intermercato.iws_m.realmModels.Bank
import com.intermercato.iws_m.realmModels.Order
import com.intermercato.iws_m.services.BleConnectionService
import com.intermercato.iws_m.ui.SettingsOperationFragment
import dagger.hilt.android.AndroidEntryPoint
import io.realm.Realm
import se.oioi.intelweighblelib.wifiservice.ConnectionService
import java.util.*


class MainActivity : BaseActivity() , SettingsOperationFragment.SnackBarEvent{

    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var mService : BleConnectionService? = null
    private val viewModel : MainViewModel by viewModels<MainViewModel>()
    private val TAG = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // add viewModel


        binding.apply {


        }


        viewModel.getBinder().observe(this, Observer { binder ->
            if(binder != null){
                Log.d(TAG,"bound to service")
                mService = binder.getService()
            }else{
                Log.d(TAG,"not bound")
            }
        })

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        //appBarConfig = AppBarConfiguration(navController.graph,drawer_layout)
        appBarConfig = AppBarConfiguration(
            setOf(R.id.fragment_orders, R.id.fragmentOrdersHistory),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfig)

        binding.navView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)

        startService()
        createNotificationChannel()
        navController.addOnDestinationChangedListener{navHostFragment , nd : NavDestination, _->
            Log.d(TAG, "nd $nd")
            when(nd.id){
                R.id.fragment_orders -> binding.bottomNav.visibility = View.VISIBLE
                R.id.fragmentOrdersHistory ->binding.bottomNav.visibility = View.VISIBLE
                else -> binding.bottomNav.visibility = View.INVISIBLE
            }
        }
        //setUpFakeOrder()

    }

    fun startService(){

        Log.d(TAG, "Starting ble connection service")
        val serviceIntent = Intent(this, BleConnectionService::class.java)
        serviceIntent.putExtra("ACT", MainActivity::class.java.getName())
        ContextCompat.startForegroundService(this, serviceIntent)

        bindService(Intent(this, BleConnectionService::class.java), viewModel.getServiceConnection(), BIND_AUTO_CREATE)

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


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    override fun valueWasUpdated(key: String?, v: String?) {

    }

    fun setUpFakeOrder(){
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransactionAsync {

                var order = it.createObject(Order::class.java,UUID.randomUUID().toString())

                order.orderNumber = "ORD00001"
                order.timeStart = System.currentTimeMillis()
                order.selectedBankIndex = 1

                val str = "bank"
                for (i in 0..str.length-1) {
                    println(str[i])
                    var bank = it.createObject(Bank::class.java,UUID.randomUUID().toString())
                    if(i==1){
                        bank.active = true
                    }

                    bank.bankOrderIndex = i


                    order.banks.add(bank)
                }
            }
        }
    }

    fun viewOrder(){
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransactionAsync {

                val size = it.where(Order::class.java).findAll().size
                Log.d(TAG,"vi har $size orders")
                val ordersResult = it.where(Order::class.java).findAll()

                ordersResult.forEach { order ->
                    Log.d(TAG,"order id ${order.id}  orderNumber${order.orderNumber} banker ${order.banks.size}")

                }

                val banks = it.where(Bank::class.java).equalTo("orderId","6430e599-e728-46e5-9472-f499c96e3deb").findAll()
                Log.d(TAG," banks size"+banks.size)
                val names  = arrayOf("flis","timmer","bark","blötflis")
                    banks.forEachIndexed{index ,b ->
                    //Log.d(TAG," bank id "+b.id+"  active " +b.active+"  "+b.bankOrderIndex)
                    Log.d(TAG," bank orderId -- >"+b.orderId)
                    b.alias = names[index]
                    it.copyToRealmOrUpdate(b)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewOrder()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}


