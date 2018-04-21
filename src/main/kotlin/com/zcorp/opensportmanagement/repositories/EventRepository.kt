package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.AbstractEvent
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<AbstractEvent, Int>