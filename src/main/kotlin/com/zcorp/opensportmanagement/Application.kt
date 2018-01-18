package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.services.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.time.LocalDate
import java.time.Month
import java.time.ZonedDateTime

@SpringBootApplication
class Application {

    private val LOG = LoggerFactory.getLogger(Application::class.java)

    @Bean
    fun init(teamRepository: TeamRepository,
             stadiumRepository: StadiumRepository,
             eventRepository: EventRepository,
             opponentRepository: OpponentRepository,
             contactRepository: ContactRepository,
             seasonRepository: SeasonRepository,
             championshipRepository: ChampionshipRepository) = CommandLineRunner {
        // save entities
        val season = seasonRepository.save(Season("2017-2018",
                LocalDate.of(2017, Month.SEPTEMBER, 1),
                LocalDate.of(2018, Month.JULY, 31),
                Status.CURRENT))
        val championship = championshipRepository.save(Championship("Championnat 2017-2018", season))
        val stadium = stadiumRepository.save(Stadium("LE stade", "2 allée", "Toulouse"))
        val contact = contactRepository.save(Contact("0159756563", "testmail@gmail.com"))
        val opponent = opponentRepository.save(Opponent("TCMS2", contact))
        val event = eventRepository.save(Event(EventType.FRIENDLY, opponent, ZonedDateTime.now(), stadium))
        val anotherEvent = eventRepository.save(ChampionshipEvent(championship, opponent, ZonedDateTime.now(), stadium))
        teamRepository.save(Team("MyTeam", Sport.BASKETBALL, Gender.BOTH, AgeGroup.ADULTS, stadium, hashSetOf(event, anotherEvent)))

        // fetch all customers
        LOG.info("Teams found with findAll():")
        LOG.info("-------------------------------")
        teamRepository.findAll().forEach { LOG.info(it.toString()) }
        LOG.info("")


        // fetch customers by last name
        LOG.info("Team found with findByLastName('MyTeam'):")
        LOG.info("--------------------------------------------")
        LOG.info(teamRepository.findByName("MyTeam").toString())
        LOG.info("")
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}