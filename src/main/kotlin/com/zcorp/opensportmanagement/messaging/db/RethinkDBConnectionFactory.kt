package com.zcorp.opensportmanagement.messaging.db


import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import java.util.concurrent.TimeoutException

class RethinkDBConnectionFactory(private val host: String) {

    fun createConnection(): Connection {
        try {
            return RethinkDB.r.connection().hostname(host).connect()
        } catch (e: TimeoutException) {
            throw RuntimeException(e)
        }
    }
}
