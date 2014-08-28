package pkg;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Manages the Game and View objects associated with the App
*@author Austin Smith
*@author Steven Cabral
*@author Pete Cappello
*/
public class ControlPanel extends JPanel implements ActionListener, MouseListener
{
 private final static int PAUSE = 150; // milliseconds
 private final View view;
 private final Game game;
 private final JButton newGameButton = new JButton( "New Game" );
 private final JLabel  gameDurationLabel = new JLabel( "  Game duration in seconds:" );
 private final JTextField gameDurationTextField = new JTextField();
 private final Timer animationTimer;
 private long gameStartTime;
 private double gameDuration = 0;
 
    ControlPanel( View view, Game game ) 
    {
        this.view = view;
        this.game = game;
  
        setLayout( new GridLayout( 1, 3 ) );
        add( newGameButton );
        add( gameDurationLabel );
        add( gameDurationTextField );

        animationTimer = new Timer( PAUSE, this );
        gameDurationTextField.setEditable( false );
        initialize();
 }

 private void initialize() 
 {
    //------------------------------------------
    // contoller TEMPLATE CODE for each action
    //------------------------------------------
    // If you are running Java 8, use lambda expressions
    //     newGameButton.addActionListener( 
    //         ( ActionEvent actionEvent ) -> { newGameButtonActionPerformed( actionEvent ); } 
    //     );
     
     newGameButton.addActionListener( new ActionListener() 
     {
         @Override
         public void actionPerformed( ActionEvent actionEvent ) 
         {
             newGameButtonActionPerformed( actionEvent );
         }
     });
     
     // register this as the listener for mouse events in the View JPanel
     view.addMouseListener( this );
 }

 // _____________________________
 //  controller for each action
 // _____________________________
 
 private void newGameButtonActionPerformed( ActionEvent actionEvent ) 
 {
	//starts the game and timer
     gameDurationTextField.setText("");
     gameStartTime = System.currentTimeMillis();
     animationTimer.restart();
     game.start();	 
 }
 
 /**
  * Implementation of ActionListener of Timer
  * @param e unused 
  */
 @Override
 public void actionPerformed( ActionEvent e) 
 {
        game.draw();

        view.repaint();
		//ends the game & updates timer
        if (game.getIsOver() && game.lastClick == true){
                long gameEndTime;
                animationTimer.stop();
                gameEndTime = System.currentTimeMillis();
                gameDuration = (double)(gameEndTime - gameStartTime)/1000;
                if (gameStartTime > 0) //updates text immediately on game end
                	gameDurationTextField.setText(String.format("%.3f", gameDuration));
                game.lastClick = false;
        }
        if (gameStartTime > 0 && gameDuration > 0)
            gameDurationTextField.setText(String.format("%.3f", gameDuration));
     
 }

 @Override
 public void mouseClicked( MouseEvent event ) {}

 @Override
 public void mousePressed(MouseEvent event) {
 
     animationTimer.stop();
         
     game.processClick( event.getX(), event.getY() );
     
     animationTimer.start();
     
     view.repaint();
 }

 @Override
 public void mouseReleased(MouseEvent e) {}

 @Override
 public void mouseEntered(MouseEvent e) {}
 
 @Override
 public void mouseExited(MouseEvent e) {}
}
