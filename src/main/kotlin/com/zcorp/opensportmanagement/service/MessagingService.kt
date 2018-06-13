package com.zcorp.opensportmanagement.service

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rethinkdb.RethinkDB
import com.rethinkdb.gen.ast.Db
import com.rethinkdb.gen.ast.Table
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.MessageCreationDto
import com.zcorp.opensportmanagement.dto.MessageDto
import com.zcorp.opensportmanagement.messaging.MessageChangesListener
import com.zcorp.opensportmanagement.messaging.RethinkDBConnectionFactory
import com.zcorp.opensportmanagement.model.Conversation
import com.zcorp.opensportmanagement.model.Message
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.naming.ServiceUnavailableException

/**
 * This class is used to access the Rethink database
 * Conversion of type needs to be done due to the following bug: https://github.com/rethinkdb/rethinkdb/issues/5859
 */
@Service
class MessagingService @Autowired constructor(
    private val accountService: AccountService,
    private val connectionFactory: RethinkDBConnectionFactory,
    private val messageChangesListener: MessageChangesListener
) : InitializingBean {

    private val log = LoggerFactory.getLogger(MessagingService::class.java)

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        createDb()
        messageChangesListener.pushChangesToWebSocket()
    }

    private fun createDb() {
        val connection = connectionFactory.createConnection()
        if (connection != null) {
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
    }

    fun getConversations(username: String): Set<Conversation> {
        val connection = connectionFactory.createConnection()
        if (connection != null) {
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
        }
        return emptySet()
    }

    fun getMessagesFromEvent(eventId: Int): List<MessageDto> {
        val conversationId = "conversation_$eventId"
        return getMessages(conversationId)
    }

    fun getMessages(conversationId: String): List<MessageDto> {
        val connection = connectionFactory.createConnection()
        if (connection != null) {
            val messagesFromDb: List<Any> = table.filter({ row -> row.g(CONVERSATION_ID).eq(conversationId) })
                    .orderBy(indexTime)
                    .run(connection)
            if (messagesFromDb.isNotEmpty()) {
                val mapper = jacksonObjectMapper()
                mapper.findAndRegisterModules()
                val messages: List<Message> = mapper.convertValue(messagesFromDb)
                return messages.map { it.toDto() }
            }
        }
        return emptyList()
    }

    fun createMessageInEvent(messageDto: MessageCreationDto, authorName: String, eventDto: EventDto): MessageDto {
        val author = accountService.findByUsername(authorName) ?: throw NotFoundException("Account $authorName does not exist")
        val connection = connectionFactory.createConnection()
        if (connection != null) {
            val message = Message()
            message.authorUsername = authorName
            message.authorFirstName = author.firstName
            message.authorLastName = author.lastName
            message.body = messageDto.body
            message.conversationId = "conversation_${eventDto._id}"
            message.conversationTopic = eventDto.name
            val run = table.insert(message).run<Any>(connection)
            log.info("Insert {}", run)
            return message.toDto()
        }
        throw ServiceUnavailableException("Messaging service is not available")
    }

    companion object {
        private const val dbName = "opensportmanagement"
        private val r: RethinkDB = RethinkDB.r
        private val db: Db = r.db(dbName)
        private const val tableName = "messages"
        val table: Table = db.table(tableName)
        const val indexTime = "time"
        private const val CONVERSATION_ID = "conversationId"
    }
}
