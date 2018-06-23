package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.MemberResponse
import org.springframework.data.jpa.repository.JpaRepository

interface MemberResponseRepository : JpaRepository<MemberResponse, Int>