package pkg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
 
/**
 * Application to run the GALA program
 * 
* @author Austin Smith
* @author Steven Cabral
* @author Pete Cappello
*/
public class App extends JFrame {
     private final View view = new View();
     private final ControlPanel controlPanel;
     private final GVM gvm;
     private final GALA gala = new GALA();
                 
     App() {
         gvm = new GVM();
         controlPanel = new ControlPanel( view, gvm, gala );
         setTitle( "Graphics Virtual Machine" );
         setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                       
         add( view, BorderLayout.CENTER );
         add( controlPanel, BorderLayout.NORTH);
         add( gala, BorderLayout.SOUTH);
                  
         Dimension dimension = new Dimension( GVM.IMAGE_SIZE, GVM.IMAGE_SIZE + controlPanel.getHeight() );
         setSize( dimension );
         setPreferredSize( dimension );
         setVisible( true );
     }
     
/**
* Run the Graphics Virtual Machine application.
* @param args unused
*/
     public static void main( String[] args ) { App app = new App(); }
}