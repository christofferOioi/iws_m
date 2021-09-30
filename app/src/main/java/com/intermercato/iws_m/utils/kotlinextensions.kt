package com.intermercato.iws_m.utils

import com.intermercato.iws_m.realmModels.Bank

fun ArrayList<Bank>.max() : Bank = this.sortedBy { it.totalWeight  }.last()
fun Boolean.toInt() = if (this) 1 else 0
