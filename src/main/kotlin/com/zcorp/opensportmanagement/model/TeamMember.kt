package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "team_member")
data class TeamMember(
        @ManyToOne @JsonBackReference(value = "memberOf") val user: User,
        val admin: Boolean?,
        val role: Role,
        val licenseNumber: Number?,
        @ManyToOne @JsonBackReference val team: Team,
        @Id @GeneratedValue val id: Int = -1) {

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return other is TeamMember && other.user.username.equals(user.username)
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "TeamMember(user='$user', admin='$admin', role='$role')"
    }
}

enum class Role {
    PLAYER, COACH, PLAYER_COACH
}