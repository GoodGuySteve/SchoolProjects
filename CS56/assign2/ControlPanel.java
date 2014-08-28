package assign2;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Steven Cabral
 * @author Austin Smith
 * Code skeleton by Pete Cappello
 */
public class ControlPanel extends JPanel 
{
    private final View view;
    private final GVM gvm;
    
    private final JButton runButton  = new JButton( "Run" );
    private final JButton stepButton = new JButton( "Step" );
    
    ControlPanel( View view, GVM gvm ) 
    {
        this.view = view;
        this.gvm = gvm;
     
        setLayout( new GridLayout( 1, 2 ) );
        add( runButton );
        add( stepButton );

        initialize();
        view.setImage( gvm.getImage() );
        gvm.load(); // load program to be executed
    }

    private void initialize() 
    {
        //------------------------------------------
        // contoller TEMPLATE CODE for each action
        //------------------------------------------
        // If you are running Java 8, use lambda expressions
//        runButton.addActionListener( ( ActionEvent actionEvent ) -> 
//        {
//            runButtonActionPerformed( actionEvent );
//        });
//        
//        stepButton.addActionListener( ( ActionEvent actionEvent ) -> 
//        {
//            stepButtonActionPerformed( actionEvent );
//        });
        
        // If you are not running Java 8, uncomment the code below   
        runButton.addActionListener( new ActionListener() 
        {
            @Override
            public void actionPerformed( ActionEvent actionEvent ) 
            {
                runButtonActionPerformed( actionEvent );
            }
        });
               
        stepButton.addActionListener( new ActionListener() 
        {
            @Override
            public void actionPerformed( ActionEvent actionEvent ) 
            {
                stepButtonActionPerformed( actionEvent );
            }
        });
    }

    // _____________________________
    //  controller for each action
    // _____________________________
    private void runButtonActionPerformed( ActionEvent actionEvent ) 
    {
        // your implementation goes here.
    	gvm.run();
    	view.setImage(gvm.getImage());
    	view.repaint();
    }

    private void stepButtonActionPerformed( ActionEvent actionEvent ) 
    { 
        // your implementation goes here.
    	gvm.step();
    	view.setImage(gvm.getImage());
    	view.repaint();
    }
}
