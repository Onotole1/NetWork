package ru.netology.network.ui.activity.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.network.ui.activity.auth.AuthState
import ru.netology.network.ui.activity.dto.Event
import ru.netology.network.ui.activity.dto.Job
import ru.netology.network.ui.activity.dto.MediaRequest
import ru.netology.network.ui.activity.dto.MediaResponse
import ru.netology.network.ui.activity.dto.Post
import ru.netology.network.ui.activity.dto.Users

interface PostRepository {
    val posts: Flow<List<Post>>
    val events: Flow<List<Event>>
    val users: Flow<List<Users>>
    val jobs: Flow<List<Job>>
    suspend fun getPosts()
    suspend fun getPostsByAuthor(userId: Long)
    suspend fun upload(upload: MediaRequest): MediaResponse
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaRequest)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun userAuthentication(login: String, pass: String): AuthState
    suspend fun userRegistration(login: String, pass: String, name: String): AuthState
    suspend fun userRegistrationWithAvatar(
        login: String,
        pass: String,
        name: String,
        avatar: MediaRequest
    ): AuthState

    suspend fun getUsers()
    suspend fun getEvents()
    suspend fun saveEvent(event: Event)
    suspend fun saveEventWithAttachment(event: Event, upload: MediaRequest)
    suspend fun likeEventById(id: Long, likedByMe: Boolean)
    suspend fun removeEventById(id: Long)
    suspend fun partEventById(id: Long, participatedByMe: Boolean)
    suspend fun getJobs(userId: Long, currentUser: Long)
    suspend fun saveJob(userId: Long, job: Job)
    suspend fun removeJobById(id: Long)
}