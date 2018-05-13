package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.config.OsmProperties
import com.zcorp.opensportmanagement.dto.*
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.service.*
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.time.*

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(OsmProperties::class)
open class Application {

    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Transactional
    open fun init(userService: UserService,
                  eventService: EventService,
                  teamService: TeamService,
                  stadiumService: StadiumService,
                  seasonService: SeasonService,
                  matchService: MatchService,
                  messagingService: MessagingService) = CommandLineRunner {


        var adminTeam1And2 = User("CR", "Coach", "Rock", "CR",
                "CR@caramail.com", "")
        var userTeam1 = User("PW", "Player", "Wow", "PW",
                "PW@caramail.com", "")
        var userTeam2 = User("bbb", "Bobb", "Bobbybob",
                "bbb", "bbb@caramail.com",
                "")

        userService.createUser(adminTeam1And2)
        userService.createUser(userTeam1)
        userService.createUser(userTeam2)

        var team1Dto = TeamDto("TEAM 1", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "https://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=san-antonio-spurs&width=500&height=500")
        team1Dto = teamService.createTeam(team1Dto, adminTeam1And2.username)

        var team2Dto = TeamDto("TEAM 2", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "https://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=san-antonio-spurs&width=140&height=140")
        team2Dto = teamService.createTeam(team2Dto, adminTeam1And2.username)

        userService.joinTeam(userTeam1.username, team1Dto._id!!)
        userService.joinTeam(userTeam2.username, team2Dto._id!!)

        var stadiumDto = StadiumDto("LE stade", "2 allée", "Toulouse")
        stadiumDto = stadiumService.createStadium(stadiumDto, team1Dto._id!!)

        var opponentDto = OpponentDto("TCMS2",
                "0159756563",
                "testmail@gmail.com",
                "https://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=houston-rockets")
        opponentDto = teamService.createOpponent(opponentDto, team1Dto._id!!)

        var seasonDto = SeasonDto("2017-2018",
                LocalDate.of(2017, Month.SEPTEMBER, 1),
                LocalDate.of(2018, Month.JULY, 31),
                Season.Status.CURRENT)
        seasonDto = teamService.createSeason(seasonDto, team1Dto._id!!)

        var championshipDto = ChampionshipDto("Championnat 2017-2018")
        championshipDto = seasonService.createChampionship(championshipDto, seasonDto._id!!)


        (0..4L).forEach({
            val fromDateTime = LocalDateTime.of(LocalDate.now().plusDays(it), LocalTime.of(20, 0))
            val matchCreationDto = MatchCreationDto("Match de championnat",
                    fromDateTime, fromDateTime.plusHours(2L), null, stadiumDto._id,
                    Match.MatchType.CHAMPIONSHIP, championshipDto._id, opponentDto._id, true)

            matchService.createMatch(team1Dto._id!!, matchCreationDto)
        })
        var lastDto: EventDto? = null
        (1..2L).forEach({
            val fromDateTime = LocalDateTime.of(LocalDate.now().minusDays(it), LocalTime.of(20, 0))

            val matchCreationDto = MatchCreationDto("Match de championnat",
                    fromDateTime, fromDateTime.plusHours(2L), null, stadiumDto._id,
                    Match.MatchType.CHAMPIONSHIP, championshipDto._id, opponentDto._id, true)
            lastDto = matchService.createMatch(team1Dto._id!!, matchCreationDto)
        })

        val fromDate = LocalDate.of(2018, 1, 5) // It's a friday
        val toDate = LocalDate.of(2018, 3, 31)
        val fromTime = LocalTime.of(10, 0)
        val toTime = LocalTime.of(11, 0)
        val dto = EventCreationDto("event", null, null, null, stadiumDto._id!!, true,
                mutableSetOf(DayOfWeek.WEDNESDAY, DayOfWeek.TUESDAY), fromTime, toTime, fromDate, toDate)
        eventService.createEvent(team1Dto._id!!, dto)
        messagingService.createMessage(MessageDto("Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... Pour cet évènement blabla... "), "CR", lastDto?._id)
        messagingService.createMessage(MessageDto("Ca roule"), "PW", lastDto?._id)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}