package com.intermercato.iws_m.utils

import com.intermercato.iws_m.realmModels.OtdOrder

class DataSourceTemp {

    companion object {

        fun getMaterials(): MutableList<OtdOrder.Material> {
            var list = mutableListOf<OtdOrder.Material>()


            var m1 = OtdOrder.Material("1", "Tall", "1200")
            var m2 = OtdOrder.Material("2", "Bark", "5200")
            var m3 = OtdOrder.Material("3", "Gran", "2200")
            list.add(m1)
            list.add(m2)
            list.add(m3)

            return list
        }

        fun getMaterials2(): MutableList<OtdOrder.Material> {
            var list = mutableListOf<OtdOrder.Material>()


            var m1 = OtdOrder.Material("11", "Gran", "200")
            var m2 = OtdOrder.Material("22", "Flis", "5200")
            var m3 = OtdOrder.Material("33", "Lärk", "2200")
            list.add(m1)
            list.add(m2)
            list.add(m3)

            return list
        }

        fun getImages(): List<OtdOrder.ShipImages> {
            var list = mutableListOf<OtdOrder.ShipImages>()


                var m = OtdOrder.ShipImages(
                    "5" ,
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png",
                    "Lastrum tillfället avstängt"
                )
                list.add(m)

            return list
        }

        fun getImages2(): List<OtdOrder.ShipImages> {
            var list = mutableListOf<OtdOrder.ShipImages>()


            var m = OtdOrder.ShipImages(
                "12" ,
                "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png",
                "Korvförsäljning på däck"
            )
            list.add(m)

            return list
        }

        fun createDataSet(): ArrayList<OtdOrder> {
            val list = ArrayList<OtdOrder>()
            list.add(
                OtdOrder(
                    "1",
                    "Västerbotten",
                    "2021-12-06T12:00:01Z",
                    "Lady Forsmark",
                    "Ved väldigt blött",
                    true,
                    getMaterials(),
                    getImages()
                )
            )
            list.add(
                OtdOrder(
                    "2",
                    "Norrbotten",
                    "2021-012-03T12:01:01Z",
                    "Red Camel",
                    "Det finns kall öl i kajutan",
                    true,
                    getMaterials2(),
                    getImages2()
                )
            )


            return list
        }
    }
}