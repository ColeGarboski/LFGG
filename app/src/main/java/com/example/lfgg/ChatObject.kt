package com.example.lfgg

class ChatObject {
    var chatId: String? = null
    var chatName : String? = null
    var currentPlayers: Int = 0
    var gameName: String? = null
    var maxPlayers: Int = 0
    var platform : String? = null
    constructor(){}

    constructor(chatId: String?, chatName: String?, currentPlayers: Int, gameName: String?, maxPlayers: Int, platform: String?) {
        this.chatId = chatId
        this.chatName = chatName
        this.currentPlayers = currentPlayers
        this.gameName = gameName
        this.maxPlayers = maxPlayers
        this.platform = platform


    }
}