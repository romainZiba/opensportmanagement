package com.zcorp.opensportmanagement.messaging.db

import com.rethinkdb.RethinkDB
import com.zcorp.opensportmanagement.messaging.MessageChangesListener
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

class DbInitializer : InitializingBean {
    @Autowired
    private lateinit var connectionFactory: RethinkDBConnectionFactory

    @Autowired
    private lateinit var messageChangesListener: MessageChangesListener

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        createDb()
        messageChangesListener.pushChangesToWebSocket()
    }

    private fun createDb() {
        val connection = connectionFactory.createConnection()
        val dbList = r.dbList().run<List<String>>(connection)
        if (!dbList.contains(dbName)) {
            r.dbCreate(dbName).run<Any>(connection)
        }
        val tables = r.db(dbName).tableList().run<List<String>>(connection)
        if (!tables.contains(tableName)) {
            db.tableCreate(tableName).run<Any>(connection)
            db.table(tableName).indexCreate(indexTime).run<Any>(connection)
        }
    }

    companion object {
        const val dbName = "opensportmanagement"
        const val tableName = "messages"
        private val r = RethinkDB.r
        val db = r.db(dbName)
        val indexTime = "time"
    }
}
