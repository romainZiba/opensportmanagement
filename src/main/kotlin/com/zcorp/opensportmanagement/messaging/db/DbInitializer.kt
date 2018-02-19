package com.zcorp.opensportmanagement.messaging.db

import com.rethinkdb.RethinkDB
import com.zcorp.opensportmanagement.messaging.chat.ChatChangesListener
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

class DbInitializer : InitializingBean {
    @Autowired
    private lateinit var connectionFactory: RethinkDBConnectionFactory

    @Autowired
    private lateinit var chatChangesListener: ChatChangesListener

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        createDb()
        chatChangesListener.pushChangesToWebSocket()
    }

    private fun createDb() {
        val connection = connectionFactory.createConnection()
        val dbList = r.dbList().run<List<String>>(connection)
        if (!dbList.contains("chat")) {
            r.dbCreate("chat").run<Any>(connection)
        }
        val tables = r.db("chat").tableList().run<List<String>>(connection)
        if (!tables.contains("messages")) {
            r.db("chat").tableCreate("messages").run<Any>(connection)
            r.db("chat").table("messages").indexCreate("time").run<Any>(connection)
        }
    }

    companion object {

        private val r = RethinkDB.r
    }
}
