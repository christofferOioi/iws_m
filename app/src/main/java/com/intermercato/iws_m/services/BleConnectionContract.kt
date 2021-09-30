package com.intermercato.iws_m.services

class BleConnectionContract {


    interface Presenter {

        fun connect()
        fun disconnect()
        fun doCommand(command :String?)
        fun doBind()
        fun unBind()
    }



}