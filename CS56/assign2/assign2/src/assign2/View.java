package assign2;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 *
 * @author Steven Cabral
 * @author Austin Smith
 * Code skeleton by Pete Cappello
 */
public class View extends JPanel {
    final static int IMAGE_SIZE = 800;
    private Image image;
     
    @Override
    public void paintComponent( Graphics graphics )
    {
        super.paintComponent( graphics );
        graphics.drawImage( image, 0, 0, IMAGE_SIZE, IMAGE_SIZE, this );
    }
     
     // Declare a setter method for the Image attribute, to be invoked by the Controller.
    public void setImage(Image image){
    	this.image = image;
    }
} 