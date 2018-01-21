package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Stadium
import org.springframework.data.repository.CrudRepository

interface StadiumRepository : CrudRepository<Stadium, Int> {

    fun findByName(name: String): Stadium?
}