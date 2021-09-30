package com.intermercato.iws_m.realmModels
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults


fun <T : RealmModel> RealmResults<T>.asLiveData() = RealmLiveData<T>(this)
fun Realm.kartDao(): KartDao = KartDao(this)
