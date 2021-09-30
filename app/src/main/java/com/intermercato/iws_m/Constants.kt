package com.intermercato.iws_m

object Constants {

    // BLE SETUP
    const val BLE_BOX_ID = "ble_box_id"


    const val REQUEST_ENABLE_BT = 5

    // weight sent status
    const val DID_GET_SENT = 0
    const val NOT_SENT = 0

    // Notification channel names
    const val SERVICE_CHANNEL = "ble.notification.channel";
    const val CHANNEL_NAME = "iws_m";

    // prefs repositories name
    const val CONTROLBOX_SETUP = "controlbox_setup";
    const val OIOI_PREFS = "com.intermercato"
    const val DISPLAYED_BANKS = "displayed_banks";
    const val SCALE_ID = "current_scale_id"
    const val DRIVER_ID = "current_driver_db_id"

    // communicate through fragments
    const val FRAGMENT_REQUEST_KEY = "fragment_request_key"
    const val KEYNUMBER = "to_active"



    // VIEW HOLDER
    const val TYPE_WEIGHT_ROW = 0
    const val TYPE_CONTAINER_ROW = 1
    const val TYPE_WEIGHT_ROW_EXTENDED = 2
}