package com.ahgitdevelopment.groupfilmmanager.data

data class User(
    val name: String = "",
    val isWatched: Boolean = false,
    val isWanted: Boolean = false) {

    constructor(user: User) : this(
        name = user.name,
        isWatched = user.isWatched,
        isWanted = user.isWanted)

}