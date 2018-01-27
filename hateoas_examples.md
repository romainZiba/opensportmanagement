{
    "_embedded": {
        "teams": [
            {
                "name": "MyTeam",
                "sport": "BASKETBALL",
                "genderKind": "BOTH",
                "ageGroup": "ADULTS",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/teams/1"
                    },
                    "team": {
                        "href": "http://localhost:8080/teams/1"
                    },
                    "seasons": {
                        "href": "http://localhost:8080/teams/1/seasons"
                    },
                    "opponents": {
                        "href": "http://localhost:8080/teams/1/opponents"
                    },
                    "events": {
                        "href": "http://localhost:8080/teams/1/events"
                    },
                    "stadiums": {
                        "href": "http://localhost:8080/teams/1/stadiums"
                    },
                    "members": {
                        "href": "http://localhost:8080/teams/1/members"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/teams"
        },
        "profile": {
            "href": "http://localhost:8080/profile/teams"
        },
        "search": {
            "href": "http://localhost:8080/teams/search"
        }
    }
}




{
    "name": "MyTeam",
    "sport": "BASKETBALL",
    "genderKind": "BOTH",
    "ageGroup": "ADULTS",
    "_links": {
        "self": {
            "href": "http://localhost:8080/teams/1"
        },
        "team": {
            "href": "http://localhost:8080/teams/1"
        },
        "seasons": {
            "href": "http://localhost:8080/teams/1/seasons"
        },
        "opponents": {
            "href": "http://localhost:8080/teams/1/opponents"
        },
        "events": {
            "href": "http://localhost:8080/teams/1/events"
        },
        "stadiums": {
            "href": "http://localhost:8080/teams/1/stadiums"
        },
        "members": {
            "href": "http://localhost:8080/teams/1/members"
        }
    }
}



{
    "_embedded": {
        "season": [
            {
                "name": "2017-2018",
                "fromDate": "2017-09-01",
                "toDate": "2018-07-31",
                "status": "CURRENT",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/season/1"
                    },
                    "season": {
                        "href": "http://localhost:8080/season/1"
                    },
                    "championships": {
                        "href": "http://localhost:8080/season/1/championships"
                    },
                    "team": {
                        "href": "http://localhost:8080/season/1/team"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/teams/1/seasons"
        }
    }
}



{
    "_embedded": {
        "championship": [
            {
                "name": "Championnat 2017-2018",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/championship/1"
                    },
                    "championship": {
                        "href": "http://localhost:8080/championship/1"
                    },
                    "season": {
                        "href": "http://localhost:8080/championship/1/season"
                    },
                    "matches": {
                        "href": "http://localhost:8080/championship/1/matches"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/season/1/championships"
        }
    }
}



{
    "_embedded": {
        "match": [
            {
                "name": "Match de championnat",
                "description": "Super match",
                "recurrent": false,
                "fromDateTime": "2018-01-01T10:00:00",
                "toDateTime": "2018-01-01T12:00:00",
                "reccurenceDays": [],
                "recurrenceFromDate": null,
                "recurrenceToDate": null,
                "recurrenceFromTime": null,
                "recurrenceToTime": null,
                "place": null,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/match/1"
                    },
                    "match": {
                        "href": "http://localhost:8080/match/1"
                    },
                    "stadium": {
                        "href": "http://localhost:8080/match/1/stadium"
                    },
                    "team": {
                        "href": "http://localhost:8080/match/1/team"
                    },
                    "opponent": {
                        "href": "http://localhost:8080/match/1/opponent"
                    },
                    "notPresentPlayers": {
                        "href": "http://localhost:8080/match/1/notPresentPlayers"
                    },
                    "championship": {
                        "href": "http://localhost:8080/match/1/championship"
                    },
                    "presentPlayers": {
                        "href": "http://localhost:8080/match/1/presentPlayers"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/championship/1/matches"
        }
    }
}