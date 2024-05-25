from flask import Blueprint, request, jsonify
from flask_restful import Api, Resource

from model.game_teams_parser import GameTeamsParser
from model.monte_carlo_simulator import MonteCarloSimulator

# Change variable name and API name and prefix
monte_carlo_api = Blueprint('monte_carlo_api', __name__, url_prefix='/api/monte_carlo')

# API docs https://flask-restful.readthedocs.io/en/latest/api.html
api = Api(monte_carlo_api)

class MonteCarloAPI:     
    class Simulate(Resource):
        def post(self):
            ''' Read data from JSON body '''
            body = request.get_json()
            
            ''' Error checking for input data '''
            if not body:
                return {'message': 'No input data provided'}, 400

            ''' Instantiate simulator and parser '''
            simulator = MonteCarloSimulator()
            parser = GameTeamsParser()

            try:
                ''' Parse the JSON data into a GameTeams object '''
                game_teams = parser.parse(body)
                
                ''' Simulate the fantasy points '''
                result = simulator.simulate_fantasy_points(game_teams)
                return jsonify(result)
            except Exception as e:
                print(e)
                return {'message': 'Error processing the simulation'}, 500

    # Building REST API endpoint, method distinguishes action
    api.add_resource(Simulate, '/simulate')

# Register the blueprint in the main __init__.py
