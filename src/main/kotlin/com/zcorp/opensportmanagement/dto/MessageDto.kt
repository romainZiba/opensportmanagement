package com.zcorp.opensportmanagement.dto

import java.time.LocalDateTime

data class MessageDto(
    val body: String,
    val authorUserName: String? = null,
    val authorFirstName: String? = null,
    val authorLastName: String? = null,
    val conversationId: String? = null,
    val recipients: List<Int> = emptyList(),
    var conversationTopic: String? = null,
    var creationDate: LocalDateTime? = null
)