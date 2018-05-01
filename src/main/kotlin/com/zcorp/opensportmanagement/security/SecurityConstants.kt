package com.zcorp.opensportmanagement.security

const val SECRET = "SecretKeyToGenJWTs"
const val EXPIRATION_TIME: Long = 7 * 24 * 60 * 60 * 1000 // 7 days in milliseconds
const val TOKEN_PREFIX = "Bearer "
const val COOKIE_KEY = "access_token"
const val SIGN_UP_URL = "/users/sign-up"
const val AUTHORITIES = "authorities"
const val REFRESH_TOKEN_RATE = 12 // in hours
const val URL_ENCODING = "UTF-8"
