class GameTeams:
    def __init__(self, teamA=None, teamB=None):
        self.teamA = teamA or []
        self.teamB = teamB or []

    def get_teamA(self):
        return self.teamA

    def set_teamA(self, teamA):
        self.teamA = teamA

    def get_teamB(self):
        return self.teamB

    def set_teamB(self, teamB):
        self.teamB = teamB
