package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.MessageDto
import com.zcorp.opensportmanagement.model.Conversation
import com.zcorp.opensportmanagement.service.MessagingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/conversations")
class MessageController @Autowired constructor(private val messagingService: MessagingService) {

    @GetMapping
    fun getConversations(authentication: Authentication): Set<Conversation> {
        return messagingService.getConversations(authentication.name)
    }

    @GetMapping("/{conversationId}/messages")
    fun getMessages(@PathVariable("conversationId") conversationId: String): List<MessageDto> {
        // TODO: check if user is allowed to access to this conversationId
        return messagingService.getMessages(conversationId)
    }

    @PostMapping("/messages")
    fun createMessage(
        @RequestBody messageDto: MessageDto,
        authentication: Authentication
    ): MessageDto {
        // TODO: check if user is allowed to send message to these users
        return messagingService.createMessage(messageDto, authentication.name)
    }
}
