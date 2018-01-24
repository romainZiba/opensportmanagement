package com.zcorp.opensportmanagement.security

val SECRET = "SecretKeyToGenJWTs"
val EXPIRATION_TIME: Long = 30000 // 30 seconds
val TOKEN_PREFIX = "Bearer "
val HEADER_STRING = "Authorization"
val SIGN_UP_URL = "/users/sign-up"