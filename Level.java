import javax.swing.JOptionPane;
/**
 * Creates a custom dialog to change level.
 *
 * @author Turgut Guvercin
 * @version January 2022
 */
public class Level
{

    private String[] choices;
    private String input = null;

    /**
     * Constructor for objects of class Level
     */
    public Level()
    {

    }

    /*
     * Method to send the user's choice
     */
    public String getLevel(){
        String l = "screens/screen.";
        String[] choices = { "1", "2", "3", "4", "5", "6" };
        String input = (String) JOptionPane.showInputDialog(null, "Choose a Level",
                "Level", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]); 
        //System.out.println(input);
        if(input == null){
            return input;

        }
        return l+input;
    }
}
