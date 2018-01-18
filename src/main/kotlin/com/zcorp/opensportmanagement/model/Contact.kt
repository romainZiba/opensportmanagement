package com.zcorp.opensportmanagement.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "contact")
data class Contact(val phoneNumber: String,
                   val email: String,
                   @Id @GeneratedValue val id: Int = -1)