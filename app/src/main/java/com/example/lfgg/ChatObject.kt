package com.example.lfgg

class ChatObject {
    var chatId: String? = null
    var chatName : String? = null
    var gameName: String? = null
    var platform : String? = null
    var currentPlayers: Int = 0
    var maxPlayers: Int? = 0
    constructor(){}

    constructor(chatId: String?, chatName: String?, gameName: String?, platform: String?, currentPlayers: Int, maxPlayers: Int) {
        this.chatId = chatId
        this.chatName = chatName
        this.gameName = gameName
        this.platform = platform
        this.currentPlayers = currentPlayers
        this.maxPlayers = maxPlayers


    }
}