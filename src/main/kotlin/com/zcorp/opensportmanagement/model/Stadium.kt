package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(
        name = "stadium",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Stadium(@Column(name = "name") @NotNull val name: String,
                   val address: String,
                   val city: String,
                   @ManyToOne @JoinColumn(name = "team_id") @JsonBackReference val team: Team,
                   @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Stadium(name='$name', address='$address', city='$city', id=$id)"
    }
}