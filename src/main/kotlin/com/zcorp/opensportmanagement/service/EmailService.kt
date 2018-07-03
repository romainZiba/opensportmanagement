package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.config.NotificationsProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import javax.mail.Message

@Component
class EmailService @Autowired constructor(
    private val notifications: NotificationsProperties
) {

    companion object {
        val LOG = LoggerFactory.getLogger(EmailService::class.java)
    }

    @Autowired
    private lateinit var emailSender: JavaMailSender

    fun sendMessage(to: List<String>, subject: String, text: String) {
        if (notifications.enabled) {
            LOG.info("Sending mail to $to")
            val message = emailSender.createMimeMessage()
            message.addRecipients(Message.RecipientType.TO, to.joinToString(", "))
            message.subject = subject
            message.setText(text)
            emailSender.send(message)
        } else {
            LOG.debug("Notifications would have been sent to $to with subject '$subject' and text message '$text'")
        }
    }
}