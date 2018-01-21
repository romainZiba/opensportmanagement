package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(
        name = "opponent",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "team_id"))))
data class Opponent(@Column(name = "name") val name: String,
                    val phoneNumber: String,
                    val email: String,
                    @ManyToOne @JsonBackReference @JoinColumn(name = "team_id") val team: Team,
                    @Id @GeneratedValue val id: Int = -1)

class OpponentDto(val name: String, val phoneNumber: String, val email: String)