import random
from .player import Player

class MonteCarloSimulator:
    def simulate_fantasy_points(self, teams_data):
        standard_deviation = self.calculate_standard_deviation(teams_data)
        mean_fantasy_points = self.calculate_mean_fantasy_points(teams_data)

        number_of_simulations = 20
        projected_fantasy_pointsA = []
        projected_fantasy_pointsB = []

        for _ in range(number_of_simulations):
            fantasy_points = self.simulate_fantasy_points_distribution(mean_fantasy_points, standard_deviation)
            projected_fantasy_pointsA.append(fantasy_points[0])
            projected_fantasy_pointsB.append(fantasy_points[1])

        average_projectionA = self.calculate_average(projected_fantasy_pointsA)
        average_projectionB = self.calculate_average(projected_fantasy_pointsB)

        response = {
            "projectedFantasyPointsTeamA": [average_projectionA],
            "projectedFantasyPointsTeamB": [average_projectionB]
        }
        return response

    def calculate_standard_deviation(self, teams_data):
        sum_ = 0
        total_players = 0
        all_players = teams_data.get_teamA() + teams_data.get_teamB()

        for player in all_players:
            sum_ += self.calculate_fantasy_points(player)
            total_players += 1

        mean = sum_ / total_players
        squared_difference_sum = sum((self.calculate_fantasy_points(player) - mean) ** 2 for player in all_players)
        variance = squared_difference_sum / total_players
        return variance ** 0.5

    def calculate_fantasy_points(self, player):
        stats = player.get_stats()
        return stats.get_points() * 1.0 + stats.get_rebounds() * 1.2 + stats.get_assists() * 1.5

    def simulate_fantasy_points_distribution(self, mean, standard_deviation):
        return [random.gauss(mean, standard_deviation), random.gauss(mean, standard_deviation)]

    def calculate_mean_fantasy_points(self, teams_data):
        sum_ = 0
        total_players = 0
        all_players = teams_data.get_teamA() + teams_data.get_teamB()

        for player in all_players:
            sum_ += self.calculate_fantasy_points(player)
            total_players += 1

        return sum_ / total_players

    def calculate_average(self, values):
        return sum(values) / len(values)
