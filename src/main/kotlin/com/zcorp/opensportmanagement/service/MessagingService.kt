package com.zcorp.opensportmanagement.service

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rethinkdb.RethinkDB
import com.rethinkdb.gen.ast.Db
import com.rethinkdb.gen.ast.Table
import com.rethinkdb.net.Cursor
import com.zcorp.opensportmanagement.dto.MessageDto
import com.zcorp.opensportmanagement.messaging.MessageChangesListener
import com.zcorp.opensportmanagement.messaging.RethinkDBConnectionFactory
import com.zcorp.opensportmanagement.model.Conversation
import com.zcorp.opensportmanagement.model.Message
import com.zcorp.opensportmanagement.rest.MessageController
import com.zcorp.opensportmanagement.rest.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

/**
 * This class is used to access the Rethink database
 * Conversion of type needs to be done due to the following bug: https://github.com/rethinkdb/rethinkdb/issues/5859
 */
@Service
class MessagingService @Autowired constructor(
        private val userService: UserService,
        private val connectionFactory: RethinkDBConnectionFactory,
        private val messageChangesListener: MessageChangesListener) : InitializingBean {

    private val log = LoggerFactory.getLogger(MessageController::class.java)

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
                { row -> row.g("authorUsername").eq(username)
                        .or(row.g("recipients").isEmpty)
                        .or(row.g("recipients").contains(username)) })
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

    fun getMessagesFromEvent(eventId: Int): List<MessageDto> {
        val conversationId = "conversation_$eventId"
        return getMessages(conversationId)
    }

    fun getMessages(conversationId: String): List<MessageDto> {
        val connection = connectionFactory.createConnection()
        val messagesFromDb: List<Any> = table.filter({ row -> row.g(CONVERSATION_ID).eq(conversationId) })
                .orderBy(indexTime)
                .run(connection)
        if (messagesFromDb.isNotEmpty()) {
            val mapper = jacksonObjectMapper()
            mapper.findAndRegisterModules()
            val messages: List<Message> = mapper.convertValue(messagesFromDb)
            return messages.map { it.toDto() }
        }
        return emptyList()
    }

    fun createMessage(messageDto: MessageDto, authorName: String, eventId: Int? = null): MessageDto {
        val author = userService.findByUsername(authorName) ?: throw NotFoundException("User $authorName does not exist")
        val connection = connectionFactory.createConnection()
        val message = Message()
        message.authorUsername = authorName
        message.authorFirstName = author.firstName
        message.authorLastName = author.lastName
        message.time = OffsetDateTime.now()
        message.body = messageDto.body

        val conversationId: String
        val conversationTopic: String

        if (eventId == null) {
            conversationId = messageDto.conversationId ?: UUID.randomUUID().toString()
            conversationTopic = messageDto.conversationTopic ?: "New subject"

        } else {
            if (messageDto.conversationId != null) {
                throw UnexpectedParameterException("conversationId")
            }
            if (messageDto.recipients.isNotEmpty()) {
                throw UnexpectedParameterException("recipients")
            }
            if (messageDto.conversationTopic != null) {
                throw UnexpectedParameterException("conversationTopic")
            }
            conversationId = "conversation_$eventId"
            conversationTopic = "Thread of event"
        }
        message.conversationId = conversationId
        val data: Cursor<Any> = table.filter(
                { row -> row.g(CONVERSATION_ID).eq(conversationId) })
                .pluck(CONVERSATION_TOPIC, RECIPIENTS)
                .limit(1)
                .run(connection)
        if (data.hasNext()) {
            // A message already exists with same conversation ID
            val mapper = jacksonObjectMapper()
            mapper.findAndRegisterModules()
            val messageFromDB: Message = mapper.convertValue(data.next())
            message.conversationTopic = messageFromDB.conversationTopic
            message.recipients = messageFromDB.recipients
        } else {
            // This is the first message of a conversation
            message.conversationTopic = conversationTopic
            message.recipients = messageDto.recipients
        }
        val run = table.insert(message).run<Any>(connection)
        log.info("Insert {}", run)
        return message.toDto()
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
