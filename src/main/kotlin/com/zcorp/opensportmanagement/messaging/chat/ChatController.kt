package com.zcorp.opensportmanagement.messaging.chat

import com.rethinkdb.RethinkDB
import com.zcorp.opensportmanagement.messaging.db.RethinkDBConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
@RequestMapping("/chat")
class ChatController {

    protected val log = LoggerFactory.getLogger(ChatController::class.java)

    @Autowired
    private lateinit var connectionFactory: RethinkDBConnectionFactory

    @RequestMapping(method = [(RequestMethod.GET)])
    fun get(): List<ChatMessage> {
        return r.db("chat").table("messages")
                .orderBy("time")
                .run(connectionFactory.createConnection(), ChatMessage::class.java)
    }

    @RequestMapping(method = [(RequestMethod.POST)])
    fun postMessage(@RequestBody chatMessage: ChatMessage): ChatMessage {
        chatMessage.time = OffsetDateTime.now()
        val run = r.db("chat").table("messages").insert(chatMessage)
                .run<Any>(connectionFactory.createConnection())

        log.info("Insert {}", run)
        return chatMessage
    }

    companion object {
        private val r = RethinkDB.r
    }
}
