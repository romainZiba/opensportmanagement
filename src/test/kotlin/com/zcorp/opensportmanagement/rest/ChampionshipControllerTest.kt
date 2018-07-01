package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.ChampionshipMatchCreationDto
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.ChampionshipService
import com.zcorp.opensportmanagement.service.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
class ChampionshipControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var championshipServiceMock: ChampionshipService

    @MockBean
    private lateinit var accessControllerMock: AccessController

    private val championshipId = 6
    private val teamId = 5
    private val seasonId = 58
    private val mockTeam = Team("Team", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS,
            "", teamId)
    private val mockSeason = Season("Season", LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 9), mockTeam, seasonId)
    private val mockChampionship = Championship("Championship", mockSeason, championshipId)

    @Test
    fun `Get championship when unauthenticated should return response code 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/championships/$championshipId")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Get championship when authenticated should return response code 'OK'`() {
        whenever(championshipServiceMock.getChampionship(any())).thenReturn(mockChampionship.toDto())
        whenever(accessControllerMock.isAccountAllowedToAccessTeam(any(), any())).thenReturn(true)
        this.mockMvc.perform(get("/championships/$championshipId"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content()
                        .json(jacksonObjectMapper().writeValueAsString(mockChampionship.toDto())))
    }

    @Test
    fun `Create match when unauthenticated should return response code 'FORBIDDEN'`() {
        this.mockMvc.perform(post("/championships/$championshipId/matches")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Create match when championship does not exist should return response code 'NOT FOUND'`() {
        val matchCreationDto = ChampionshipMatchCreationDto(
                fromDate = LocalDate.of(2025, 1, 1),
                toDate = LocalDate.of(2025, 1, 1),
                fromTime = LocalTime.of(10, 0),
                toTime = LocalTime.of(12, 0),
                placeId = 2,
                matchType = Match.MatchType.CHAMPIONSHIP,
                championshipId = championshipId,
                opponentId = 15,
                isTeamLocal = true)
        whenever(championshipServiceMock.getChampionship(any())).thenThrow(NotFoundException(""))
        this.mockMvc.perform(
                post("/championships/$championshipId/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(matchCreationDto)))
                .andExpect(status().isNotFound)
    }
}