package com.zcorp.opensportmanagement.services

import com.zcorp.opensportmanagement.model.Team
import org.springframework.data.repository.CrudRepository

interface TeamRepository : CrudRepository<Team, Int> {

    fun findByName(name: String): Team
}