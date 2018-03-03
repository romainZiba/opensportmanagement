package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.messaging.db.RethinkDbService
import com.zcorp.opensportmanagement.model.Conversation
import com.zcorp.opensportmanagement.model.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class MessageController {

    @Autowired
    private lateinit var rethinkDbService: RethinkDbService

    @GetMapping("/conversations")
    fun getConversations(authentication: Authentication): List<Conversation> {
        return rethinkDbService.getConversations(authentication.name)
    }

    @GetMapping("/conversations/{conversationId}/messages")
    fun getMessages(@PathVariable("conversationId") conversation: String): List<Message> {
        //TODO: check if user is allowed to access to this conversation
        return rethinkDbService.getMessages(conversation)
    }

    @PostMapping("/messages")
    fun postMessage(@RequestBody message: Message, authentication: Authentication): Message {
        message.from = authentication.name
        rethinkDbService.createMessage(message)
        return message
    }
}
