package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "team_member",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("username", "team_id"))))
data class TeamMember(
        @ManyToOne @JsonBackReference(value = "memberOf") @JoinColumn(name = "username") val user: User,
        @ElementCollection val roles: MutableSet<Role>,
        @ManyToOne @JsonBackReference @JoinColumn(name = "team_id") val team: Team,
        @Id @GeneratedValue val id: Int = -1) {

    var licenseNumber: String? = null

    override fun toString(): String {
        return "TeamMember(user='$user', roles='$roles')"
    }
}

enum class Role {
    PLAYER, COACH, ADMIN
}