package com.zcorp.opensportmanagement.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import javax.mail.Message

@Component
class EmailService {

    companion object {
        val LOG = LoggerFactory.getLogger(EmailService::class.java)
    }

    @Autowired
    private lateinit var emailSender: JavaMailSender

    fun sendMessage(to: List<String>, subject: String, text: String) {
        LOG.info("Sending mail to $to")
        val message = emailSender.createMimeMessage()
        message.addRecipients(Message.RecipientType.TO, to.joinToString(", "))
        message.subject = subject
        message.setText(text)
        emailSender.send(message)
    }
}