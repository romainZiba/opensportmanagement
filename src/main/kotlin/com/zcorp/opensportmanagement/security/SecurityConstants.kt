package com.zcorp.opensportmanagement.security

val SECRET = "SecretKeyToGenJWTs"
val EXPIRATION_TIME: Long = 6000000 // 1000 minutes in milliseconds
val TOKEN_PREFIX = "Bearer "
val HEADER_STRING = "Authorization"
val SIGN_UP_URL = "/users/sign-up"
val AUTHORITIES = "authorities"
val ADMIN = "adminUser"