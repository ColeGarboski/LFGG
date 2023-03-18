package com.example.lfgg

class ChatObject {
    var chatId: String? = null
    var chatName : String? = null

    constructor(){}

    constructor(chatId: String?, chatName: String?) {
        this.chatId = chatId
        this.chatName = chatName
    }
}