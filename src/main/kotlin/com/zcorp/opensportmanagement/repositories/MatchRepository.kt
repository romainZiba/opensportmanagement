package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Team
import org.springframework.data.repository.CrudRepository

interface MatchRepository : CrudRepository<Match, Int>