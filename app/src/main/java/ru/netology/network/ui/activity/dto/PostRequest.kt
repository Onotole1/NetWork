package ru.netology.network.ui.activity.dto

data class PostRequest(
    val id: Long,
    val content: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val attachment: Attachment? = null,
    val mentionIds: List<Long>? = emptyList()
)