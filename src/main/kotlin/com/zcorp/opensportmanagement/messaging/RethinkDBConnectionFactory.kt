package com.zcorp.opensportmanagement.messaging

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import org.slf4j.LoggerFactory

open class RethinkDBConnectionFactory(private val host: String) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    open fun createConnection(): Connection? {
        try {
            return RethinkDB.r.connection().hostname(host).connect()
        } catch (e: Exception) {
            log.debug("Rethink database is offline")
        }
        return null
    }
}
