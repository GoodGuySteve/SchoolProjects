package util;

import javax.swing.JOptionPane;

/**
 *
 * @author Austin Smith
 * @author Steven Cabral
 */


public class BoundedIntegerOptionPane extends IntegerOptionPane { 
    
    private int low;
    private int high;
    
    public BoundedIntegerOptionPane(String prompt, int low, int high) {
    	super(prompt);
    	this.low = low;
    	this.high = high;
    }
    
    @Override
    public int getIntValue(){
            
    	this.integer = super.getIntValue();
    	if(this.integer > high || this.integer < low) { 
    		JOptionPane.showMessageDialog(null, "Error: integer out of bounds");
    		this.integer = getIntValue(); 
		}
        return this.integer;
    }   
    
    public static void main(String[] args){
        
    	BoundedIntegerOptionPane pane = new BoundedIntegerOptionPane("Enter an int value:", 0, 100);
    	int integer = pane.getIntValue();
    	JOptionPane.showMessageDialog(null, "Your integer is " + integer);
    }
}