package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.service.AccountService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
class AccountControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountService: AccountService

    private val username = "toto"
    private val user = Account("toto", "To", "To", "password", "", "")

    @Test
    @WithMockUser("toto")
    fun `GET on 'users-me' when authenticated should return a response with status 'OK'`() {
        whenever(accountService.findByUsername(username)).thenReturn(user.toDto())
        this.mockMvc.perform(get("/users/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(user.toDto())))
                .andDo(document("get_user_info", responseFields(
                        fieldWithPath("username").description("The account's username."),
                        fieldWithPath("firstName").description("The account's first name."),
                        fieldWithPath("lastName").description("The account's last name."),
                        fieldWithPath("email").description("The account's email address."),
                        fieldWithPath("phoneNumber").description("The account's phone number.")
                )))
    }

    @Test
    fun `GET on 'users-me' when unauthenticated should return a response with status 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/users/me")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("toto")
    fun `GET on 'users-me' when an user does not exist should return a response with status 'FORBIDDEN'`() {
        whenever(accountService.findByUsername(username)).thenReturn(null)
        this.mockMvc.perform(
                get("/users/me"))
                .andExpect(status().isForbidden)
    }

    @Test
    fun `Create a not existing user should return a response with status 'CREATED'`() {
        whenever(accountService.findByUsername(username)).thenReturn(null)
        whenever(accountService.createUser(user)).thenReturn(user.toDto())
        this.mockMvc.perform(
                post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(user)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(user.toDto())))
                .andDo(document("create_user", responseFields(
                        fieldWithPath("username").description("The account's username."),
                        fieldWithPath("firstName").description("The account's first name."),
                        fieldWithPath("lastName").description("The account's last name."),
                        fieldWithPath("email").description("The account's email address."),
                        fieldWithPath("phoneNumber").description("The account's phone number.")
                )))
    }
    @Test
    fun `Create an existing user should return a response with status 'CONFLICT'`() {
        whenever(accountService.findByUsername(username)).thenReturn(user.toDto())
        this.mockMvc.perform(
                post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(user)))
                .andExpect(status().isConflict)
    }
}