import javax.imageio.ImageIO;
import java.awt.Image;
import javax.swing.*;

/**
 * This class represents the icons used in the GUI.
 *
 * @author Turgut Guvercin
 * @version January 2022
 */

public class Icons
{
    private Image wallBrick = null;
    private Image woodenBox  = null;
    private Image target  = null;
    private Image actor  = null;
    private Image empty  = null;
    private Image targetBox = null;
    private Image targetActor = null;

    /**
     * Constructor for reading the icons
     */
    public Icons() throws java.io.IOException
    {
        try
        { 
            wallBrick = ImageIO.read(getClass().getResource("icons/wallbrick.jpg"));
            woodenBox = ImageIO.read(getClass().getResource("icons/woodenbox.png"));
            target = ImageIO.read(getClass().getResource("icons/target.png"));
            actor = ImageIO.read(getClass().getResource("icons/actor.png"));
            empty = ImageIO.read(getClass().getResource("icons/white.png"));
            targetBox = ImageIO.read(getClass().getResource("icons/targetBox.png"));
            targetActor = ImageIO.read(getClass().getResource("icons/targetActor.png"));
        }
        catch (java.io.IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * Determines the cell icon in GUI according to Sokoban class.
     * 
     * @param imageDetermiener display as character from Sokoban class.
     */

    public ImageIcon getIcon(char imageDeterminer) {

        if(imageDeterminer == Sokoban.WALL)
            return (new ImageIcon(wallBrick));
        else if(imageDeterminer == Sokoban.BOX)
            return new ImageIcon(woodenBox);
        else if(imageDeterminer == Sokoban.TARGET)
            return new ImageIcon(target);
        else if(imageDeterminer == Sokoban.ACTOR)
            return new ImageIcon(actor);
        else if(imageDeterminer == Sokoban.EMPTY)
            return new ImageIcon(empty);  
        else if(imageDeterminer == Sokoban.TARGET_ACTOR)
            return new ImageIcon(targetActor); 
        else if(imageDeterminer == Sokoban.TARGET_BOX)
            return new ImageIcon(targetBox);
        else 
            throw new SokobanException("Related image could not found or invalid imageDeterminer.");

    }

}
