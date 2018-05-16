package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.*
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class TeamService @Autowired constructor(private val teamRepository: TeamRepository,
                                              private val teamMemberRepository: TeamMemberRepository,
                                              private val userRepository: UserRepository,
                                              private val seasonRepository: SeasonRepository,
                                              private val opponentRepository: OpponentRepository) {

    @Transactional
    open fun getTeamMembers(teamId: Int): List<TeamMemberDto> {
        return teamRepository.getTeamMembers(teamId).map { it.toDto() }
    }

    @Transactional
    open fun getTeams(teamIds: List<Int>): List<TeamDto> {
        return teamRepository.findByIds(teamIds).map { it.toDto() }
    }

    @Transactional
    open fun getTeam(teamId: Int): TeamDto {
        return teamRepository.getOne(teamId).toDto()
    }

    @Transactional
    open fun createTeam(teamDto: TeamDto, creatorUsername: String): TeamDto {
        val user = userRepository.findByUsername(creatorUsername) ?: throw NotFoundException("User $creatorUsername does not exist")
        var team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup, teamDto.imgUrl)
        team = teamRepository.save(team)
        val teamMember = TeamMember(mutableSetOf(TeamMember.Role.ADMIN), team)
        user.addTeamMember(teamMember)
        userRepository.save(user)
        return team.toDto()
    }

    @Transactional
    open fun deleteTeam(teamId: Int) {
        teamRepository.deleteById(teamId)
    }

    @Transactional
    open fun getStadiums(teamId: Int): List<StadiumDto> {
        return teamRepository.getStadiums(teamId).map { it.toDto() }
    }

    @Transactional
    open fun getSeasons(teamId: Int): List<SeasonDto> {
        return teamRepository.getSeasons(teamId).map { it.toDto() }
    }


    @Transactional
    open fun createSeason(seasonDto: SeasonDto, teamId: Int): SeasonDto {
        val team = teamRepository.getOne(teamId)
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
    open fun createOpponent(opponentDto: OpponentDto, teamId: Int): OpponentDto {
        val team = teamRepository.getOne(teamId)
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
}