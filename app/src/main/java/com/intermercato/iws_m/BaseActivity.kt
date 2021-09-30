package com.intermercato.iws_m

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intermercato.iws_m.Constants.REQUEST_ENABLE_BT

open class BaseActivity : AppCompatActivity() {

    private val FINE_LOCATION_PERMISSION = 101
    private val COARSE_LOCATION_PERMISSION  = 202
    private val TAG = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            doCheck()
    }


    /** * * * *  * * * permissions check start
     *
     *
     * * **/

    private fun doCheck(){
        checkForPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,"Location fine",FINE_LOCATION_PERMISSION)
        checkForPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION,"Location coarse",COARSE_LOCATION_PERMISSION)

    }

    private fun checkForPermissions(permission : String, name : String, requestCode :Int){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            when {

                ContextCompat.checkSelfPermission(applicationContext,permission) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(applicationContext,"$name permission granted", Toast.LENGTH_SHORT).show()
                }

                shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)

                else -> {
                    ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        fun innerCheck(name:String){
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"$name permission refused ",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"$name permission granted ",Toast.LENGTH_SHORT).show()
            }
        }

        when(requestCode){


            FINE_LOCATION_PERMISSION -> {
                Log.d(TAG,"fine location")
                innerCheck("location fine")
            }
            COARSE_LOCATION_PERMISSION -> {
                Log.d(TAG,"fine location")
                innerCheck("location coarse")
            }
        }

    }

    private fun showDialog(permission: String, name: String,requestCode: Int){

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("Ok") { dialog, which ->
                ActivityCompat.requestPermissions(this@BaseActivity, arrayOf(permission),requestCode)
            }
        }

        val dialog = builder.create()
        dialog.show()

    }

    /** * * * * * * * * * * * * * * permissions ends  */
}