package com.example.lfgg

class User {
    var name: String? = null
    var email: String? = null
    var platform: String? = null
    var game: String? = null
    var uid: String? = null

    constructor(){}

    constructor(name: String, email: String, platform: String, game: String, uid: String) {
        this.name = name
        this.email = email
        this.platform = platform
        this.game = game
        this.uid = uid
    }
}