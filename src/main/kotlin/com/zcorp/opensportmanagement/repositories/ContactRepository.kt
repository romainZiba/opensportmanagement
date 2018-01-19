package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Contact
import org.springframework.data.repository.CrudRepository

interface ContactRepository : CrudRepository<Contact, Int>