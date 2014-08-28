package pkg;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
 private final static int PAUSE = 200; // milliseconds
 private final View view;
 private final Game game;
 private final JLabel  gameLevelLabel = new JLabel( "Game Level: ");
 private final JTextField gameLevelText = new JTextField();
 private final JButton newGameButton = new JButton( "New Game" );
 private final JLabel  gameDurationLabel = new JLabel( "Game Duration (s):" );
 private final JTextField gameDurationTextField = new JTextField();
 private final Timer animationTimer;
 private long gameStartTime;
 private double gameDuration = 0;
 private int gameLevel = 4;
 private final JOptionPane completionBox = new JOptionPane();
 
    ControlPanel( View view, Game game ) 
    {
        this.view = view;
        this.game = game;
  
        setLayout( new GridLayout( 1, 5 ) );
        add( gameLevelLabel );
        add( gameLevelText );
        add( newGameButton );
        add( gameDurationLabel );
        add( gameDurationTextField );

        animationTimer = new Timer( PAUSE, this );
        gameLevelText.setEditable( false );
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
	 gameLevelText.setText(""+gameLevel);
     gameDurationTextField.setText("");
     
     gameStartTime = System.currentTimeMillis();
     animationTimer.setDelay(PAUSE - 20 * (gameLevel-4));
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
        if (game.getIsOver() && game.clickEnabled() == false){
                long gameEndTime;
                animationTimer.stop();
                gameEndTime = System.currentTimeMillis();
                gameDuration = (double)(gameEndTime - gameStartTime)/1000;
                if (gameStartTime > 0) //updates text immediately on game end
                	gameDurationTextField.setText(String.format("%.3f", gameDuration));
                if (gameDuration > 10.0){
                	if(gameLevel > 1) --gameLevel; //chose to bound level between 1 and 13
                	completionBox.showMessageDialog(null, "Too slow! Level decreased to "+gameLevel+".");
                }
                else {
                	if(gameLevel < 13) ++gameLevel; //level capped at 13 for performance reasons
                	completionBox.showMessageDialog(null, "Congratulations! Level increased to "+gameLevel+"!");
                }
                game.clickEnabled(true);
        }

        gameDuration = (double)(System.currentTimeMillis() - gameStartTime)/1000;
        
        if (gameStartTime > 0 && gameDuration > 0 && !game.getIsOver())
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
