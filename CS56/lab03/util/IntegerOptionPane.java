/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import javax.swing.JOptionPane;

/**
 *
 * @author Austin Smith
 * @author Steven Cabral
 */



public class IntegerOptionPane {
    
    private String prompt;
    protected int integer;
    private String userInput;
    
    public IntegerOptionPane(String prompt){
    	this.prompt = prompt;
    }
    
    public int getIntValue(){
    	        
    	userInput = JOptionPane.showInputDialog(prompt);
        
        try{
            this.integer = Integer.parseInt(userInput);
            return this.integer;
        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Error: Not an int value." );
            return getIntValue();
        }           
    } 
	
//    public static void main(String[] args){
//        
//    	IntegerOptionPane pane = new IntegerOptionPane("Enter an int value:");
//    	int integer = pane.getIntValue();
//    	JOptionPane.showMessageDialog(null, "Your integer is " + integer);
//    }
    
}
