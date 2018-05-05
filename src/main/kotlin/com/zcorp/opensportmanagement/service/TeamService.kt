package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.*
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class TeamService {

    companion object {
        const val MAX_RESULTS = 20
    }

    @Autowired
    lateinit var teamRepository: TeamRepository
    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var seasonRepository: SeasonRepository
    @Autowired
    lateinit var eventRepository: EventRepository
    @Autowired
    lateinit var stadiumRepository: StadiumRepository
    @Autowired
    lateinit var opponentRepository: OpponentRepository

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
        val user = userRepository.findByUsername(creatorUsername) ?: throw UserNotFoundException()
        var team = Team(teamDto.name, teamDto.sport, teamDto.genderKind, teamDto.ageGroup)
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

    fun createEvent(eventDto: EventDto, teamId: Int): EventDto {
        val team = teamRepository.getOne(teamId)
        val event = createEventFromDto(eventDto, team)
        return eventRepository.save(event).toDto()
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

    private fun createEventFromDto(eventDto: EventDto, team: Team): Event {
        val place = eventDto.place
        val optionalStadium = stadiumRepository.findById(eventDto.stadiumId)
        if (place == null && !optionalStadium.isPresent) {
            throw Exception("Either valid stadium identifier or place must be provided")
        }
        val recurrenceDays = eventDto.reccurenceDays
        val fromDateTime = eventDto.fromDate
        val toDateTime = eventDto.toDate
        val recurrenceFromTime = eventDto.recurrenceFromTime
        val recurrenceToTime = eventDto.recurrenceToTime
        if ((recurrenceDays == null || recurrenceFromTime == null || recurrenceToTime == null)
                && (fromDateTime == null || toDateTime == null)) {
            throw Exception("Either recurrence or fixed event information must be provided")
        }

        return if (eventDto.reccurenceDays == null) {
            if (optionalStadium.isPresent) {
                Event(eventDto.name, eventDto.description, fromDateTime!!, toDateTime!!, optionalStadium.get(), team)
            } else {
                Event(eventDto.name, eventDto.description, fromDateTime!!, toDateTime!!, place!!, team)
            }
        } else {
            if (optionalStadium.isPresent) {
                Event(eventDto.name, eventDto.description, eventDto.reccurenceDays!!, eventDto.recurrenceFromDate!!,
                        eventDto.recurrenceToDate!!, recurrenceFromTime!!, recurrenceToTime!!, optionalStadium.get(), team)
            } else {
                Event(eventDto.name, eventDto.description, eventDto.reccurenceDays!!, eventDto.recurrenceFromDate!!,
                        eventDto.recurrenceToDate!!, recurrenceFromTime!!, recurrenceToTime!!, place!!, team)
            }
        }
    }
}