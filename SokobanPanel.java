import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * A graphical based user interface for a Sokoban puzzle.
 *
 * @author Turgut Guvercin
 * @version January 2022
 */

public class SokobanPanel extends JPanel implements Observer, ActionListener 
{

    private PanelCell[][] cells         = null;
    private JPanel        grid          = null;
    private Sokoban       puzzle        = null;
    private JButton       clear         = null;
    private JButton       undo          = null;
    private JButton       save          = null;
    private JButton       load          = null;
    private JButton       exit          = null;
    private JButton       section       = null;
    private JTextArea     status        = null;
    private Level level;
    private ArrayList<Direction>  recordMoves       = null;
    private static String  FILENAME                 = "screens/screen.1";
    private static String  SAVEFILE                 = "saveGUI.txt";
    private static String  SAVEFILESCREEN           = "saveGUIscreen.txt";
    private static JFrame frame;
    private static boolean   traceOn                = false; // for debugging

    /**
     * Create a Sokoban GUI from a standard Sokoban screen file 
     * 
     * Also, clear, undo, save and loads buttons, and a status area for feedback
     */
    public SokobanPanel()
    {   

        puzzle = new Sokoban(new File(FILENAME));
        puzzle.addObserver(this);
        recordMoves = new ArrayList<Direction>();

        // set up main puzzle grid
        grid  = new JPanel(new GridLayout(puzzle.getNumRows(),puzzle.getNumCols()));
        cells = new PanelCell[puzzle.getNumRows()+1][puzzle.getNumCols()+1];
        for (int row=0; row<puzzle.getNumRows(); row++) {
            for (int col=0; col<puzzle.getNumCols(); col++) {
                Cell cellTemp = puzzle.getCell(row,col);
                char puzzleImage = cellTemp.getDisplay();
                cells[row][col] = new PanelCell(this, row, col, puzzleImage);
                grid.add(cells[row][col]);

            }
        }

        setLayout(new BorderLayout());
        add(grid, BorderLayout.NORTH);
        JPanel center = new JPanel(new GridLayout(1,5));
        clear = new JButton("Clear");
        clear.addActionListener(this);
        clear.setFocusable(false); // After pressing the buttons to keep focusing on grid
        undo = new JButton("Undo");
        undo.addActionListener(this);
        undo.setFocusable(false);
        save = new JButton("Save");
        save.addActionListener(this);
        save.setFocusable(false);
        load = new JButton("Load");
        load.addActionListener(this);
        load.setFocusable(false);
        exit = new JButton("Exit");
        exit.addActionListener(this);
        exit.setFocusable(false);
        section = new JButton("Level");
        section.addActionListener(this);
        section.setFocusable(false);
        center.add(clear);
        center.add(undo);
        center.add(save);
        center.add(load);
        center.add(section);
        center.add(exit);
        level = new Level();
        add(center, BorderLayout.CENTER);
        status = new JTextArea();
        add(new JScrollPane(status), BorderLayout.SOUTH);
        status.setFocusable(false);
    }

    /**
     * Move the actor to the next cell in a given direction
     * 
     * @param dir the direction to move
     */
    public void makeMove(Direction dir){
        if (dir == null)
            throw new SokobanException("dir cannot be null");
        Vector<Direction> availableMoves = puzzle.canMove();
        trace("availableMoves: " + availableMoves);
        if(!availableMoves.contains(dir)){
            setStatus("invalid user move");
            return;
        }
        puzzle.move(dir);
        recordMoves.add(dir); // It saves the user moves for undo function
        onTarget();
    }

    /**
     * Updates the PanelCells when the underlying model cells are assigned
     * 
     * @param o the observable
     * @param arg the cell that was assigned
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg == null)
            throw new SokobanException("arg is null");
        Cell c = (Cell) arg;
        cells[c.getRow()][c.getCol()].addIcon(c.getDisplay());
    }

    /**
     * Action event: handle the button presses (clear, undo, save, load and exit)
     * 
     * @param ae the ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == clear)
            clear();
        else if (ae.getSource() == undo)
            undo();
        else if (ae.getSource() == save)
            save();
        else if (ae.getSource() == load)
            load();
        else if (ae.getSource() == exit)
            exit();
        else if (ae.getSource() == section)
            level();
    }

    private void level(){

        String getFileName = level.getLevel();
        if(getFileName != null)
            FILENAME = getFileName;
        else
            return;
        recordMoves.removeAll(recordMoves);
        frame.dispose();
        frame = new JFrame("Sokoban");
        SokobanPanel panel = new SokobanPanel();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Close the game from the GUI
     */
    private void exit(){
        int input = JOptionPane.showConfirmDialog(null, 
                "Are you sure?", "Exit",JOptionPane.YES_NO_OPTION);

        if (input == JOptionPane.YES_OPTION)
            frame.dispose();
    }

