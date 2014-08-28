package pkg;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Display for the Zapem game model
 * @author Steven Cabral
 * @author Austin Smith
 * @author Pete Cappello
 */

public class View extends JPanel {
    private final Image image;
    private final Game game; 
    
    /**
     * 
     * @param game Game object
     * @param image java.awt.Image object
     */
    public View(Game game, Image image){
    	this.game = game;
    	this.image = image;
    }
    		
    @Override
    public void paintComponent( Graphics graphics )
    {
        super.paintComponent(graphics);
        graphics.drawImage( image, 0, 0, game.IMAGE_SIZE, game.IMAGE_SIZE, this );
    }
    
} 

