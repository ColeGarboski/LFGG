package com.example.lfgg
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ChatObject {
    var chatId: String? = null
    var chatName : String? = null
    var currentPlayers: Int = 0
    var gameName: String? = null
    var maxPlayers: Int = 0
    var platform : String? = null
    var timeCreated : String? = null
    var members : ArrayList<String> = ArrayList()
    var sortValue : Float = 999f

    constructor(){}

    constructor(chatId: String?, chatName: String?, currentPlayers: Int, gameName: String?, maxPlayers: Int, platform: String?, timeCreated: String?, members: ArrayList<String>) {
        this.chatId = chatId
        this.chatName = chatName
        this.currentPlayers = currentPlayers
        this.gameName = gameName
        this.maxPlayers = maxPlayers
        this.platform = platform
        this.timeCreated = timeCreated
        this.members = members

    }
}