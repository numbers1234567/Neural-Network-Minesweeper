import java.lang.Math;
import java.util.Queue;
import java.util.LinkedList;

/*
A basic interface to a minesweeper board.

The board member is represented as a 2-dimensional array. -1 means a mine, 
 nonnegative integer means the number of mines around it.

The MinesweeperGame contains all the information available to the player
 in a normal game of Minesweeper.
*/
public class MinesweeperInterface {
    private int[][] board;
    private int width;
    private int height;
    private int numMines;

    /* 
    Generates a Minesweeper board randomly with the given
     number of mines and the dimensions
    */
    public MinesweeperInterface(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.numMines = mines;

        board = new int[height][width];
        int minesAdded = 0;

        int[][] minePos = new int[mines][2];

        while (minesAdded < mines) {
            int posX = (int)(Math.random()*width);
            int posY = (int)(Math.random()*height);
            if (board[posY][posX] == -1) continue;
            board[posY][posX] = -1;
            int[] pos = {posX, posY};
            minePos[minesAdded] = pos;
            minesAdded++;
        }
        
        // Set up surrounding mine numbers
        for (int[] pos: minePos) {
            // Increment through every possible surrounding index
            for (int x=pos[0]-1; x<pos[0]+2; x++) {
                for (int y=pos[1]-1; y<pos[1]+2; y++) {
                    // A simple way to filter out invalid indices
                    try {
                        if (board[y][x] != -1) board[y][x]++;

                    } catch (IndexOutOfBoundsException e) {;}
                }
            }
        }

    }

    // Basic getters
    public int getHeight() {return height;}
    public int getWidth() {return width;}
    public int getNumMines() {return numMines;}

    public int[][] getBoard() {
        int[][] newBoard = new int[board.length][];
        for (int i=0; i<board.length; i++) newBoard[i] = board[i].clone();
        return newBoard;
    }

    /*
    Default 16x16 with 40 mines.
    */
    public MinesweeperInterface() {
        this(16, 16, 40);
    }

    /*
    An interface to the minesweeper game, where clicking operations can be performed.

    possibleMoves is a 2-dimensional array, 0 if not opened and 1 if opened.
     If opened, it is possible to perform the clicking operation on it.

    Also where the data for computer vision is taken.
    */
    public class MinesweeperGame {
        private int[][] possibleMoves;
        // A sort of mosaic of the possibleMoves and board members. It represents what is visible to the player during a game, -1 being unknown.
        private int[][] visibleBoard;

        /* 
        Constructor for a minesweeper game, taking in
         a list of the positions of all clicked boxes
        */
        public MinesweeperGame(int[][] clicked) {
            possibleMoves = new int[board.length][board[0].length];
            visibleBoard = new int[board.length][board[0].length];
            for (int[] pos: clicked) click(pos[0], pos[1]);
            for (int[] row : visibleBoard) 
                for (int i=0; i<board[0].length; i++) row[i] = -1;
        }

        public MinesweeperGame() {
            possibleMoves = new int[board.length][board[0].length];
            visibleBoard = new int[board.length][board[0].length];
            for (int[] row : visibleBoard) 
                for (int i=0; i<board[0].length; i++) row[i] = -1;
        }
        
        /*
        Returns false if that click was a losing click.

        Uses a floodfill to update possibleMoves.
        */
        public boolean click(int posX, int posY) {
            // Slash out base cases, dependent only on the click made.
            // Now we no longer check for -1's.
            if (possibleMoves[posY][posX] == 1) return true;
            if (board[posY][posX] == -1)        return false;
            
            Queue<int[]> floodFillQueue = new LinkedList<int[]>();
            
            int[] pos0 = {posX, posY};
            floodFillQueue.add(pos0);
            
            while (floodFillQueue.size() > 0) {
                int[] flooding = floodFillQueue.poll();
                // Just an easy way to filter out invalid indices
                try {
                    board[flooding[1]][flooding[0]] += 0;
                } catch (IndexOutOfBoundsException e) {continue;}
                
                if (board[flooding[1]][flooding[0]] == 0 && 
                    possibleMoves[flooding[1]][flooding[0]] == 0) 
                    // Flood adjacents next
                    for (int y=flooding[1]-1; y<flooding[1]+2; y++) {for (int x=flooding[0]-1; x<flooding[0]+2; x++){
                        int pos[] = {x, y};
                        floodFillQueue.add(pos);
                    }}
             
                // Flood this now
                possibleMoves[flooding[1]][flooding[0]] = 1;
                // Update visible board
                visibleBoard[flooding[1]][flooding[0]] = board[flooding[1]][flooding[0]];
            }
            
            return true;
        }

        /*
        Returns a copy of the visibleBoard
        */
        public int[][] getPlayerBoard() {
            int[][] newVis = new int[board.length][];
            for (int i=0; i<board.length; i++) newVis[i] = visibleBoard[i].clone();
            return newVis;
        }
        
        // Basic getters
        public int getHeight() {return height;}
        public int getWidth() {return width;}
        public int getNumMines() {return numMines;}

        /*
        What can I say? Returns true if the board is solved, false otherwise
        */
        public boolean isSolved() {
            for (int y=0; y<board.length; y++)
                for (int x=0; x<board.length; x++)
                    // If there is an unopened box that doesn't contain a mine
                    if (board[y][x] != -1 && visibleBoard[y][x] == -1) return false;

            return true;
        }
    }
    
}