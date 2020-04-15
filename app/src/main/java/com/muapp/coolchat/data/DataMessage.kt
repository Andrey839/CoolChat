package com.muapp.coolchat.data

data class DataMessage(
    var name: String? = "",
    var text: String = "",
    var imageuri: String? = null,
    var sender: String = "",
    var recipient: String? = "",
    var isMy: Boolean? = null
)