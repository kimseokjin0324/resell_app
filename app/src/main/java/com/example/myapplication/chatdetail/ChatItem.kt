package com.example.myapplication.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String,
) {
    constructor() :this("","")
}
