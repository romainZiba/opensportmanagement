package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.config.OsmProperties
// import com.zcorp.opensportmanagement.dto.ChampionshipDto
// import com.zcorp.opensportmanagement.dto.OpponentDto
// import com.zcorp.opensportmanagement.dto.PlaceDto
// import com.zcorp.opensportmanagement.dto.SeasonDto
// import com.zcorp.opensportmanagement.dto.TeamDto
//    import com.zcorp.opensportmanagement.model.Season
// import com.zcorp.opensportmanagement.model.Team
// import com.zcorp.opensportmanagement.model.User
// import com.zcorp.opensportmanagement.service.EventService
// import com.zcorp.opensportmanagement.service.MatchService
// import com.zcorp.opensportmanagement.service.PlaceService
// import com.zcorp.opensportmanagement.service.SeasonService
// import com.zcorp.opensportmanagement.service.TeamService
// import com.zcorp.opensportmanagement.service.UserService
// import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
// import java.time.LocalDate
// import java.time.Month
// import javax.transaction.Transactional

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(OsmProperties::class)
open class Application {

    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

//    @Bean
//    @Transactional
//    open fun init(
//            userService: UserService,
//            eventService: EventService,
//            teamService: TeamService,
//            placeService: PlaceService,
//            seasonService: SeasonService,
//            matchService: MatchService
//    ) = CommandLineRunner {
//
//        var adminTeam1And2 = User("CR", "Coach", "Rock", "CR",
//                "CR@caramail.com", "")
//        var userTeam1 = User("PW", "Player", "Wow", "PW",
//                "PW@caramail.com", "")
//        var userTeam2 = User("bbb", "Bobb", "Bobbybob",
//                "bbb", "bbb@caramail.com",
//                "")
//
//        userService.createUser(adminTeam1And2)
//        userService.createUser(userTeam1)
//        userService.createUser(userTeam2)
//
//        var team1Dto = TeamDto("TEAM 1", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "https://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=san-antonio-spurs&width=500&height=500")
//        team1Dto = teamService.createTeam(team1Dto, adminTeam1And2.username)
//
//        var team2Dto = TeamDto("TEAM 2", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "https://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=san-antonio-spurs&width=140&height=140")
//        team2Dto = teamService.createTeam(team2Dto, adminTeam1And2.username)
//
//        userService.joinTeam(userTeam1.username, team1Dto._id!!)
//        userService.joinTeam(userTeam2.username, team2Dto._id!!)
//
//        var placeDto = PlaceDto("LE stade", "2 allée", "Toulouse")
//        placeService.createPlace(placeDto, team1Dto._id!!)
//
//        var opponentDto = OpponentDto("TCMS2",
//                "0159756563",
//                "testmail@gmail.com",
//                "https://tsnimages.tsn.ca/ImageProvider/TeamLogo?seoId=houston-rockets")
//        teamService.createOpponent(opponentDto, team1Dto._id!!)
//
//        var seasonDto = SeasonDto("2017-2018",
//                LocalDate.of(2017, Month.SEPTEMBER, 1),
//                LocalDate.of(2018, Month.JULY, 31),
//                Season.Status.CURRENT)
//        seasonDto = teamService.createSeason(seasonDto, team1Dto._id!!)
//
//        var championshipDto = ChampionshipDto("Championnat 2017-2018")
//        seasonService.createChampionship(championshipDto, seasonDto._id!!)
//    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}