package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "team_member")
data class TeamMember(
        @ManyToOne @JsonBackReference(value = "memberOf") val user: User,
        val admin: Boolean?,
        @ElementCollection val roles: MutableSet<Role>,
        val licenseNumber: Number?,
        @ManyToOne @JsonBackReference val team: Team,
        @Id @GeneratedValue val id: Int = -1) {

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return other is TeamMember && other.user.username == user.username
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "TeamMember(user='$user', admin='$admin', roles='$roles')"
    }
}

enum class Role {
    PLAYER, COACH, ADMIN
}