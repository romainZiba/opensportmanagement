package com.zcorp.opensportmanagement.messaging

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import java.util.concurrent.TimeoutException

open class RethinkDBConnectionFactory(private val host: String) {
    open fun createConnection(): Connection {
        try {
            return RethinkDB.r.connection().hostname(host).connect()
        } catch (e: TimeoutException) {
            throw RuntimeException(e)
        }
    }
}
