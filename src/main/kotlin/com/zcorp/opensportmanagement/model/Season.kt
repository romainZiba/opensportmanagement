package com.zcorp.opensportmanagement.model

import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

enum class Status {
    CURRENT, CLOSED
}

@Entity
@Table(name = "season")
data class Season(val name: String,
                  val from: ZonedDateTime,
                  val to: ZonedDateTime,
                  val status: Status,
                  @Id @GeneratedValue val id: Int = -1)