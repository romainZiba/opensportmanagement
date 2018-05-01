package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(
        name = "opponent",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Opponent(@Column(name = "name") val name: String,
                    val phoneNumber: String,
                    val email: String,
                    @ManyToOne @JsonBackReference @JoinColumn(name = "team_id") val team: Team,
                    val imgUrl: String = "",
                    @Id @GeneratedValue val id: Int = -1)