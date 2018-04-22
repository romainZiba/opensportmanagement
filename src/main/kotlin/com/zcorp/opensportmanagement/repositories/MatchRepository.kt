package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Match
import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository : JpaRepository<Match, Int>