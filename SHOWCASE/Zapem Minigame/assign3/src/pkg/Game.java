package pkg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

/**
* Zapem game model
*
*@author Austin Smith
*@author Steven Cabral
*@author Peter Cappello
*/
public class Game 
{

    /**
     *
     */
    public final static int IMAGE_SIZE = 600;
 private final Image image;
 private final Critter critterFactory;
 private List<Critter> critterList;
 private boolean isOver;
 public boolean lastClick = false; 
 
    /**
     *
     * @return Returns image
     */
    public Image getImage()
 {
    return this.image;
 }

    /**
     *
     * @return Returns state of game
     */
    public boolean getIsOver()
 {
    return this.isOver;
 }
 
 
 Game() 
 {         
     // * construct the Image object, of size IMAGE_SIZE
     image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
     Graphics graphics = image.getGraphics();
	 
     // * paint the image all white for its initial display
     graphics.setColor(new Color(255,255,255));
     graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);	 
	 
     // * instantiate critterFactory, a Critter [subclass] object, that is used as a "factory" to create Critter objects.
     critterFactory = new SquareCritter();
     critterList = new LinkedList<>();
 }
 
    /**
     * Starts the game object
     */
    public void start()
 {
     // 1. put the game in the state that it is not over.
	 isOver = false;
	 lastClick = true;
	 
     // 2. construct an ArrayList of new Critters. 
     //    For each Critter that you create, use the critterFactory to randomly select a subclass of Critter to instantiate.
	 critterList = new LinkedList<>();
	 
	 for (int i = 0; i < 3; i++){
		 critterList.add(critterFactory.makeCritter());
	 }
	 
 }
     
    /**
     * Draws graphics to the image
     */
    public void draw()
 {       
	 
     Graphics graphics = image.getGraphics();
	 
     // 1. blank the previous image (otherwise, you get a "trail")
     graphics.setColor(Color.WHITE);
     graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
	 
     // 2. move then draw each critter.
    for (Critter critter : critterList){
         
         //  move the critter
         critter.move();
		 
	 //  draw the critter
         critter.draw(graphics);
    }
 }
     
    /**
     * Runs behavior for mouse clicks
     * @param x Mouse coordinate x
     * @param y Mouse coordinate y
     */
    public void processClick( int x, int y )
 //TODO program timer changes when screen is clicked, exception when clicked before game starts       
 {
     // for each critter, if the mouse click is "in" the critter, remove it from the critter list.
     //if such a removal makes the list empty, put the game in a "game over" state.
     if (!critterList.isEmpty()){
        for (Critter critter : critterList){
            if (critter instanceof SquareCritter){
                if ((Math.abs(x - (critter.x + 30)) <= 30) &&  (Math.abs(y - (critter.y + 30)) <= 30)){
                    critterList.remove(critter);  
                    break;
                }
            }

            else {  //if WOW critter or round critter
                if ( Math.sqrt(Math.pow(x - (critter.x + 30), 2) + Math.pow(y - (critter.y + 30), 2)) <= 30){
                    critterList.remove(critter); 
                    break;
                }
            }
        }
     }
     
     if (critterList.isEmpty()){
         isOver = true;
     }
 }
 
 abstract private class Critter
 {
     Color critterColor;
     int x;
     int y;
     int dx;
     int dy;
     
     Critter makeCritter() 
     {
         // construct a critter, selected randomly from the 3 Critter subclasses 
         double randNum = 3*Math.random();
         if(randNum < 1.0){
            return new SquareCritter();
         }
         else if(randNum < 2.0){
             return new RoundCritter();
         }
         else {
             return new WowCritter();
         }
     }
     
     private Critter()
     {
         // set attributes with suitably random values.
        
        float r = (float)Math.random();
        float g = (float)Math.random();
        float b = (float)Math.random();
        critterColor = new Color(r,g,b);
        x = (int)(Math.random()*IMAGE_SIZE);
        y = (int)(Math.random()*IMAGE_SIZE);
        int deltaMin = (int)Math.pow( (double)IMAGE_SIZE, (double)0.25);
        int deltaMax = (int)Math.pow( (double)IMAGE_SIZE, (double)0.5);
        dx = (int)(Math.random()*(deltaMax-deltaMin))+deltaMin;
        dy = (int)(Math.random()*(deltaMax-deltaMin))+deltaMin;
        if(Math.random() < 0.5){
            dx *= -1;
        }
        if(Math.random() < 0.5){
            dy *= -1;
        }
     }
     
     
     abstract void draw( Graphics graphics );
     
     void move()
     {

         x += dx;
         y += dy;
         while(x > IMAGE_SIZE){
             x -= IMAGE_SIZE;
         }
         while(x < 0){
             x += IMAGE_SIZE;
         }
         while(y > IMAGE_SIZE){
             y -= IMAGE_SIZE;
         }
         while(y < 0){
             y += IMAGE_SIZE;
         }
     }
     
 }
 
 // Subclass Critter with a square critter
 private class SquareCritter extends Critter
 {
	 
    public SquareCritter(){
        super();
    }

    @Override
    public void draw(Graphics graphics){
        graphics.setColor(critterColor);
    	graphics.fillRect(x, y, 60, 60);
    }
     
 }
 
 // Subclass Critter with a round critter
 private class RoundCritter extends Critter
 {
     // your implementation goes here.
    public RoundCritter(){
        super();
    }
     
    @Override
    public void draw(Graphics graphics){
        graphics.setColor(critterColor);
        graphics.fillOval(x, y, 60, 60);
    }

 }
 
 // Subclass Critter with a round critter whose color is randomly chosen each time it is drawn
 private class WowCritter extends Critter
 {
    public WowCritter(){
        super();
    }
    @Override
    public void draw(Graphics graphics){
        float r = (float)Math.random();
        float g = (float)Math.random();
        float b = (float)Math.random();
        critterColor = new Color(r,g,b);
        graphics.setColor(critterColor);
        graphics.fillOval(x, y, 60, 60);
    }
 }
}
    
