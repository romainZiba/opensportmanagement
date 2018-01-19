package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "season")
data class Season(@Column(unique = true) val name: String,
                  @Convert(converter = LocalDateAttributeConverter::class) val fromDate: LocalDate,
                  @Convert(converter = LocalDateAttributeConverter::class) val toDate: LocalDate,
                  @Enumerated(EnumType.STRING) val status: Status,
                  @OneToMany(mappedBy = "season") @JsonManagedReference val championships: MutableSet<Championship>,
                  @ManyToOne @JsonBackReference val team: Team,
                  @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Season(name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            if (other is Season) {
                return other.id == this.id
            }
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }
}

enum class Status {
    CURRENT, CLOSED
}