package com.zcorp.opensportmanagement.security

const val SECRET = "SecretKeyToGenJWTs"
const val EXPIRATION_TIME: Int = 7 * 24 * 60 * 60 // 7 days in seconds
const val TOKEN_PREFIX = "Bearer "
const val ACCESS_TOKEN_COOKIE_KEY = "access_token"
const val INSECURE_COOKIE_KEY = "open_session"
const val SIGN_UP_URL = "/users"
const val AUTHORITIES = "authorities"
const val REFRESH_TOKEN_RATE = 12 // in hours
const val URL_ENCODING = "UTF-8"
