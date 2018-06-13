package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.MessageDto
import java.time.OffsetDateTime

/**
 * This class does not use the Kotlin feature because for now, there are some constraints due to ReflectDB
 */
open class Message {
    fun toDto(): MessageDto {
        return MessageDto(
                body = body,
                authorUserName = authorUsername,
                authorFirstName = authorFirstName,
                authorLastName = authorLastName,
                conversationId = conversationId,
                recipients = recipients,
                conversationTopic = conversationTopic,
                creationDate = time.toLocalDateTime()
        )
    }

    var body = ""
    var authorUsername = "" // Username of the author
    var authorFirstName = ""
    var authorLastName = ""
    var recipients: List<Int> = emptyList() // If empty, everyone is targeted. The recipients contain list of team member ids
    var time: OffsetDateTime = OffsetDateTime.now()
    var conversationTopic = ""
    var conversationId = ""
}
