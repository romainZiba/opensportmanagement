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
                  userRepository: UserRepository,
                  teamMemberRepository: TeamMemberRepository) = CommandLineRunner {
        // save entities

        var team1 = teamRepository.save(Team("TEAM 1", Sport.BASKETBALL, Gender.BOTH, AgeGroup.ADULTS))

        var team2 = teamRepository.save(Team("TEAM 2", Sport.BASKETBALL, Gender.BOTH, AgeGroup.ADULTS))

        val coachTeam1_2 = userRepository.save(User("Coach", "Rock", "CR", bCryptPasswordEncoder().encode("CR"),
                "CR@caramail.com", ""))
        val playerTeam1 = userRepository.save(User("Player", "Wow", "PW", bCryptPasswordEncoder().encode("PW"),
                "PW@caramail.com", ""))

        val playerCoachTeam2 = userRepository.save(User("Bobb", "Bobbybob", "bbb",
                bCryptPasswordEncoder().encode("bbb"), "bbb@caramail.com",
                ""))

        val teamMember1_1 = teamMemberRepository.save(
                TeamMember(coachTeam1_2, mutableSetOf(Role.COACH, Role.ADMIN), team1)
        )
        val teamMember1_2 = teamMemberRepository.save(
                TeamMember(coachTeam1_2, mutableSetOf(Role.COACH, Role.ADMIN), team2)
        )
        teamMember1_1.licenseNumber = "12345"
        teamMember1_2.licenseNumber = "12345"
        val teamMember2 = teamMemberRepository.save(
                TeamMember(playerTeam1, mutableSetOf(Role.PLAYER), team1)
        )
        val teamMember3 = teamMemberRepository.save(
                TeamMember(playerCoachTeam2, mutableSetOf(Role.PLAYER, Role.COACH), team2)
        )
        teamMember3.licenseNumber = "255069690"

        team1 = teamRepository.save(team1)
        team2 = teamRepository.save(team2)

        val stadium = stadiumRepository.save(Stadium("LE stade", "2 allée", "Toulouse", team1))

        val opponent = opponentRepository.save(Opponent("TCMS2", "0159756563", "testmail@gmail.com", team1))

        val season = seasonRepository.save(Season("2017-2018",
                LocalDate.of(2017, Month.SEPTEMBER, 1),
                LocalDate.of(2018, Month.JULY, 31),
                Status.CURRENT,
                mutableSetOf(),
                team1
        ))
        val championship = championshipRepository.save(Championship("Championnat 2017-2018", season))
        val match = eventRepository.save(Match("Match de championnat", "Super match",
                LocalDateTime.of(2018, 1, 1, 10, 0, 0),
                LocalDateTime.of(2018, 1, 1, 12, 0, 0),
                stadium, opponent, team1, championship))
        val event = eventRepository.save(OtherEvent(
                "Apéro",
                "Apéro avec les potes",
                LocalDateTime.of(2017, 1, 1, 10, 0, 0),
                LocalDateTime.of(2017, 1, 1, 12, 0, 0),
                "2 des champs",
                team1))
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}