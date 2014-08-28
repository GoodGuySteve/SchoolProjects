package lab4;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author Steven Cabral
 * @author Austin Smith
 * @author Pete Cappello
 */
public class App extends JFrame
{
    private static final int HEIGHT = 230;
    private static final int WIDTH = 450;
    
    private final Controller controller;
    
    App() 
    {        
        controller = new Controller(); 
        
        setTitle( "Printer Control Panel" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
          

        add( controller, BorderLayout.CENTER );
        
        Dimension dimension = new Dimension( WIDTH, HEIGHT );
        setSize( dimension  );
        setPreferredSize( dimension );
        setVisible( true );
    }
    
    /**
     * Run the application.
     * @param args unused 
     */
    public static void main( String[] args ) { App app = new App(); }
}