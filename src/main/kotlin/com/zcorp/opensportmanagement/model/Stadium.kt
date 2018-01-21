package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(
        name = "stadium",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "team_id"))))
data class Stadium(@Column(name = "name") @NotNull val name: String,
                   val address: String,
                   val city: String,
                   @ManyToOne @JsonBackReference @JoinColumn(name = "team_id") val team: Team,
                   @Id @GeneratedValue val id: Int = -1)