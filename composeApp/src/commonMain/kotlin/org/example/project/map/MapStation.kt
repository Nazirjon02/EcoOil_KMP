package org.example.project.map

import org.example.data.Station as ApiStation
import org.example.data.MapFuelPrice
import org.example.data.MapStation


private fun String.toDoubleSafe(): Double =
    this.replace(',', '.').toDoubleOrNull() ?: 0.0

private fun Int.toBool(): Boolean = this == 1

fun ApiStation.toMapStation(): MapStation {
    val prices = buildList {
        ai92_price?.takeIf { it.isNotBlank() }?.let { add(MapFuelPrice("ai92", "АИ-92", it)) }
        ai95_price?.takeIf { it.isNotBlank() }?.let { add(MapFuelPrice("ai95", "АИ-95", it)) }
        dt_price?.takeIf { it.isNotBlank() }?.let { add(MapFuelPrice("dt", "ДТ", it)) }
        dtecto_price?.takeIf { it.isNotBlank() }?.let { add(MapFuelPrice("dtecto", "ДТ-ЭКТО", it)) }
        gas_price?.takeIf { it.isNotBlank() }?.let { add(MapFuelPrice("gas", "ГАЗ", it)) }
    }

    return MapStation(
        name = station_name,
        snippet = station_snippet,
        latitude = station_latitude.toDoubleSafe(),
        longitude = station_longitude.toDoubleSafe(),
        hasShop = station_shop.toBool(),
        workAroundTime = station_work_around_time.toBool(),
        hasCoffee = station_coffee.toBool(),
        hasToilet = station_toilet.toBool(),
        hasPayTerminal = station_pay_terminal.toBool(),
        prices = prices
    )
}