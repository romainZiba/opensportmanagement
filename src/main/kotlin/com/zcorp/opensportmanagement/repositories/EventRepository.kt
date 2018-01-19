package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Event
import org.springframework.data.repository.CrudRepository

interface EventRepository : CrudRepository<Event, Int>