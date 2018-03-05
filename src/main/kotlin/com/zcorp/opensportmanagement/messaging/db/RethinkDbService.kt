package com.zcorp.opensportmanagement.messaging.db

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rethinkdb.RethinkDB
import com.rethinkdb.gen.ast.Db
import com.rethinkdb.gen.ast.Table
import com.rethinkdb.net.Cursor
import com.zcorp.opensportmanagement.messaging.MessageChangesListener
import com.zcorp.opensportmanagement.model.Conversation
import com.zcorp.opensportmanagement.model.Message
import com.zcorp.opensportmanagement.rest.MessageController
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime
import java.util.*

/**
 * This class is used to access the Rethink database
 * Conversion of type needs to be done due to the following bug: https://github.com/rethinkdb/rethinkdb/issues/5859
 */
class RethinkDbService : InitializingBean {

    private val log = LoggerFactory.getLogger(MessageController::class.java)

    @Autowired
    private lateinit var connectionFactory: RethinkDBConnectionFactory

    @Autowired
    private lateinit var messageChangesListener: MessageChangesListener

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        createDb()
        messageChangesListener.pushChangesToWebSocket()
    }

    private fun createDb() {
        val connection = connectionFactory.createConnection()
        val dbList = r.dbList().run<List<String>>(connection)
        if (!dbList.contains(dbName)) {
            r.dbCreate(dbName).run<Any>(connection)
        }
        val tables = db.tableList().run<List<String>>(connection)
        if (!tables.contains(tableName)) {
            db.tableCreate(tableName).run<Any>(connection)
            db.table(tableName).indexCreate(indexTime).run<Any>(connection)
        }
    }

    fun getConversations(username: String): Set<Conversation> {
        val connection = connectionFactory.createConnection()
        val messagesFromDb: List<Any> = table.filter(
                { row -> row.g("from").eq(username).or(row.g("recipients").isEmpty).or(row.g("recipients").contains(username)) })
                .orderBy(indexTime)
                .run(connection)
        if (messagesFromDb.isNotEmpty()) {
            val mapper = jacksonObjectMapper()
            mapper.findAndRegisterModules()
            val messages: List<Message> = mapper.convertValue(messagesFromDb)
            return messages.map { Conversation(it.conversationId, it.conversationTopic) }.toSet()
        }
        return emptySet()
    }

    fun getMessages(conversation: String): List<Message> {
        val connection = connectionFactory.createConnection()
        val messagesFromDb: List<Any> = table.filter({ row -> row.g(CONVERSATION_ID).eq(conversation) })
                .orderBy(indexTime)
                .run(connection)
        if (messagesFromDb.isNotEmpty()) {
            val mapper = jacksonObjectMapper()
            mapper.findAndRegisterModules()
            val messages: List<Message> = mapper.convertValue(messagesFromDb)
            return messages
        }
        return emptyList()
    }

    fun createConversation(message: Message) {
        val connection = connectionFactory.createConnection()
        message.time = OffsetDateTime.now()
        val conversationId = message.conversationId
        if (conversationId.isEmpty()) {
            message.conversationId = UUID.randomUUID().toString()
            val run = table.insert(message).run<Any>(connection)
            log.info("Insert {}", run)
        }
    }

    fun createMessageInConversation(message: Message): Message {
        val connection = connectionFactory.createConnection()
        message.time = OffsetDateTime.now()
        val conversationId = message.conversationId

        val data: Cursor<Any> = table.filter(
                { row -> row.g(CONVERSATION_ID).eq(conversationId) })
                .pluck(CONVERSATION_TOPIC, RECIPIENTS)
                .limit(1)
                .run(connection)
        if (data.hasNext()) {
            val mapper = jacksonObjectMapper()
            mapper.findAndRegisterModules()
            val messageFromDB: Message = mapper.convertValue(data.next())
            message.conversationTopic = messageFromDB.conversationTopic
            message.recipients = messageFromDB.recipients
            val run = table.insert(message).run<Any>(connection)
            log.info("Insert {}", run)
            return message
        }
        return message
    }

    companion object {
        private const val dbName = "opensportmanagement"
        private val r: RethinkDB = RethinkDB.r
        private val db: Db = r.db(dbName)
        private const val tableName = "messages"
        val table: Table = db.table(tableName)
        const val indexTime = "time"
        private const val CONVERSATION_ID = "conversationId"
        private const val CONVERSATION_TOPIC = "conversationTopic"
        private const val RECIPIENTS = "recipients"
    }
}
