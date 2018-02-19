package com.zcorp.opensportmanagement.messaging.chat

import java.time.OffsetDateTime

/**
 * This class does not use the Kotlin feature because for now, there are some constraints due to ReflectDB
 */
open class ChatMessage() {

    var message: String = ""
    var from: String = ""
    var time: OffsetDateTime? = null

    constructor(message: String, from: String, time: OffsetDateTime?): this() {
        this.message = message
        this.from = from
        this.time = time
    }
}
