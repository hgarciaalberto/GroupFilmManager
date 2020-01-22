package com.ahgitdevelopment.groupfilmmanager.data

import java.util.*

data class User(
    var id: String = "",
    var name: String = "",
    var isWatched: Boolean = false,
    var isWanted: Boolean = false) : Comparable<User> {

    override fun compareTo(other: User): Int =
        if (this.name.toLowerCase(Locale.getDefault()) > other.name.toLowerCase(Locale.getDefault())) 1 else -1
}