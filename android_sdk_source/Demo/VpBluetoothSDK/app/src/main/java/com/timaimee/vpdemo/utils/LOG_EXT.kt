package com.timaimee.vpdemo.utils

import com.veepoo.protocol.util.VPLogger

fun String.e(log: String? = null) {
    VPLogger.e(this.plus(log ?: ""))
}

fun String.i(log: String? = null) {
    VPLogger.i(this.plus(log ?: ""))
}

fun String.d(log: String? = null) {
    VPLogger.d(this.plus(log ?: ""))
}

fun String.w(log: String? = null) {
    VPLogger.w(this.plus(log ?: ""))
}

fun String.v(log: String? = null) {
    VPLogger.v(this.plus(log ?: ""))
}