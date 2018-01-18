package com.zcorp.opensportmanagement.model

import javax.persistence.*

@Entity
@Table(name = "opponent")
data class Opponent(@Column(unique = true) val name: String,
                    @ManyToOne val contact: Contact,
                    @Id @GeneratedValue val id: Int = -1)