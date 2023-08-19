package ru.netology.network.dto

data class UserResponse(
    val id: Long = 0,
    val login: String,
    val name: String,
    val avatar: String? = null
)