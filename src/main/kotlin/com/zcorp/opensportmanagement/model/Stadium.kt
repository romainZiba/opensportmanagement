package com.zcorp.opensportmanagement.model

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "stadium")
data class Stadium(@NotNull @Column(unique = true) val name: String,
                   val address: String,
                   val city: String,
                   @Id @GeneratedValue val id: Int = -1)