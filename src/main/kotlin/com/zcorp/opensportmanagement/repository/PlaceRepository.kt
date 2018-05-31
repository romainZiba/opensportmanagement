package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Place
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceRepository : JpaRepository<Place, Int>