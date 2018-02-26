package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(
        name = "championship",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "season_id"))))
data class Championship(@Column(name = "name") val name: String,
                        @ManyToOne @JsonBackReference @JoinColumn(name = "season_id") val season: Season,
                        @Id @GeneratedValue val id: Int = -1) {

    @OneToMany(mappedBy = "championship", cascade = [CascadeType.REMOVE])
    @JsonManagedReference
    val matches: MutableSet<Match> = mutableSetOf()


    override fun toString(): String {
        return "Championship(name='$name', id=$id)"
    }
}