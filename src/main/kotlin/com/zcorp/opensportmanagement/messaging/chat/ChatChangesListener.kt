package com.zcorp.opensportmanagement.messaging.chat

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Cursor
import com.zcorp.opensportmanagement.messaging.db.RethinkDBConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
open class ChatChangesListener {
    private val log = LoggerFactory.getLogger(ChatChangesListener::class.java)

    @Autowired
    private lateinit var connectionFactory: RethinkDBConnectionFactory

    @Autowired
    private lateinit var webSocket: SimpMessagingTemplate

    @Async
    open fun pushChangesToWebSocket() {
        val cursor: Cursor<ChatMessage> = r.db("chat").table("messages").changes()
                .getField("new_val")
                .run(connectionFactory.createConnection(), ChatMessage::class.java)

        while (cursor.hasNext()) {
            val chatMessage = cursor.next()
            log.info("New message: {}", chatMessage.message)
            webSocket.convertAndSend("/topic/messages", chatMessage)
        }
    }

    companion object {
        private val r = RethinkDB.r
    }
}
