package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.AbstractEvent
import org.springframework.data.repository.CrudRepository

interface EventRepository : CrudRepository<AbstractEvent, Int>