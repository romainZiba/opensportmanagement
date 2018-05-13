package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.rethinkdb.net.Connection
import com.zcorp.opensportmanagement.dto.MessageDto
import com.zcorp.opensportmanagement.messaging.MessageChangesListener
import com.zcorp.opensportmanagement.messaging.RethinkDBConnectionFactory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class MessagingServiceTest {
    private lateinit var messagingService: MessagingService
    private lateinit var connectionFactoryMock: RethinkDBConnectionFactory
    private lateinit var changeListenerMock: MessageChangesListener
    private lateinit var connectionMock: Connection
    private lateinit var userServiceMock: UserService

    @Before
    fun setUp() {
        userServiceMock = mock()
        changeListenerMock = mock()
        connectionMock = mock()
        connectionFactoryMock = mock()
        messagingService = MessagingService(userServiceMock, connectionFactoryMock, changeListenerMock)
    }

    @Test
    @Ignore
    fun createNewMessageForEvent() {
        whenever(connectionFactoryMock.createConnection()).thenReturn(connectionMock)
        val body = "a message body"
        val dto = MessageDto(body)
        val messageCreated = messagingService.createMessage(dto, "me", 1)
        assertEquals("conversation_1", messageCreated.conversationId)
        assertEquals(body, messageCreated.body)
        assertEquals(emptyList<Int>(), messageCreated.recipients)
    }
}