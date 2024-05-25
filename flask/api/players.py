from flask import Blueprint, request, jsonify
from flask_restful import Api, Resource
from model.player import Player

# Define the blueprint for players
player_api = Blueprint('player_api', __name__, url_prefix='/api/players')

# Initialize the API with the blueprint
api = Api(player_api)

class PlayerAPI:     
    class Action(Resource):
        def post(self):
            ''' Read data from JSON body '''
            body = request.get_json()
            
            ''' Avoid garbage in, error checking '''
            # Validate name
            name = body.get('name')
            if name is None or len(name) < 2:
                return {'message': 'Name is missing, or is less than 2 characters'}, 400
            
            # Validate uid
            uid = body.get('uid')
            if uid is None or len(uid) < 2:
                return {'message': 'User ID is missing, or is less than 2 characters'}, 400
            
            # Look for password and tokens
            password = body.get('password')
            tokens = body.get('tokens')

            ''' Key code block: setup PLAYER OBJECT '''
            player = Player(name=name, uid=uid, tokens=tokens)
            
            ''' Additional garbage error checking '''
            # Set password if provided
            if password is not None:
                player.set_password(password)
            
            ''' Key code block to add player to database '''
            created_player = player.create()
            
            # Success returns JSON of player
            if created_player:
                return jsonify(created_player.read())
            # Failure returns error
            return {'message': f'Processed {name}, either a format error or User ID {uid} is duplicate'}, 400

        def get(self):
            players = Player.query.all()  # Read/extract all players from database
            json_ready = [player.read() for player in players]  # Prepare output in JSON
            return jsonify(json_ready)  # jsonify creates Flask response object, more specific to APIs than json.dumps

        def put(self):
            body = request.get_json()  # Get the body of the request
            uid = body.get('uid')  # Get the UID
            data = body.get('data')
            player = Player.query.get(uid)  # Get the player (using the uid in this case)
            player.update(data)
            return jsonify(player.read())

        def delete(self):
            body = request.get_json()
            uid = body.get('uid')
            player = Player.query.get(uid)
            player.delete()
            return jsonify({'message': f'{player.read()["name"]} has been deleted'})

    # Add resource to the API
    api.add_resource(Action, '/')

