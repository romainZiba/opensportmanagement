package com.zcorp.opensportmanagement.dto

import java.time.LocalDateTime

data class MessageDto(
    val body: String,
    val authorUserName: String,
    val authorFirstName: String,
    val authorLastName: String,
    val conversationId: String,
    val recipients: List<Int> = emptyList(),
    val conversationTopic: String,
    val creationDate: LocalDateTime
)

data class MessageCreationDto(val body: String)