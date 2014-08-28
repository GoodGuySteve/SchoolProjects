package assign2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
 
 /**
  * 
  * @author Pete Cappello
  */
public class App extends JFrame {
     private final View view = new View();;
     private final ControlPanel controlPanel;
     private final GVM gvm;
                 
     App() {        
         gvm = new GVM();
         controlPanel = new ControlPanel( view, gvm );        
         setTitle( "Graphics Virtual Machine" );
         setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                       
         add( view, BorderLayout.CENTER );
         add( controlPanel, BorderLayout.SOUTH );
         
         Dimension dimension = new Dimension( GVM.IMAGE_SIZE, GVM.IMAGE_SIZE + controlPanel.getHeight() );
         setSize( dimension  );
         setPreferredSize( dimension );
         setVisible( true );
     }
     
     /**
      * Run the Graphics Virtual Machine application.
      * @param args unused 
      */
     public static void main( String[] args ) { App app = new App(); }
}