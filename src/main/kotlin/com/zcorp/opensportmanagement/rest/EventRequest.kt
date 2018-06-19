package com.zcorp.opensportmanagement.rest

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class EventRequest : Pageable {
    override fun getPageNumber(): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun hasPrevious(): Boolean {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getSort(): Sort {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun next(): Pageable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getPageSize(): Int {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getOffset(): Long {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun first(): Pageable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun previousOrFirst(): Pageable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}