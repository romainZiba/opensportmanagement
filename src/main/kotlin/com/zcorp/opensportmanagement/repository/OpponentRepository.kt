package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Opponent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface OpponentRepository : JpaRepository<Opponent, Int>