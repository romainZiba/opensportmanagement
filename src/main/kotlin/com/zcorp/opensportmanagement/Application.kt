package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

@SpringBootApplication
class Application {

    private val LOG = LoggerFactory.getLogger(Application::class.java)

    @Bean
    fun init(teamRepository: TeamRepository,
             stadiumRepository: StadiumRepository,
             eventRepository: EventRepository,
             opponentRepository: OpponentRepository,
             seasonRepository: SeasonRepository,
             championshipRepository: ChampionshipRepository) = CommandLineRunner {
        // save entities

        val team = teamRepository.save(Team("MyTeam", Sport.BASKETBALL, Gender.BOTH, AgeGroup.ADULTS, mutableSetOf(),
                mutableSetOf(), mutableSetOf(), mutableSetOf()))

        val stadium = stadiumRepository.save(Stadium("LE stade", "2 allée", "Toulouse", team))

        val opponent = opponentRepository.save(Opponent("TCMS2", "0159756563", "testmail@gmail.com", team))

        val season = seasonRepository.save(Season("2017-2018",
                LocalDate.of(2017, Month.SEPTEMBER, 1),
                LocalDate.of(2018, Month.JULY, 31),
                Status.CURRENT,
                mutableSetOf(),
                team
        ))
        val championship = championshipRepository.save(Championship("Championnat 2017-2018", season, mutableSetOf()))
        val match = eventRepository.save(Match("Match de championnat", "Super match",
                LocalDateTime.of(2018, 1, 1, 10, 0, 0),
                LocalDateTime.of(2018, 1, 1, 12, 0, 0),
                stadium, opponent, team, championship))
        val event = eventRepository.save(OtherEvent(
                "Apéro",
                "Apéro avec les potes",
                LocalDateTime.of(2017, 1, 1, 10, 0, 0),
                LocalDateTime.of(2017, 1, 1, 12, 0, 0),
                "2 des champs",
                team))


        // fetch all teams
        LOG.info("Teams found with findAll():")
        LOG.info("-------------------------------")
        teamRepository.findAll().forEach { LOG.info(it.toString()) }
        LOG.info("")


        // fetch team by
        LOG.info("Team found with findByLastName('MyTeam'):")
        LOG.info("--------------------------------------------")
        LOG.info(teamRepository.findByName("MyTeam").toString())
        LOG.info("")
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}