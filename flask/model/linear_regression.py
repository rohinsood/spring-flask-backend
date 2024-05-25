import numpy as np
from sklearn.linear_model import LinearRegression
from .player import Player

class LinearRegressionSimulator:
    def simulate_fantasy_points(self, teams_data):
        # Prepare the data for linear regression
        X, y = self.prepare_data(teams_data)

        # Train the linear regression model
        model = LinearRegression()
        model.fit(X, y)

        # Predict the fantasy points
        projected_fantasy_pointsA = model.predict(self.get_team_features(teams_data.get_teamA()))
        projected_fantasy_pointsB = model.predict(self.get_team_features(teams_data.get_teamB()))

        # Calculate the average projection
        average_projectionA = self.calculate_average(projected_fantasy_pointsA)
        average_projectionB = self.calculate_average(projected_fantasy_pointsB)

        response = {
            "projectedFantasyPointsTeamA": [average_projectionA],
            "projectedFantasyPointsTeamB": [average_projectionB]
        }
        return response

    def prepare_data(self, teams_data):
        X = []
        y = []
        all_players = teams_data.get_teamA() + teams_data.get_teamB()

        for player in all_players:
            stats = player.get_stats()
            features = [stats.get_points(), stats.get_rebounds(), stats.get_assists()]
            X.append(features)
            y.append(self.calculate_fantasy_points(player))

        return np.array(X), np.array(y)

    def get_team_features(self, team):
        features = []
        for player in team:
            stats = player.get_stats()
            features.append([stats.get_points(), stats.get_rebounds(), stats.get_assists()])
        return np.array(features)

    def calculate_fantasy_points(self, player):
        stats = player.get_stats()
        return stats.get_points() * 1.0 + stats.get_rebounds() * 1.2 + stats.get_assists() * 1.5

    def calculate_average(self, values):
        return np.mean(values)
