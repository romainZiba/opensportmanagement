package com.zcorp.opensportmanagement.services

import com.zcorp.opensportmanagement.model.Season
import org.springframework.data.repository.CrudRepository

interface SeasonRepository : CrudRepository<Season, Int>