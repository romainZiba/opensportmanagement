package com.zcorp.opensportmanagement.dto

data class AccountDto(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phoneNumber: String?
)

data class AccountUpdateDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?
)

data class AccountConfirmationDto(
    val confirmationId: String,
    val password: String
)