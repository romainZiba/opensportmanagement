package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Opponent
import org.springframework.data.jpa.repository.JpaRepository

interface OpponentRepository : JpaRepository<Opponent, Int> {
    fun findByName(name: String): Opponent?
}