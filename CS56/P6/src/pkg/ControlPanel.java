package pkg;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

/**
* Manages the View, GVM, and GALA objects associated with the App
* 
* @author Steven Cabral
* @author Austin Smith
* Code skeleton by Pete Cappello
*/

public class ControlPanel extends JPanel
{
    private final View view;
    private final GVM gvm;
    private final GALA gala;
    
    private JTextArea console;
    private JTextArea dataMemText;
    private JTextArea stackText;
    private Map defMap;
    
    private final JFileChooser fileChooser = new JFileChooser();
    private final JButton loadButton = new JButton( "Load" );
    private final JButton runButton = new JButton( "Run" );
    private final JButton stepButton = new JButton( "Step" );
    
    ControlPanel( View view, GVM gvm, GALA gala )
    {
        Image image;
        
        this.view = view;
        this.gvm = gvm;
        this.gala = gala;
        
        console = gala.getConsole();
        dataMemText = gala.getDataMemText();
        stackText = gala.getStackText();
        gvm.setConsole(console);
        gvm.setDataMemText(dataMemText);
        gvm.setStackText(stackText);
        
        setLayout( new GridLayout( 1, 3 ) );
        add( loadButton);
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
        //
        // loadButton.addActionListener( ( ActionEvent actionEvent ) ->
        // {
        // loadButtonActionPerformed( actionEvent );
        // });
        //
        // runButton.addActionListener( ( ActionEvent actionEvent ) ->
        // {
        // runButtonActionPerformed( actionEvent );
        // });
        //
        // stepButton.addActionListener( ( ActionEvent actionEvent ) ->
        // {
        // stepButtonActionPerformed( actionEvent );
        // });
        
        // If you are not running Java 8, uncomment the code below
        loadButton.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent actionEvent )
            {
                loadButtonActionPerformed( actionEvent );
            }
        });
        
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
    // controller for each action
    // _____________________________
    private void loadButtonActionPerformed( ActionEvent actionEvent )
    {
        int[][] instructions;
        fileChooser.showOpenDialog(getParent());
        gala.setFile(fileChooser.getSelectedFile());
        instructions = gala.processFile();
        defMap = gala.getDefMap();
        gvm.setDefMap(defMap);
        if(instructions != null){
            gvm.load(instructions);
        }
    }
    
    private void runButtonActionPerformed( ActionEvent actionEvent )
    {
     gvm.run();
     view.setImage(gvm.getImage());
     view.repaint();
     updateText();
    }

    private void stepButtonActionPerformed( ActionEvent actionEvent )
    {
     gvm.step();
     view.setImage(gvm.getImage());
     view.repaint();
     updateText();
    }
    
    private void updateText(){
        dataMemText.setText("");
        stackText.setText("");
        
        gvm.printDataMem();
        gvm.printStack();
    }
}