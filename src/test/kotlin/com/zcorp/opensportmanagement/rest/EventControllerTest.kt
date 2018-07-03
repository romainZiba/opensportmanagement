package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.EventModificationDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.EventService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
class EventControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var eventServiceMock: EventService
    @MockBean
    private lateinit var accessController: AccessController

    @Test
    @WithMockUser("foo")
    fun `Update event should return a response with status 'OK'`() {
        val fromDateTime = LocalDateTime.of(2018, 1, 1, 1, 0)
        val toDateTime = LocalDateTime.of(2018, 1, 1, 2, 0)
        val placeId = 1
        val eventId = 58
        val teamId = 10
        val modificationDto = EventModificationDto(
                fromDate = fromDateTime.toLocalDate(),
                toDate = toDateTime.toLocalDate(),
                fromTime = fromDateTime.toLocalTime(),
                toTime = toDateTime.toLocalTime(),
                placeId = placeId
        )
        val eventDto = EventDto(
                _id = eventId,
                name = "event",
                fromDateTime = fromDateTime.minusDays(1),
                toDateTime = toDateTime.minusDays(1),
                placeId = placeId,
                presentMembers = listOf(),
                absentMembers = listOf(),
                waitingMembers = listOf(),
                teamId = teamId,
                cancelled = false,
                openForRegistration = true
        )
        val updatedEventDto = EventDto(
                _id = eventId,
                name = "event",
                fromDateTime = fromDateTime,
                toDateTime = toDateTime,
                placeId = placeId,
                presentMembers = listOf(),
                absentMembers = listOf(),
                waitingMembers = listOf(),
                cancelled = false,
                openForRegistration = true
        )
        whenever(accessController.isTeamAdmin(any(), any())).thenReturn(true)
        whenever(eventServiceMock.getEvent(eventId)).thenReturn(eventDto)
        whenever(eventServiceMock.updateEvent(any(), any(), any())).thenReturn(updatedEventDto)
        this.mockMvc.perform(
                put("/events/$eventId")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(modificationDto)))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json("{\n" +
                                "  \"_id\" : $eventId,\n" +
                                "  \"name\" : \"event\",\n" +
                                "  \"fromDateTime\" : \"2018-01-01T01:00:00\",\n" +
                                "  \"toDateTime\" : \"2018-01-01T02:00:00\",\n" +
                                "  \"placeId\" : $placeId,\n" +
                                "  \"presentMembers\" : [ ],\n" +
                                "  \"absentMembers\" : [ ],\n" +
                                "  \"waitingMembers\" : [ ],\n" +
                                "  \"localTeamName\" : null,\n" +
                                "  \"visitorTeamName\" : null,\n" +
                                "  \"localTeamImgUrl\" : null,\n" +
                                "  \"visitorTeamImgUrl\" : null,\n" +
                                "  \"visitorTeamScore\" : null,\n" +
                                "  \"localTeamScore\" : null,\n" +
                                "  \"done\" : null,\n" +
                                "  \"openForRegistration\" : true,\n" +
                                "  \"cancelled\" : false\n" +
                                "}", true))
                .andDo(MockMvcRestDocumentation.document("update_event", PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("_id")
                                .description("The identifier of the event"),
                        PayloadDocumentation.fieldWithPath("name")
                                .description("The name of the event"),
                        PayloadDocumentation.fieldWithPath("fromDateTime")
                                .description("The date when the event starts"),
                        PayloadDocumentation.fieldWithPath("toDateTime")
                                .description("The date when the event ends"),
                        PayloadDocumentation.fieldWithPath("placeId")
                                .description("The identifier of the place where the event takes place"),
                        PayloadDocumentation.fieldWithPath("presentMembers")
                                .description("The members who are present"),
                        PayloadDocumentation.fieldWithPath("absentMembers")
                                .description("The members who are absent"),
                        PayloadDocumentation.fieldWithPath("waitingMembers")
                                .description("The members who are in waiting list"),
                        PayloadDocumentation.fieldWithPath("localTeamName")
                                .description("The name of the local team"),
                        PayloadDocumentation.fieldWithPath("visitorTeamName")
                                .description("The name of the team which visits"),
                        PayloadDocumentation.fieldWithPath("localTeamImgUrl")
                                .description("The url of the image of the local team"),
                        PayloadDocumentation.fieldWithPath("visitorTeamImgUrl")
                                .description("The url of the image of the team which visits"),
                        PayloadDocumentation.fieldWithPath("localTeamScore")
                                .description("The score of the local team"),
                        PayloadDocumentation.fieldWithPath("visitorTeamScore")
                                .description("The score of the team which visits"),
                        PayloadDocumentation.fieldWithPath("done")
                                .description("Whether or not the match has been done"),
                        PayloadDocumentation.fieldWithPath("openForRegistration")
                                .description("Event is open for registration or not"),
                        PayloadDocumentation.fieldWithPath("cancelled")
                                .description("Event is cancelled or not"))))
    }

    @Test
    @WithMockUser("foo")
    fun `Cancel event should return a response with status 'OK'`() {
        val fromDateTime = LocalDateTime.of(2018, 1, 1, 1, 0)
        val toDateTime = LocalDateTime.of(2018, 1, 1, 2, 0)
        val placeId = 1
        val eventId = 58
        val teamId = 10
        val eventDto = EventDto(
                _id = eventId,
                name = "event",
                fromDateTime = fromDateTime,
                toDateTime = toDateTime,
                placeId = placeId,
                presentMembers = listOf(),
                absentMembers = listOf(),
                waitingMembers = listOf(),
                teamId = teamId,
                cancelled = false,
                openForRegistration = true
        )
        val cancelledDto = EventDto(
                _id = eventId,
                name = "event",
                fromDateTime = fromDateTime,
                toDateTime = toDateTime,
                placeId = placeId,
                presentMembers = listOf(),
                absentMembers = listOf(),
                waitingMembers = listOf(),
                teamId = teamId,
                cancelled = true,
                openForRegistration = true
        )
        whenever(accessController.isTeamAdmin(any(), any())).thenReturn(true)
        whenever(eventServiceMock.getEvent(eventId)).thenReturn(eventDto)
        whenever(eventServiceMock.cancelEvent(any(), any())).thenReturn(cancelledDto)
        this.mockMvc.perform(
                put("/events/$eventId/cancelled")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json("{\n" +
                                "  \"_id\" : $eventId,\n" +
                                "  \"name\" : \"event\",\n" +
                                "  \"fromDateTime\" : \"2018-01-01T01:00:00\",\n" +
                                "  \"toDateTime\" : \"2018-01-01T02:00:00\",\n" +
                                "  \"placeId\" : $placeId,\n" +
                                "  \"presentMembers\" : [ ],\n" +
                                "  \"absentMembers\" : [ ],\n" +
                                "  \"waitingMembers\" : [ ],\n" +
                                "  \"localTeamName\" : null,\n" +
                                "  \"visitorTeamName\" : null,\n" +
                                "  \"localTeamImgUrl\" : null,\n" +
                                "  \"visitorTeamImgUrl\" : null,\n" +
                                "  \"visitorTeamScore\" : null,\n" +
                                "  \"localTeamScore\" : null,\n" +
                                "  \"openForRegistration\" : true,\n" +
                                "  \"done\" : null,\n" +
                                "  \"cancelled\" : true\n" +
                                "}", true))
                .andDo(MockMvcRestDocumentation.document("update_event", PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("_id")
                                .description("The identifier of the event"),
                        PayloadDocumentation.fieldWithPath("name")
                                .description("The name of the event"),
                        PayloadDocumentation.fieldWithPath("fromDateTime")
                                .description("The date when the event starts"),
                        PayloadDocumentation.fieldWithPath("toDateTime")
                                .description("The date when the event ends"),
                        PayloadDocumentation.fieldWithPath("placeId")
                                .description("The identifier of the place where the event takes place"),
                        PayloadDocumentation.fieldWithPath("presentMembers")
                                .description("The members who are present"),
                        PayloadDocumentation.fieldWithPath("absentMembers")
                                .description("The members who are absent"),
                        PayloadDocumentation.fieldWithPath("waitingMembers")
                                .description("The members who are in waiting list"),
                        PayloadDocumentation.fieldWithPath("localTeamName")
                                .description("The name of the local team"),
                        PayloadDocumentation.fieldWithPath("visitorTeamName")
                                .description("The name of the team which visits"),
                        PayloadDocumentation.fieldWithPath("localTeamImgUrl")
                                .description("The url of the image of the local team"),
                        PayloadDocumentation.fieldWithPath("visitorTeamImgUrl")
                                .description("The url of the image of the team which visits"),
                        PayloadDocumentation.fieldWithPath("localTeamScore")
                                .description("The score of the local team"),
                        PayloadDocumentation.fieldWithPath("visitorTeamScore")
                                .description("The score of the team which visits"),
                        PayloadDocumentation.fieldWithPath("done")
                                .description("Whether or not the match has been done"),
                        PayloadDocumentation.fieldWithPath("openForRegistration")
                                .description("Event is open for registration or not"),
                        PayloadDocumentation.fieldWithPath("cancelled")
                                .description("Event is cancelled or not"))))
    }
}