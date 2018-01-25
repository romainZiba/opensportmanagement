package com.zcorp.opensportmanagement.security

val SECRET = "SecretKeyToGenJWTs"
val EXPIRATION_TIME: Long = 3000000 // milliseconds
val TOKEN_PREFIX = "Bearer "
val HEADER_STRING = "Authorization"
val SIGN_UP_URL = "/users/sign-up"
val TEAMS = "teams"
val ADMIN = "adminUser"