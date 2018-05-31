package com.zcorp.opensportmanagement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import javax.mail.Message

@Component
class EmailService {

    @Autowired
    private lateinit var emailSender: JavaMailSender

    fun sendMessage(to: List<String>, subject: String, text: String) {
        val message = emailSender.createMimeMessage()
        message.addRecipients(Message.RecipientType.TO, to.joinToString(", "))
        message.subject = subject
        message.setText(text)
        emailSender.send(message)
    }
}