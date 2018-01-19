package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Opponent
import org.springframework.data.repository.CrudRepository

interface OpponentRepository : CrudRepository<Opponent, Int> {
    fun findByName(name: String): Opponent?
}