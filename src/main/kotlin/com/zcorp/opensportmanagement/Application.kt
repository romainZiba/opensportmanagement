package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import com.zcorp.opensportmanagement.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

@SpringBootApplication
@EnableAsync
open class Application {

    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Transactional
    open fun init(userService: UserService,
                  teamRepository: TeamRepository,
                  stadiumRepository: StadiumRepository,
                  eventRepository: EventRepository,
                  matchRepository: MatchRepository,
                  opponentRepository: OpponentRepository,
                  seasonRepository: SeasonRepository,
                  championshipRepository: ChampionshipRepository,
                  userRepository: UserRepository,
                  teamMemberRepository: TeamMemberRepository) = CommandLineRunner {

        var team1 = Team("TEAM 1", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS)
        team1.imgUrl = "http://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=san-antonio-spurs&width=500&height=500"
        team1 = teamRepository.save(team1)

        var team2 = Team("TEAM 2", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS)
        team2.imgUrl = "http://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=san-antonio-spurs&width=140&height=140"
        team2 = teamRepository.save(team2)

        var userTeam1_2 = User("CR", "Coach", "Rock", bCryptPasswordEncoder().encode("CR"),
                "CR@caramail.com", "")
        var userTeam1 = User("PW", "Player", "Wow", bCryptPasswordEncoder().encode("PW"),
                "PW@caramail.com", "")
        var userTeam2 = User("bbb", "Bobb", "Bobbybob",
                bCryptPasswordEncoder().encode("bbb"), "bbb@caramail.com",
                "")
        userTeam1_2 = userRepository.save(userTeam1_2)
        userTeam1 = userRepository.save(userTeam1)
        userTeam2 = userRepository.save(userTeam2)

        userService.joinTeam(userTeam1_2.username, team1.id)
        userService.joinTeam(userTeam1_2.username, team2.id)
        userService.joinTeam(userTeam1.username, team1.id)
        userService.joinTeam(userTeam2.username, team2.id)

        val stadium = stadiumRepository.save(Stadium("LE stade", "2 allée", "Toulouse", team1))

        var opponent = Opponent("TCMS2",
                "0159756563",
                "testmail@gmail.com",
                "http://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=houston-rockets",
                team1)
        opponent = opponentRepository.save(opponent)

        val season = seasonRepository.save(Season("2017-2018",
                LocalDate.of(2017, Month.SEPTEMBER, 1),
                LocalDate.of(2018, Month.JULY, 31),
                Season.Status.CURRENT,
                team1
        ))
        val championship = championshipRepository.save(Championship("Championnat 2017-2018", season))
        val match = matchRepository.save(Match("Match de championnat", "Super match",
                LocalDateTime.of(2018, 1, 1, 10, 0, 0),
                LocalDateTime.of(2018, 1, 1, 12, 0, 0),
                stadium, opponent, team1, championship))
        val event = eventRepository.save(Event(
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