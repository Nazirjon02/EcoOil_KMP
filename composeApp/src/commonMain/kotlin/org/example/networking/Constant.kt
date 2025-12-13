package org.example.networking

import androidx.compose.ui.graphics.Color

object Constant {
    //POST
    const val baseUrl="http://95.142.86.183:8080"
    const val chackPhoneNumber="/check_phone_number"
    const val checkSMS="/check_verification_code"
    const val getTransactions="/get_transactions"
    const val getCar="/get_car_data"
    const val getStocks="/get_stocks"
    const val getToken="/set_car_token_notification"
    const val getQr="/set_car_qr_code"
    const val getMap="/get_station_map_data"
}
object Color{

    val colorSegenj     = Color(0xFF00CBFE)
    val colorSegenj1    = Color(0xFF0068AE)
    val colorPre        = Color(0xFF264DCF)
    val colorText       = Color(0xFF8A602E)
    val menuUnColor     = Color(0xFF727176)
    val colorBlack      = Color(0xFF000000)
    val color3          = Color(0xFFFE7700)
    val colorBlue       = Color(0xFF0068AE)
    val colorBlueOp     = Color(0xFF00CBFE)

    val ecoOilGray      = Color(0xFF636363)
    val ecoOilBlue      = Color(0xFF00B4D8)
    val ecoOilOrange    = Color(0xFFFF5722)
    val ecoOilBlack     = Color(0xFF333333)
}