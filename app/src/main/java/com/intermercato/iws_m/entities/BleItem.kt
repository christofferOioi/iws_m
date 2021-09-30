package com.intermercato.iws_m.entities

data class BleItem(val _name: String?,val _adress: String?, val active: Boolean?) {

    var isActive: Boolean? = active
    var name: String? = _name
    var adress: String? = _adress
}

