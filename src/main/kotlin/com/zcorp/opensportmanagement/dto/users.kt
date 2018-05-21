package com.zcorp.opensportmanagement.dto

data class UserDto(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val phoneNumber: String?
)

data class UserUpdateDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?
)