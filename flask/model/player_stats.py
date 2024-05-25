class PlayerStats:
    def __init__(self, points, rebounds, assists):
        self.points = points
        self.rebounds = rebounds
        self.assists = assists

    def get_points(self):
        return self.points

    def set_points(self, points):
        self.points = points

    def get_rebounds(self):
        return self.rebounds

    def set_rebounds(self, rebounds):
        self.rebounds = rebounds

    def get_assists(self):
        return self.assists

    def set_assists(self, assists):
        self.assists = assists
