package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Season
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface SeasonRepository : JpaRepository<Season, Int>