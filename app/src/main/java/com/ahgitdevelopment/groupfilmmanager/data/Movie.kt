package com.ahgitdevelopment.groupfilmmanager.data

data class Movie(
    var name: String,
    var description1: String,
    var description2: String,
    var users: ArrayList<User> = ArrayList())


