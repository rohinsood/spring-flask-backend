import json
from .game_teams import GameTeams
from .player import Player
from .player_stats import PlayerStats

class GameTeamsParser:
    def parse(self, json_data):
        data = json.loads(json_data)
        teamA = [Player(player['name'], PlayerStats(**player['stats'])) for player in data.get('teamA', [])]
        teamB = [Player(player['name'], PlayerStats(**player['stats'])) for player in data.get('teamB', [])]
        return GameTeams(teamA, teamB)
