package com.electricitybill.calculator

data class BillRecord(
    val id: Long,
    val m1: Int,       // month index 0-11
    val m2: Int,
    val last: Double,
    val curr: Double,
    val units: Double,
    val bill: Double,
    val rate: Double,
    val date: String
)
