import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JFrame;


/*
Contains graphics and UI for the Minesweeper.

Specialized version of Minesweeper for the purpose of gathering
 proper data for a neural net.

It will display a minesweeper board. You select multiple squares, 
 and then when you press q, it reveals those squares and does 
 the iconic minesweeper flood fill
*/
public class MinesweeperDataGathering extends JFrame implements MouseListener, KeyListener {
    // Interface to the lower-level minesweeper game
    private MinesweeperInterface inter;
    // false means not selected.
    private boolean[][] selectedSquares;
    // Interface to the Minesweeper game data
    public MinesweeperInterface.MinesweeperGame visibleGame;

    private boolean caching = true;

    public static final int windowWidth=600;
    public static final int windowHeight=600;

    public static void main(String[] args) {
        MinesweeperDataGathering dataUI = new MinesweeperDataGathering();
        dataUI.repaint();
    }

    MinesweeperDataGathering() {
        super("Data collection");

        inter = new MinesweeperInterface();
        visibleGame = inter.new MinesweeperGame();
        selectedSquares = new boolean[inter.getHeight()][inter.getWidth()];

        getContentPane().add(this.new BoardPainter());
        addMouseListener(this);
        addKeyListener(this);

        setSize(windowWidth, windowHeight);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); 
    }

    // Toggle a square
    public void select(int x, int y) {
        selectedSquares[y][x] = !selectedSquares[y][x];
        repaint();
    }

    /*
    Reveal all selected blocks. Return True if good and return False if mine selected
    */

    public boolean revealSelected() {
        int[][] minesBoard = inter.getBoard();
        for (int y=0; y<selectedSquares.length; y++) 
            for (int x=0; x<selectedSquares[0].length; x++) 
                if (minesBoard[y][x] == -1 && selectedSquares[y][x]) return false;

        for (int y=0; y<selectedSquares.length; y++) 
            for (int x=0; x<selectedSquares[0].length; x++) 
                if (selectedSquares[y][x]) visibleGame.click(x, y);
        
        selectedSquares = new boolean[inter.getHeight()][inter.getWidth()];
        return true;
    }

    /*
    MOUSE LISTENING EVENTS
    */
    public void mousePressed(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY()-23;
        int tileX = x*inter.getWidth()/windowWidth;
        int tileY = y*inter.getHeight()/(windowHeight-23);
        System.out.println(tileX + " " + tileY);
        select(tileX, tileY);
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    /*
    KEYBOARD EVENTS
    */
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'q') {
            // Click everywhere
            revealSelected();
            repaint();
            if (visibleGame.isSolved()) 
                // Dispatch window close event
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        }
    }
    public void keyTyped(KeyEvent e) {}

    /*
    HELPER PAINTER CLASS
    */
    private class BoardPainter extends JPanel {
        /*
        Paint number at tile x,y with a visible board from a MinesweeperGame.
        */
        public void tilePaintText(Graphics g, int x, int y, int[][] visBoard) {
            int gameHeight = visibleGame.getHeight();
            int gameWidth = visibleGame.getWidth();
            int windowHeight = MinesweeperDataGathering.windowHeight - 23;

            float xCoordIncrement = ((float)windowWidth)/gameWidth;
            float yCoordIncrement = ((float)windowHeight)/gameHeight;

            if (visBoard[y][x] > 0) {
                g.setFont(new Font("Serif", Font.BOLD, 25));
                g.drawString("" +visBoard[y][x], (int)(xCoordIncrement*x + windowWidth/(2*gameWidth)-5),
                                                 (int)(yCoordIncrement*y + (windowHeight-23)/(2*gameHeight) + 5));
                g.setFont(new Font("Serif", Font.PLAIN, 20));
            }
            else if (visBoard[y][x] == -1) {
                g.drawString("X", (int)(xCoordIncrement*x + windowWidth/(2*gameWidth)),
                                  (int)(yCoordIncrement*y + windowHeight/(2*gameHeight)));
            }
        }

        public void paintGridLines(Graphics g) {
            // 16x16 or 49x49 is what is referred to in gameHeight/gameWidth
            int gameHeight = visibleGame.getHeight();
            int gameWidth = visibleGame.getWidth();
            int windowHeight = MinesweeperDataGathering.windowHeight - 23;
            // Draw gridlines
            for (int i=0; i<gameHeight; i++) {
                int yCoord = i*windowHeight/gameHeight;
                g.drawLine(0, yCoord, windowWidth, yCoord);
            }
            for (int i=0; i<gameWidth; i++) {
                int xCoord = i*windowWidth/gameWidth;
                g.drawLine(xCoord, 0, xCoord, windowHeight);
            }
        }

        /*
        Paint entire board
        */
        public void paintBoard(Graphics g) {
            int[][] visBoard = visibleGame.getPlayerBoard();
            // 16x16 or 49x49 is what is referred to in gameHeight/gameWidth
            int gameHeight = visibleGame.getHeight();
            int gameWidth = visibleGame.getWidth();
            int windowHeight = MinesweeperDataGathering.windowHeight - 23;
            // Draw gridlines
            paintGridLines(g);

            // Draw numbers and stuff
            float xCoordIncrement = ((float)windowWidth)/gameWidth;
            float yCoordIncrement = ((float)windowHeight)/gameHeight;
            g.setFont(new Font("Serif", Font.PLAIN, 20));
            // Tile increment
            for (int y=0; y<gameHeight; y++) {
                for (int x=0; x<gameWidth; x++) {
                    // Draw selected square
                    if (selectedSquares[y][x]) 
                        g.drawOval((int)(xCoordIncrement*x + 0.1*xCoordIncrement),
                                   (int)(yCoordIncrement*y + 0.1*yCoordIncrement),
                                   (int)(0.8*xCoordIncrement), (int)(0.8*yCoordIncrement));
                    
                    // Draw number
                    else tilePaintText(g, x, y, visBoard);
                }
            }
        }

        /*
        General paint function
        */
        public void paint(Graphics g) {
            paintBoard(g);
        }
    }
}
