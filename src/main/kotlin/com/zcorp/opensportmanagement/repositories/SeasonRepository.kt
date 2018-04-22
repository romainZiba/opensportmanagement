package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Season
import org.springframework.data.jpa.repository.JpaRepository

interface SeasonRepository : JpaRepository<Season, Int> {
    fun findByName(name: String): Season?
}