package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

@SpringBootApplication
@EnableAutoConfiguration
open class Application {

    private val LOG = LoggerFactory.getLogger(Application::class.java)

    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Transactional
    open fun init(teamRepository: TeamRepository,
                  stadiumRepository: StadiumRepository,
                  eventRepository: EventRepository,
                  opponentRepository: OpponentRepository,
                  seasonRepository: SeasonRepository,
                  championshipRepository: ChampionshipRepository,
                  userRepository: UserRepository) = CommandLineRunner {
        // save entities

        var myTeam = teamRepository.save(Team("MyTeam", Sport.BASKETBALL, Gender.BOTH, AgeGroup.ADULTS, mutableSetOf(),
                mutableSetOf(), mutableSetOf(), mutableSetOf()))

        var otherTeam = teamRepository.save(Team("OtherTeam", Sport.BASKETBALL, Gender.BOTH, AgeGroup.ADULTS, mutableSetOf(),
                mutableSetOf(), mutableSetOf(), mutableSetOf()))

        val user = userRepository.save(User("Bob", "Bobby", "bb", bCryptPasswordEncoder().encode("bb"),
                "bb@caramail.com", "", false, Role.PLAYER, 88839))

        val user2 = userRepository.save(User("Bobb", "Bobbybob", "bbb",
                bCryptPasswordEncoder().encode("bbb"), "bbb@caramail.com",
                "", false, Role.PLAYER, 88840))

        myTeam.addMember(user)

        myTeam = teamRepository.save(myTeam)

        val stadium = stadiumRepository.save(Stadium("LE stade", "2 allée", "Toulouse", myTeam))

        val opponent = opponentRepository.save(Opponent("TCMS2", "0159756563", "testmail@gmail.com", myTeam))

        val season = seasonRepository.save(Season("2017-2018",
                LocalDate.of(2017, Month.SEPTEMBER, 1),
                LocalDate.of(2018, Month.JULY, 31),
                Status.CURRENT,
                mutableSetOf(),
                myTeam
        ))
        val championship = championshipRepository.save(Championship("Championnat 2017-2018", season, mutableSetOf()))
        val match = eventRepository.save(Match("Match de championnat", "Super match",
                LocalDateTime.of(2018, 1, 1, 10, 0, 0),
                LocalDateTime.of(2018, 1, 1, 12, 0, 0),
                stadium, opponent, myTeam, championship))
        val event = eventRepository.save(OtherEvent(
                "Apéro",
                "Apéro avec les potes",
                LocalDateTime.of(2017, 1, 1, 10, 0, 0),
                LocalDateTime.of(2017, 1, 1, 12, 0, 0),
                "2 des champs",
                myTeam))


        // fetch all teams
        LOG.info("Teams found with findAll():")
        LOG.info("-------------------------------")
        teamRepository.findAll().forEach { LOG.info(it.toString()) }
        LOG.info("")


        // fetch myTeam by
        LOG.info("Team found with findByLastName('MyTeam'):")
        LOG.info("--------------------------------------------")
        LOG.info(teamRepository.findByName("MyTeam").toString())
        LOG.info("")
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}