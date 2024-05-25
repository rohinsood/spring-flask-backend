class Player:
    def __init__(self, name, stats):
        self.name = name
        self.stats = stats

    def get_name(self):
        return self.name

    def set_name(self, name):
        self.name = name

    def get_stats(self):
        return self.stats

    def set_stats(self, stats):
        self.stats = stats
