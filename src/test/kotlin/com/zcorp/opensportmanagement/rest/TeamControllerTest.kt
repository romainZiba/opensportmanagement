package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.dto.TeamDto
import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.model.Place.PlaceType
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.BadParameterException
import com.zcorp.opensportmanagement.service.EventService
import com.zcorp.opensportmanagement.service.TeamService
import com.zcorp.opensportmanagement.service.AccountService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
class TeamControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var teamServiceMock: TeamService
    @MockBean
    private lateinit var accountServiceMock: AccountService
    @MockBean
    private lateinit var eventServiceMock: EventService
    @MockBean
    private lateinit var accessController: AccessController

    private val teamId = 1
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)

    private val mockPlacesDto = listOf(PlaceDto("name", "address", "city", PlaceType.STADIUM, teamId, 1))
    private val mockGlobalAdmin = Account("foo", "foo", "password", "foo",
            "0", false, true)

    @Test
    fun `Get teams when unauthenticated should return FORBIDDEN`() {
        this.mockMvc.perform(get("/teams")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Get teams when authenticated should return OK`() {
        whenever(accessController.getUserTeamIds(any())).thenReturn(listOf(teamId))
        whenever(teamServiceMock.getTeams(listOf(teamId))).thenReturn(listOf(mockTeam).map { it.toDto() })
        this.mockMvc.perform(get("/teams")).andExpect(status().isOk)
    }

    @Test
    fun `Create team when unauthenticated should return FORBIDDEN`() {
        val teamDto = TeamDto("The team", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "")
        this.mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(teamDto)))
                .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Create team when authenticated should return CREATED`() {
        val teamDto = TeamDto("The team", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "")
        val savedTeam = TeamDto("The team", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", 2)
        whenever(accountServiceMock.findByUsername(any())).thenReturn(mockGlobalAdmin.toDto())
        whenever(teamServiceMock.createTeam(any(), any())).thenReturn(savedTeam)
        val teamMembers = setOf(TeamMember(mutableSetOf(TeamMember.Role.ADMIN), mockTeam, "AAA", 1))
        whenever(accountServiceMock.getTeamsAndRoles(any())).thenReturn(teamMembers)
        this.mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(teamDto)))
                .andExpect(status().isCreated)
                .andExpect(content().json(jacksonObjectMapper().writeValueAsString(savedTeam)))
    }

    @Test
    fun `GET places when unauthenticated should return a response with status 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/teams/1/places")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `GET places when authenticated should return a response with status 'OK'`() {
        whenever(accessController.isAccountAllowedToAccessTeam(any(), any())).thenReturn(true)
        whenever(teamServiceMock.getPlaces(any())).thenReturn(mockPlacesDto)
        this.mockMvc.perform(
                get("/teams/1/places"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(mockPlacesDto)))
    }

    @Test
    @WithMockUser("foo")
    fun `Get opponents when authenticated should return a response with status 'OK'`() {
        whenever(accessController.isAccountAllowedToAccessTeam(any(), any())).thenReturn(true)
        this.mockMvc.perform(get("/teams/1/opponents"))
                .andExpect(status().isOk)
    }

    @Test
    @WithMockUser("foo")
    fun `Create event with an empty name should return a response with status 'BAD REQUEST'`() {
        whenever(accessController.isAccountAllowedToAccessTeam(any(), any())).thenReturn(true)
        whenever(eventServiceMock.createEvent(any(), any())).thenThrow(BadParameterException(""))
        val eventCreationDto = EventCreationDto(
                fromDate = LocalDate.of(2050, 1, 1),
                toDate = LocalDate.of(2050, 2, 1),
                fromTime = LocalTime.of(16, 0),
                toTime = LocalTime.of(18, 0),
                placeId = 1,
                type = AbstractEvent.EventType.OTHER)
        this.mockMvc.perform(
                post("/teams/1/events")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(eventCreationDto)))
                .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser("foo")
    fun `Create place should return a response with status 'CREATED'`() {
        val dto = mockPlacesDto[0]
        val placeDto = PlaceDto(dto.name, dto.address, dto.city, PlaceType.STADIUM)
        whenever(accessController.isTeamAdmin(any(), any())).thenReturn(true)
        whenever(teamServiceMock.createPlace(any(), any())).thenReturn(dto)
        this.mockMvc.perform(
                post("/teams/1/places")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(placeDto)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(dto)))
                .andDo(MockMvcRestDocumentation.document("create_place", PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("name").description("The name of the place"),
                        PayloadDocumentation.fieldWithPath("address").description("The address of the place"),
                        PayloadDocumentation.fieldWithPath("city").description("The city of the place"),
                        PayloadDocumentation.fieldWithPath("type").description("The type of the place"),
                        PayloadDocumentation.fieldWithPath("_id").description("The identifier of the place")
                )))
    }
}