import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.event.*;

/**
 * A graphical representation of a cell in Sokoban game.
 *
 * @author Turgut Guvercin
 * @version January 2022
 */
public class PanelCell extends JButton
{
    private SokobanPanel panel;
    private Icons icon;
    private int row;
    private int column;

    /**
     * Constructor for key listeners and determines a cell
     * 
     * @param p the panel object from SokobanPanel class
     * @param r the row in the game
     * @param c the column in the game
     * @param image determiner in Sokoban class
     */
    public PanelCell(SokobanPanel p, int r, int c, char imageDeterminer) 
    {   
        if (p == null)
            throw new SokobanException("cannot have null panel");
        if ((imageDeterminer != Sokoban.WALL) && (imageDeterminer != Sokoban.BOX) && (imageDeterminer != Sokoban.ACTOR) && (imageDeterminer != Sokoban.TARGET) && (imageDeterminer != Sokoban.EMPTY) && (imageDeterminer != Sokoban.TARGET_BOX) && (imageDeterminer != Sokoban.TARGET_ACTOR))
            throw new SokobanException("invalid char");
        if ((r<0))
            throw new SokobanException("invalid row (" + r + ")");
        if ((c<0))
            throw new SokobanException("invalid col (" + c + ")");
        try{
            icon = new Icons();
            icon.getIcon(Sokoban.ACTOR);
        }
        catch (java.io.IOException ioe)
        {
            ioe.printStackTrace();
        }
        panel = p;
        row = r;
        column = c;
        addIcon(imageDeterminer);
        setHorizontalAlignment(CENTER);
        setForeground(Color.black);
        setPreferredSize(new Dimension(50,50));
        setBackground(Color.WHITE);
        //setMaximumSize(new Dimension(50,50));
        setOpaque(true);

        addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    
                    if (e.getKeyCode() == KeyEvent.VK_UP)
                        panel.makeMove(Direction.NORTH);

                    else if(e.getKeyCode() == KeyEvent.VK_DOWN)
                        panel.makeMove(Direction.SOUTH);

                    else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                        panel.makeMove(Direction.EAST);

                    else if(e.getKeyCode() == KeyEvent.VK_LEFT)
                        panel.makeMove(Direction.WEST);

                    else
                        panel.setStatus("Please Use Arrow Keys");
                }});

    }

    /**
     * Determines the cell icon in GUI according to Sokoban class.
     * 
     * @param imageDetermiener display as character from Sokoban class.
     */

    public void addIcon(char imageDeterminer)
    {   
        setIcon(icon.getIcon(imageDeterminer));
    }

}
