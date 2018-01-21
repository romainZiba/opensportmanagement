package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "opponent")
data class Opponent(@Column(unique = true) val name: String,
                    val phoneNumber: String,
                    val email: String,
                    @ManyToOne @JsonBackReference val team: Team,
                    @Id @GeneratedValue val id: Int = -1)

class OpponentDto(val name: String, val phoneNumber: String, val email: String)