    /**
     * Reset the game
     */
    private void clear(){
        recordMoves.removeAll(recordMoves);
        puzzle = new Sokoban(new File(FILENAME));
        updatePanel();
        puzzle.addObserver(this);
        setStatus("game has been reset");
    }

    /**
     * Undo the last user move
     */
    private void undo(){
        puzzle.clear();
        if(recordMoves.size() > 0)
        {
            recordMoves.remove(recordMoves.size()-1); // clear the last move
            trace("recordMoves: " + recordMoves);
            for(Direction oneDirection:recordMoves)
            {
                puzzle.move(oneDirection);
            }
        }
    }

    /**
     * Load the game from save file and checks whether is correct save file or not.
     */
    void load() {
        Scanner fscnr = null;
        StringBuffer sb = new StringBuffer();
        int row = puzzle.getNumRows();
        int col = puzzle.getNumCols();
        Sokoban saveFileTest = new Sokoban(new File(SAVEFILESCREEN));
        int rowS = saveFileTest.getNumRows();
        int colS = saveFileTest.getNumCols();
        if(row == rowS && col == colS)
        {
            puzzle = new Sokoban(new File(FILENAME));
            recordMoves.removeAll(recordMoves);
            puzzle.addObserver(this);
        }
        else
        {
            setStatus("the saved file is not suitable for this screen/section");
            return;
        }
        try {

            fscnr = new Scanner(new File(SAVEFILE));
            while (fscnr.hasNextLine())
            {
                sb.append(fscnr.nextLine());
                trace(sb.toString());
                Direction d = Direction.fromString(sb.toString());
                puzzle.move(d);
                recordMoves.add(d); 
                sb.setLength(0); // clears the buffer to add one by one

            }
        } catch(IOException e) {
            setStatus("an i/o error occurred");
        } finally {
            if (fscnr != null)
                fscnr.close();
        }
        fscnr.close();
        trace("row: " + row + "col: " + col + "rowS: " + rowS + "cols: " + colS);
        updatePanel();
        setStatus("game loaded from file");

    }

    /**
     * Save the game
     */
    private void save() {

        try{
            PrintStream printScreen = new PrintStream(new File(SAVEFILESCREEN));
            printScreen.println(puzzle.toString());
            printScreen.close();
            PrintStream print = new PrintStream(new File(SAVEFILE));
            for(Direction oneDirection: recordMoves){

                print.println(oneDirection);
            }

            setStatus ("The game has been saved successfully");
            print.close();

        }
        catch(IOException e){
            setStatus("an i/o error occurred");
        }

    }

    /**
     * Sets the status bar to a given string
     * 
     * @param s the new status
     */
    void setStatus(String s) {
        status.setText(s);
    }

    /**
     * Update the GUI when screen changed
     */
    private void updatePanel()
    {
        for (int row=0; row<puzzle.getNumRows(); row++) {
            for (int col=0; col<puzzle.getNumCols(); col++) {
                Cell cellTemp = puzzle.getCell(row,col);
                char puzzleImage = cellTemp.getDisplay();
                cells[row][col].addIcon(puzzleImage); 
            }
        }
    }

    /**
     * Provides status feedback whether all boxes on target or not.
     */
    public void onTarget()
    {
        if(puzzle.onTarget())
            setStatus("All boxes on target. Congratulations!");
        else
            setStatus("Target Achieved:" + puzzle.numBoxes() + "/" + (puzzle.numOnTarget()));
    }

    /**
     * A trace method for debugging (active when traceOn is true)
     * 
     * @param s the string to output
     */
    public static void trace(String s) {
        if (traceOn)
            System.out.println("trace: " + s);
    }

    /*
     * Main function to run SokobanPanel
     */
    public static void main(String[] args){
        frame = new JFrame("Sokoban");
        SokobanPanel panel = new SokobanPanel();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
