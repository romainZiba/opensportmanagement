package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.messaging.Message
import com.zcorp.opensportmanagement.messaging.db.DbInitializer.Companion.db
import com.zcorp.opensportmanagement.messaging.db.DbInitializer.Companion.indexTime
import com.zcorp.opensportmanagement.messaging.db.DbInitializer.Companion.tableName
import com.zcorp.opensportmanagement.messaging.db.RethinkDBConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
@RequestMapping("/messages")
class MessageController {

    private val log = LoggerFactory.getLogger(MessageController::class.java)

    @Autowired
    private lateinit var connectionFactory: RethinkDBConnectionFactory

    @RequestMapping(method = [(RequestMethod.GET)])
    fun get(): List<Message> {
        return db.table(tableName)
                .orderBy(indexTime)
                .run(connectionFactory.createConnection(), Message::class.java)
    }

    @RequestMapping(method = [(RequestMethod.POST)])
    fun postMessage(@RequestBody message: Message): Message {
        message.time = OffsetDateTime.now()
        val run = db.table(tableName).insert(message)
                .run<Any>(connectionFactory.createConnection())

        log.info("Insert {}", run)
        return message
    }
}
