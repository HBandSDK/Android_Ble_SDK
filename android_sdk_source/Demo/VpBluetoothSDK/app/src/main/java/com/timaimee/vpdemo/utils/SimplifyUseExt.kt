package com.timaimee.vpdemo.utils

import com.veepoo.protocol.model.enums.EFunctionStatus

fun String?.default(def: String) = this.takeIf { it!!.isNotBlank() } ?: def
fun Boolean.toDes(trueDes:String, falseDes:String) = if(this) trueDes else falseDes

fun <T> Boolean.toObj(trueObj:T, falseObj:T) = if(this) trueObj else falseObj

fun <T> Boolean.switch(trueObj:T, falseObj:T) = if(this) trueObj else falseObj

fun EFunctionStatus.toDes() = when(this) {
    EFunctionStatus.UNSUPPORT -> "不支持"
    EFunctionStatus.SUPPORT -> "支持"
    EFunctionStatus.SUPPORT_OPEN -> "开启"
    EFunctionStatus.SUPPORT_CLOSE -> "关闭"
    EFunctionStatus.UNKONW -> "未知"
}