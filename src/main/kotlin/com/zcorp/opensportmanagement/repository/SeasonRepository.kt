package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Season
import org.springframework.data.jpa.repository.JpaRepository

interface SeasonRepository : JpaRepository<Season, Int>