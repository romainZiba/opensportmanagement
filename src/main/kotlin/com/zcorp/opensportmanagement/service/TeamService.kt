package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.OpponentDto
import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.dto.SeasonDto
import com.zcorp.opensportmanagement.dto.TeamDto
import com.zcorp.opensportmanagement.dto.TeamMemberCreationDto
import com.zcorp.opensportmanagement.dto.TeamMemberDto
import com.zcorp.opensportmanagement.dto.TeamMemberUpdateDto
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.repository.OpponentRepository
import com.zcorp.opensportmanagement.repository.PlaceRepository
import com.zcorp.opensportmanagement.repository.SeasonRepository
import com.zcorp.opensportmanagement.repository.TeamMemberRepository
import com.zcorp.opensportmanagement.repository.TeamRepository
import com.zcorp.opensportmanagement.repository.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class TeamService @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val accountRepository: AccountRepository,
    private val seasonRepository: SeasonRepository,
    private val placeRepository: PlaceRepository,
    private val opponentRepository: OpponentRepository
) {
    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    @Transactional
    open fun getTeamMembers(teamId: Int): List<TeamMemberDto> {
        return teamRepository.getTeamMembers(teamId).map { it.toDto() }
    }

    @Transactional
    open fun getTeams(teamIds: List<Int>): List<TeamDto> {
        return teamRepository.findByIdIn(teamIds).map { it.toDto() }
    }

    @Transactional
    open fun getTeam(teamId: Int): TeamDto {
        return teamRepository.findById(teamId)
                .map { it.toDto() }
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
    }

    @Transactional
    open fun createTeam(teamDto: TeamDto, creatorUsername: String): TeamDto {
        val user = accountRepository.findByUsername(creatorUsername) ?: throw NotFoundException("Account $creatorUsername does not exist")
        var team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup, teamDto.imgUrl)
        team = teamRepository.save(team)
        val teamMember = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), team)
        user.addTeamMember(teamMember)
        accountRepository.save(user)
        return team.toDto()
    }

    @Transactional
    open fun deleteTeam(teamId: Int) {
        // TODO: should delete everything related to the team: members, championships...
        teamRepository.deleteById(teamId)
    }

    @Transactional
    open fun getPlaces(teamId: Int): List<PlaceDto> {
        return teamRepository.getPlaces(teamId).map { it.toDto() }
    }

    @Transactional
    open fun getSeasons(teamId: Int): List<SeasonDto> {
        return teamRepository.getSeasons(teamId).map { it.toDto() }
    }

    @Transactional
    open fun createSeason(seasonDto: SeasonDto, teamId: Int): SeasonDto {
        val team = teamRepository.findById(teamId)
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
        var season = Season(seasonDto.name, seasonDto.fromDate, seasonDto.toDate, seasonDto.status, team)
        season = seasonRepository.save(season)
        return season.toDto()
    }

    @Transactional
    open fun getEvents(teamId: Int, pageable: Pageable): Page<EventDto> {
        return teamRepository.getEvents(teamId, pageable).map { event -> event.toDto() }
    }

    @Transactional
    open fun getOpponents(teamId: Int): List<OpponentDto> {
        return teamRepository.getOpponents(teamId).map { it.toDto() }
    }

    @Transactional
    open fun createPlace(placeDto: PlaceDto, teamId: Int): PlaceDto {
        val team = teamRepository.findById(teamId)
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
        if (placeDto.name.isEmpty()) {
            throw BadParameterException("Name should not be empty")
        }
        if (placeDto.address.isEmpty()) {
            throw BadParameterException("Address should not be empty")
        }
        if (placeDto.city.isEmpty()) {
            throw BadParameterException("City should not be empty")
        }
        val place = Place(placeDto.name, placeDto.address, placeDto.city, placeDto.type, team)
        return placeRepository.save(place).toDto()
    }

    @Transactional
    open fun createOpponent(opponentDto: OpponentDto, teamId: Int): OpponentDto {
        val team = teamRepository.findById(teamId)
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
        val opponent = Opponent(opponentDto.name, opponentDto.phoneNumber, opponentDto.email, opponentDto.imgUrl, team)
        return opponentRepository.save(opponent).toDto()
    }

    @Transactional
    open fun getTeamMember(teamId: Int, memberId: Int): TeamMemberDto? {
        return teamRepository.getTeamMember(teamId, memberId)?.toDto()
    }

    @Transactional
    open fun getTeamMemberByUsername(teamId: Int, name: String): TeamMemberDto? {
        return teamRepository.getTeamMemberByUserName(teamId, name)?.toDto()
    }

    @Transactional
    open fun updateProfile(dto: TeamMemberUpdateDto, teamId: Int, name: String): TeamMemberDto {
        val teamMember = teamRepository.getTeamMemberByUserName(teamId, name) ?: throw NotFoundException("Team member $name does not exist")
        teamMember.licenseNumber = dto.licenseNumber
        return teamMemberRepository.save(teamMember).toDto()
    }

    @Transactional
    open fun createTeamMember(teamMemberDto: TeamMemberCreationDto, teamId: Int): TeamMemberDto {
        val team = teamRepository.findById(teamId).orElseThrow { NotFoundException("Team $teamId does not exist") }
        val firstName = teamMemberDto.firstName
        val lastName = teamMemberDto.lastName
        val userToSave = Account(firstName, lastName, bCryptPasswordEncoder.encode("password"),
                teamMemberDto.email, teamMemberDto.phoneNumber)
        val user = accountRepository.findByEmail(teamMemberDto.email) ?: accountRepository.save(userToSave)
        val teamMember = TeamMember(teamMemberDto.roles.toMutableSet(), team)
        user.addTeamMember(teamMember)
        val savedUser = accountRepository.save(user)
        return savedUser.getMemberOf().first { it.team.id == teamId }.toDto()
    }
}