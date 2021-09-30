package com.intermercato.iws_m.realmModels

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.util.*


/**
 * Created by fredrik on 2017-12-04.
 */
open class Truck(
    @PrimaryKey var id: String,
    var apiTruckReturnId: String? = null,
    var truckName: String? = null,
    var regNumber: String? = null,
    var isSelected: Boolean = false
) : RealmObject() {

    constructor () : this(id = UUID.randomUUID().toString())
}

open class CloudApiKey(
    @PrimaryKey var id: String,
    var apiKey: String? = null,
    var apiOrderReturnId: String? = null,
    var publicId: String? = null
) : RealmObject() {
    constructor() : this(id = UUID.randomUUID().toString())
}


open class Row(
    @PrimaryKey var id: String,
    var autoid: Int? = null,
    var autostderror: Int? = null,
    var devicemode: Int? = null,
    var taravalue: Int? = null,
    var controlsum: Int? = null,
    var boxtimestamp: Long? = null,
    var scalemode: Int? = null,
    var taraacc: Int? = null,
    var orderNumber: String? = null,
    var weight: Int = 0,
    var bruttoWeight: Int = 0,
    var containerWeight: Int = 0,
    var timeStart: Long? = null,
    var gpsLat: Double = 0.toDouble(),
    var gpsLong: Double = 0.toDouble(),
    var gpsElevation: Double = 0.toDouble(),
    var gpsAccuracy: Double = 0.toDouble(),
    var bankAlias: String? = null,
    var bank_id: String? = null,
    var bankOrderIndex: Int? = 0,
    var collectionsite_id: String? = null,
    var order_id: String? = null,
    var driver_id: String? = null,
    var driverFirstName: String? = null,
    var driverLastName: String? = null,
    var isSpotCheck: Boolean = false,
    var scaleId: String? = null,
    var type: Int = 0,
    var didGetSent: Int? = 0,
    var IndexOfRow: Int? = 0,
    var apiRowReturnId: String? = null,
    var apiOrderReturnId: String? = null,
    var containerCategoryName: String? = null,
    var massUnit: String? = null
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}


open class CollectionSite(
    @PrimaryKey var id: String,
    var apiSiteReturnId: String? = null,
    var placeNumber: String? = null,
    var name: String? = null,
    var streetAddress: String? = null,
    var city: String? = null,
    var postalNumber: String? = null,
    var customerId: String? = null,
    var isSelected: Boolean = false
) : RealmObject() {
    constructor ()
            : this(id = UUID.randomUUID().toString())
}

data class OtdOrder(
    var id : String?  = null,
    var orderName : String? = null,
    var arrivalDate : String? = null,
    var shipName : String? = null,
    var message : String? = null,
    var usesSpotCheck : Boolean? = true,
    var materials : List<Material>? = null,
    var images : List<ShipImages>? = null

)  {

    data class ShipImages(
        var id : String? = null,
        var url : String? = null,
        var description : String? = null
    )

    data class Material(
        var id : String? = null,
        var name : String? = null,
        var total : String? = null
    )
}

data class OtdWeight(
    var id: String?,
    var weight: Int?,
    var time : String?,
    var materialName : String?,
    var orderId : String,
    var collective : String?,
    var index : Int?,
    var shipName : String?,
    var spotCheck  : Boolean,
    var uploaded : Boolean

)

data class OtdEvent(
    var event : Int,
    var time : String
)
open class OrderImage(
    @PrimaryKey var id: String,
    var url : String? = null,
    var description : String? = null

) : RealmObject() {
    constructor() : this(id = UUID.randomUUID().toString())
}


open class Order(
    @PrimaryKey var id: String,
    var apiOrderReturnId: String? = null,
    var orderNumber: String? = null,
    var orderShipName: String? = null,
    var arrivalDate : String? = null,
    var shipName : String? = null,
    var message : String? = null,
    var description: Description? = null,
    var truck: Truck? = null,
    var customer: Customer? = null,
    var contractor: Contractor? = null,
    var collectionSite: CollectionSite? = null,
    var timeStart: Long = System.currentTimeMillis(),
    var timeEnd: Long = 0,
    var totalWeight: Int = 0,
    var tripTotalWeight: Int? = 0,
    var isContainer: Boolean = false,
    var isActive: Boolean = true,
    var selectedBankIndex: Int? = 0,
    var firstWeightDateTaken: Long? = 0L,
    var workmode: Int? = 0,
    var index: Int? = 0,
    var scaleId: String? = null,
    var apiOrderStatus: String? = null,
    var didGetSent: Int? = 0,
    var usesSpotCheck : Boolean? = true,
    var bankDisplayList: RealmList<BankDisplayOrder> = RealmList(),
    var spotCheckList: RealmList<Bank> = RealmList(),
    var banks: RealmList<Bank> = RealmList(),
    var orderImages: RealmList<OrderImage> = RealmList()
) : RealmObject() {
    constructor() : this(id = UUID.randomUUID().toString())
}


open class BankDisplayOrder(
    @PrimaryKey var id: String,
    var bundleList: RealmList<String> = RealmList()
) : RealmObject() {
    constructor() : this(id = UUID.randomUUID().toString())
}

open class Customer(
    @PrimaryKey var id: String,
    var apiCustomerReturnId: String? = null,
    var customerNumber: String? = null,
    var name: String? = null,
    var customerEmail: String? = null,
    var customerPhone: String? = null,
    var customerAddress: String? = null,
    var customerPostalNumber: String? = null,
    var customerCity: String? = null,
    var isSelected: Boolean = false,
    var collectionSites: RealmList<CollectionSite> = RealmList()
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}

open class Contractor(
    @PrimaryKey var id: String,
    var apiContractorReturnId: String? = null,
    var contractorNumber: String? = null,
    var name: String? = null,
    var contractorEmail: String? = null,
    var contractorPhone: String? = null,
    var contractorAddress: String? = null,
    var contractorPostalNumber: String? = null,
    var contractorCity: String? = null,
    var isSelected: Boolean = false
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}

open class Bank(
    @PrimaryKey var id: String,
    var apiBankReturnId: String? = null,
    var alias: String? = null,
    var bankOrderIndex: Int? = 0,
    var active: Boolean = false,
    var totalWeight: Int = 0,
    var orderId: String? = null,
    var map: String? = null,
    var rows: RealmList<Row> = RealmList()
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}


open class Description(
    @PrimaryKey var id: String,
    var apiDescriptionReturnId: String? = null,
    @Index var name: String? = "",
    var isSelected: Boolean = false
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}

open class ContainerCategory(
    @PrimaryKey var id: String,
    @Index var name: String? = "",
    var isSelected: Boolean = false
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}

open class Driver(
    @PrimaryKey var id: String,
    var apiDriverReturnId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var isSelected: Boolean = false
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}

open class BaseOrderBanks(var defaultBanks: RealmList<BaseBank> = RealmList()) : RealmObject()
open class BaseBank(
    @PrimaryKey
    var id: String,
    var bankOrderIndex: Int? = 0,
    var alias: String? = null
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}

open class CurrentDriver(var driver: Driver? = null) : RealmObject()
open class CurrentTruck(var truck: Truck? = null) : RealmObject()
open class CurrentCustomer(var customer: Customer? = null) : RealmObject()
open class CurrentContractor(var contractor: Contractor? = null) : RealmObject()
open class CurrentOrder(var order: Order? = null) : RealmObject()
open class CurrentScaleId(
    @PrimaryKey var id: String? = null,
    var apiCurrentScaleId: String? = null,
    var currentScaleId: String? = null
) : RealmObject() {
    constructor () : this(id = UUID.randomUUID().toString())
}

