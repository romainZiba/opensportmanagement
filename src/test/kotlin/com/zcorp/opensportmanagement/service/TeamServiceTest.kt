package com.zcorp.opensportmanagement.service

import assertk.assert
import assertk.assertAll
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.config.OsmProperties
import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Place.PlaceType
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repository.AccountRepository
import com.zcorp.opensportmanagement.repository.OpponentRepository
import com.zcorp.opensportmanagement.repository.PlaceRepository
import com.zcorp.opensportmanagement.repository.SeasonRepository
import com.zcorp.opensportmanagement.repository.TeamMemberRepository
import com.zcorp.opensportmanagement.repository.TeamRepository
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Optional

class TeamServiceTest {
    private val teamRepoMock: TeamRepository = mock()
    private val accountRepoMock: AccountRepository = mock()
    private val seasonRepoMock: SeasonRepository = mock()
    private val opponentRepoMock: OpponentRepository = mock()
    private val teamMemberRepoMock: TeamMemberRepository = mock()
    private val placeRepoMock: PlaceRepository = mock()
    private val emailServiceMock: EmailService = mock()
    private val osmPropertiesMock: OsmProperties = OsmProperties(listOf("pouet"))
    private val teamService: TeamService = TeamService(
            teamRepository = teamRepoMock,
            teamMemberRepository = teamMemberRepoMock,
            accountRepository = accountRepoMock,
            seasonRepository = seasonRepoMock,
            placeRepository = placeRepoMock,
            opponentRepository = opponentRepoMock,
            emailService = emailServiceMock,
            properties = osmPropertiesMock)
    private val teamIds = listOf(5, 8, 13, 25)
    private val mockTeams = teamIds.map {
        Team("SuperName $it", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", it)
    }
    private val mockPlaces = listOf(Place("Place", "", "", PlaceType.STADIUM, mockTeams[0], 1))

    @Test
    fun `get teams should return the teams`() {
        whenever(teamRepoMock.findByIdIn(teamIds)).thenReturn(mockTeams)
        val teams = teamService.getTeams(teamIds)
        assert(teams).containsExactly(
                mockTeams[0].toDto(),
                mockTeams[1].toDto(),
                mockTeams[2].toDto(),
                mockTeams[3].toDto()
        )
    }

    @Test
    fun `places should be empty`() {
        whenever(teamRepoMock.getPlaces(any())).thenReturn(emptyList())
        val places = teamService.getPlaces(teamIds[0])
        assert(places).isEmpty()
    }

    @Test
    fun `places should not be empty`() {
        whenever(teamRepoMock.getPlaces(any())).thenReturn(mockPlaces)
        val places = teamService.getPlaces(teamIds[0])
        assertAll {
            assert(places).isNotEmpty()
            assert(places).hasSize(1)
            assert(places[0]).isEqualTo(mockPlaces[0].toDto())
        }
    }

    @Test
    fun `create place should create it`() {
        whenever(teamRepoMock.findById(teamIds[0])).thenReturn(Optional.of(mockTeams[0]))
        whenever(placeRepoMock.save<Place>(any())).thenReturn(mockPlaces[0])
        val placeDto = PlaceDto("place", "address", "city", PlaceType.STADIUM)
        val savedPlace = teamService.createPlace(placeDto, teamIds[0])
        assert(savedPlace).isEqualTo(mockPlaces[0].toDto())
    }

    @Nested
    inner class ErrorCases {
        @Test
        fun `create place with a not existing team should fail`() {
            whenever(teamRepoMock.findById(teamIds[0])).thenReturn(Optional.empty())
            val placeDto = PlaceDto("place", "address", "city", PlaceType.STADIUM)
            assert {
                teamService.createPlace(placeDto, teamIds[0])
            }.thrownError { isInstanceOf(NotFoundException::class) }
        }

        @Test
        fun `create place with an empty name should fail`() {
            whenever(teamRepoMock.findById(teamIds[0])).thenReturn(Optional.of(mockTeams[0]))
            val placeDto = PlaceDto("", "address", "city", PlaceType.STADIUM)
            assert {
                teamService.createPlace(placeDto, teamIds[0])
            }.thrownError { isInstanceOf(BadParameterException::class) }
        }

        @Test
        fun `create place with an empty address should fail`() {
            whenever(teamRepoMock.findById(teamIds[0])).thenReturn(Optional.of(mockTeams[0]))
            val placeDto = PlaceDto("name", "", "city", PlaceType.STADIUM)
            assert {
                teamService.createPlace(placeDto, teamIds[0])
            }.thrownError { isInstanceOf(BadParameterException::class) }
        }

        @Test
        fun `create place with an empty city should fail`() {
            whenever(teamRepoMock.findById(teamIds[0])).thenReturn(Optional.of(mockTeams[0]))
            val placeDto = PlaceDto("name", "address", "", PlaceType.STADIUM)
            assert {
                teamService.createPlace(placeDto, teamIds[0])
            }.thrownError { isInstanceOf(BadParameterException::class) }
        }
    }
}