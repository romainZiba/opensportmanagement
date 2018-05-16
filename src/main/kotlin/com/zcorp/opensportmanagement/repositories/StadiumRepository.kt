package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Stadium
import org.springframework.data.jpa.repository.JpaRepository

interface StadiumRepository : JpaRepository<Stadium, Int>