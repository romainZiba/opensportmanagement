package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.AccountConfirmationDto
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.service.AccountService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
class AccountControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var accountService: AccountService

    private val username = "foo"
    private val adminUsername = "admin"
    private val mockAccount = Account("To", "To", "password", "mail@foo", "")
    private val mockAdminAccount = Account(
            firstName = "To",
            lastName = "To",
            password = "password",
            email = "mail@admin",
            phoneNumber = "",
            temporary = false,
            globalAdmin = true
    )

    @Test
    @WithMockUser("foo")
    fun `Get account details when authenticated should return a response with status 'OK'`() {
        whenever(accountService.findByUsername(username)).thenReturn(mockAccount.toDto())
        this.mockMvc.perform(get("/accounts/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(mockAccount.toDto())))
                .andDo(document("get_account_info", responseFields(
                        fieldWithPath("username").description("The account's username."),
                        fieldWithPath("firstName").description("The account's first name."),
                        fieldWithPath("lastName").description("The account's last name."),
                        fieldWithPath("email").description("The account's email address."),
                        fieldWithPath("phoneNumber").description("The account's phone number."),
                        fieldWithPath("globalAdmin").description("Whether the account is admin.")
                )))
    }

    @Test
    fun `Get account details when unauthenticated should return a response with status 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/accounts/me")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Get account details when an user does not exist should return a response with status 'FORBIDDEN'`() {
        whenever(accountService.findByUsername(username)).thenReturn(null)
        this.mockMvc.perform(
                get("/accounts/me"))
                .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("admin")
    fun `Create a not existing account should return a response with status 'CREATED'`() {
        whenever(accountService.findByUsername(adminUsername)).thenReturn(mockAdminAccount.toDto())
        whenever(accountService.findByEmail(username)).thenReturn(null)
        whenever(accountService.createAccount(mockAccount)).thenReturn(mockAccount.toDto())
        this.mockMvc.perform(
                post("/accounts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(mockAccount)))
                .andExpect(status().isCreated)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(mockAccount.toDto())))
                .andDo(document("create_account", responseFields(
                        fieldWithPath("username").description("The account's username."),
                        fieldWithPath("firstName").description("The account's first name."),
                        fieldWithPath("lastName").description("The account's last name."),
                        fieldWithPath("email").description("The account's email address."),
                        fieldWithPath("phoneNumber").description("The account's phone number."),
                        fieldWithPath("globalAdmin").description("Whether the account is admin.")
                )))
    }

    @Test
    @WithMockUser("admin")
    fun `Create an existing account should return a response with status 'CONFLICT'`() {
        whenever(accountService.findByUsername(adminUsername)).thenReturn(mockAdminAccount.toDto())
        whenever(accountService.findByEmail(mockAccount.email)).thenReturn(mockAccount.toDto())
        this.mockMvc.perform(
                post("/accounts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(mockAccount)))
                .andExpect(status().isConflict)
    }

    @Test
    fun `Confirm account should work`() {
        whenever(accountService.confirmAccount(any())).thenReturn(mockAccount.toDto())
        val accountConfirmationDto = AccountConfirmationDto("id", "password")
        this.mockMvc.perform(
                put("/accounts/confirmation")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(accountConfirmationDto)))
                .andExpect(status().isOk)
                .andDo(document("confirm_account", responseFields(
                        fieldWithPath("username").description("The account's username."),
                        fieldWithPath("firstName").description("The account's first name."),
                        fieldWithPath("lastName").description("The account's last name."),
                        fieldWithPath("email").description("The account's email address."),
                        fieldWithPath("phoneNumber").description("The account's phone number."),
                        fieldWithPath("globalAdmin").description("Whether the account is admin.")
                )))
    }
}