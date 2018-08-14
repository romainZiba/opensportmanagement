package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface PlaceRepository : JpaRepository<Place, Int>