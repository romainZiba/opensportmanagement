package com.zcorp.opensportmanagement.messaging

import com.rethinkdb.net.Cursor
import com.zcorp.opensportmanagement.service.MessagingService.Companion.table
import com.zcorp.opensportmanagement.model.Message
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
open class MessageChangesListener {
    private val log = LoggerFactory.getLogger(MessageChangesListener::class.java)

    @Autowired
    private lateinit var connectionFactory: RethinkDBConnectionFactory

    @Autowired
    private lateinit var webSocket: SimpMessagingTemplate

    @Async
    open fun pushChangesToWebSocket() {
        val connection = connectionFactory.createConnection()
        if (connection != null) {
            val cursor: Cursor<Message> = table.changes()
                    .getField("new_val")
                    .run(connection, Message::class.java)

            while (cursor.hasNext()) {
                val message = cursor.next()
                log.info("New body: {}", message.body)
                webSocket.convertAndSend("/topic/" + message.conversationId, message.toDto())
            }
        }
    }
}
