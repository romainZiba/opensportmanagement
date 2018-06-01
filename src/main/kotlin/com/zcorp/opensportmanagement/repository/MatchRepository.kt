package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Match
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MatchRepository : JpaRepository<Match, Int> {
    @Query("DELETE FROM Match m WHERE m.championship.id = :championshipId")
    fun deleteAllMatches(championshipId: Int): Int
}