package org.example.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseStock(
    val code: Int,
    val message: String,
    val data: DataStock
)

@Serializable
data class DataStock(
    val list_stock: List<Stock>
)

@Serializable
data class Stock(
    val stock_title: String,
    val stock_date_time: String,
    val stock_image: String
)
