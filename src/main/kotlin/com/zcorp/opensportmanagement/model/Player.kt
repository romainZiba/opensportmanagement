package com.zcorp.opensportmanagement.model

import javax.persistence.*

@Entity
@Table(name = "player")
data class Player(@OneToOne val account: Account,
                  val licenseNumber: Number,
                  @Id @GeneratedValue val id: Int = -1)