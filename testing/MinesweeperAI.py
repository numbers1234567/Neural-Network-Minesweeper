from keras.models import load_model
import numpy as np

"""
self.board is a 2D numpy array padded by a 3 space long border of just -3's at every side
"""
class MinesweeperAI:
    BORDER = -3
    FLAG = -2
    UNKNOWN = -1
    def __init__(self, start_board):
        self.model = load_model("../minesweeper_ai_model.h5")
        self.set_board(start_board)
        self.regional_check = (np.random.randint(0, len(start_board[0])-6), np.random.randint(0, len(start_board)-6))

    """
    Determines probabilities that each tile is a mine given tile_list: [(x1, y1), ...]

    Returns [P(x1, y1), P(x2, y2), ...] where P is a probability function
    """
    def get_next_probabilities(self, tile_list):
        probabilities = []
        for tile in tile_list:
            probabilities.append(self.model.predict(self.get_input_format(tile[0], tile[1])))

        return probabilities

    """
    Formats the input for the neural network
    """
    def get_input_format(self, x, y):
        x += 3 # Bypass border
        y += 3
        return self.board[y-3, y+4][x-3, x+4].flatten()

    def set_board(self, new_state):
