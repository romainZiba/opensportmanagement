package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.OffsetDateTime

/**
 * This class does not use the Kotlin feature because for now, there are some constraints due to ReflectDB
 * We ignore the id generated by RethinkDB
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class Message() {

    var message: String = ""
    var from: String = ""
    var recipients: List<String> = emptyList() // If empty, everyone is targeted
    var time: OffsetDateTime? = null
    var conversationTopic: String = ""
    var conversationId: String = ""

    constructor(conversationTopic: String, message: String, from: String, recipients: List<String>, time: OffsetDateTime?) : this() {
        this.conversationTopic = conversationTopic
        this.message = message
        this.from = from
        this.time = time
        this.recipients = recipients
    }
}
