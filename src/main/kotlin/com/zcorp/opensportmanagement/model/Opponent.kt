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
                    @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Opponent(name='$name', phoneNumber='$phoneNumber', email='$email', id=$id)"
    }

    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return other is Opponent && other.name.equals(name) && other.team.equals(team)
        }
        return false
    }
}

class OpponentDto(val name: String, val phoneNumber: String, val email: String)