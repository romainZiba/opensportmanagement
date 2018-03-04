package com.zcorp.opensportmanagement.model

data class Conversation(val conversationId: String, val conversationTopic: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Conversation

        if (conversationId != other.conversationId) return false

        return true
    }

    override fun hashCode(): Int {
        return conversationId.hashCode()
    }
}