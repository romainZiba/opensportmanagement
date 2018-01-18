package com.zcorp.opensportmanagement.services

import com.zcorp.opensportmanagement.model.Stadium
import com.zcorp.opensportmanagement.model.Team
import org.springframework.data.repository.CrudRepository

interface StadiumRepository : CrudRepository<Stadium, Int> {

    fun findByName(name: String): Stadium
}