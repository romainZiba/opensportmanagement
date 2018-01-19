package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import org.springframework.data.repository.CrudRepository

interface SeasonRepository : CrudRepository<Season, Int> {
    fun findByName(name: String): Season?
}