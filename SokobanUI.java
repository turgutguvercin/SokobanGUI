
import java.io.*;
import java.util.*;

/**
 * A text-based user interface for a Sokoban puzzle.
 * 
 * @author Dr Mark C. Sinclair
 * @version September 2021
 * 
 * @author Turgut Guvercin
 * @version January 2022
 */
public class SokobanUI {
    /**
     * Default constructor
     */
    public SokobanUI() {
        scnr   = new Scanner(System.in);
        puzzle = new Sokoban(new File(FILENAME));
        player = new RandomPlayer();
        recordMoves = new ArrayList<Direction>();
    }

    /**
     * Main control loop.  This displays the puzzle, then enters a loop displaying a menu,
     * getting the user command, executing the command, displaying the puzzle and checking
     * if further moves are possible
     */
    public void menu() throws FileNotFoundException {
        String command = "";
        System.out.print(puzzle);
        while (!command.equalsIgnoreCase("Quit") && !puzzle.onTarget())  {
            displayMenu();
            command = getCommand();
            execute(command);
            System.out.print(puzzle);
            if (puzzle.onTarget())
                System.out.println("puzzle is complete");
            trace("onTarget: "+puzzle.numOnTarget());
        }
    }

    /**
     * Display the user menu
     */
    private void displayMenu()  {
        System.out.println("Commands are:");
        System.out.println("   Move North         [N]");
        System.out.println("   Move South         [S]");
        System.out.println("   Move East          [E]");
        System.out.println("   Move West          [W]");
        System.out.println("   Player move        [P]");
        System.out.println("   Undo move          [U]");
        System.out.println("   Restart puzzle [Clear]");
        System.out.println("   Save to file    [Save]");
        System.out.println("   Load from file  [Load]");
        System.out.println("   To end program  [Quit]");    
    }

    /**
     * Get the user command
     * 
     * @return the user command string
     */
    private String getCommand() {
        System.out.print ("Enter command: ");
        return scnr.nextLine();
    }

    /**
     * Execute the user command string
     * 
     * @param command the user command string
     */
    private void execute(String command) throws FileNotFoundException {
        if (command.equalsIgnoreCase("Quit")) {
            System.out.println("Program closing down");
            System.exit(0);
        } else if (command.equalsIgnoreCase("N")) {
            north();
        } else if (command.equalsIgnoreCase("S")) {
            south();
        } else if (command.equalsIgnoreCase("E")) {
            east();
        } else if (command.equalsIgnoreCase("W")) {
            west();
        } else if (command.equalsIgnoreCase("P")) {
            playerMove();
        } else if (command.equalsIgnoreCase("U")) {
            undo();
        } else if (command.equalsIgnoreCase("Clear")) {
            clear();
        } else if (command.equalsIgnoreCase("Save")) {
            save(saveFile);
        } else if (command.equalsIgnoreCase("Load")) {
            load(saveFile);
        } else {
            System.out.println("Unknown command (" + command + ")");
        }
    }

    /**
     * Move the actor north
     */
    private void north() {
        move(Direction.NORTH);
    }

    /**
     * Move the actor south
     */
    private void south() {
        move(Direction.SOUTH);
    }

    /**
     * Move the actor east
     */
    private void east() {
        move(Direction.EAST);
    }

    /**
     * Move the actor west
     */
    private void west() {
        move(Direction.WEST);
    }

    /**
     * Undo the last user move
     */
    private void undo(){
        puzzle.clear();
        if(recordMoves.size() > 0)
        {
            recordMoves.remove(recordMoves.size()-1);
            for(Direction oneDirection:recordMoves)
            {
                puzzle.move(oneDirection);
            }
        }
    }

    /**
     * Reset the game
     */
    private void clear(){
        recordMoves.removeAll(recordMoves);
        puzzle = new Sokoban(new File(FILENAME));
    }

    /**
     * Save the game
     */
    private void save(String fileName) throws FileNotFoundException{

        PrintStream print = new PrintStream(new File(saveFile));
        for(Direction oneDirection: recordMoves){

            print.println(oneDirection);
        }

        System.out.println("The game has been saved successfully");
        print.close();

    }

    /**
     * Load the game from save file
     */
    private void load(String saveFile){
        Scanner      fscnr = null;
        StringBuffer sb    = new StringBuffer();
        recordMoves.removeAll(recordMoves); 
        puzzle = new Sokoban(new File(FILENAME));
        try{
            fscnr = new Scanner(new File(saveFile));

            while (fscnr.hasNextLine())
            {
                sb.append(fscnr.nextLine());
                Direction d = Direction.fromString(sb.toString());
                puzzle.move(d);
                recordMoves.add(d); 
                sb.setLength(0); // clears the buffer to add one by one

            }
        System.out.println("Game loaded from the file"); 
        }
        catch (IOException e) {
            System.out.println("an input output error occurred");
        }
        fscnr.close();
    }

    /**
     * Move the actor according to the computer player's choice
     */
    private void playerMove() {
        Vector<Direction> choices = puzzle.canMove();
        Direction         choice  = player.move(choices);
        move(choice);
    }  

    /**
     * If it is safe, move the actor to the next cell in a given direction
     * 
     * @param dir the direction to move
     */
    private void move(Direction dir) {
        if (!puzzle.canMove(dir)) {
            System.out.println("invalid move");
            return;
        }

        puzzle.move(dir);
        recordMoves.add(dir);
        if (puzzle.onTarget())
            System.out.println("game won!");
    }

    public static void main(String[] args) throws FileNotFoundException {

        SokobanUI ui = new SokobanUI();
        ui.menu();
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

    private Scanner scnr                        = null;
    private Sokoban puzzle                      = null;
    private Player  player                      = null;
    private ArrayList<Direction>  recordMoves   = null;
    private String  saveFile                    = "save.txt";
    private static String  FILENAME = "screens/screen.1";

    private static boolean   traceOn = false; // for debugging
}
