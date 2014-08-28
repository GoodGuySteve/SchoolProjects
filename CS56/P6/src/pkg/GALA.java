package pkg;

import java.awt.GridLayout;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
* Processes instructions and passes them to the instruction list
*
* @author Steven Cabral
* @author Austin Smith
*/
public class GALA extends JPanel{
    
    private final JTextArea programText = new JTextArea(8, 1);
    private final JTextArea console = new JTextArea(8, 1);
    private final JTextArea dataMemText = new JTextArea(8, 1);
    private final JTextArea stackText = new JTextArea(8, 1);
    private final JScrollPane programTextScroll = new JScrollPane(programText);
    private final JScrollPane consoleScroll = new JScrollPane(console);
    private final JScrollPane dataCellScroll = new JScrollPane(dataMemText);
    private final JScrollPane dataStackScroll = new JScrollPane(stackText);
    private File file;
    private final Map operandMap;
    private final Map opcodeMap;
    private Map labelsMap;
    private Map defMap;
    private LinkedList<LabelData> labelsToUpdate;
    private LinkedList<int[]> instructionList;
    private int instructionCount;
    private double buildTime;
    
 
    GALA(){
        setLayout(new GridLayout(2,2));
        add(programTextScroll);
        add(consoleScroll);
        add(dataCellScroll);
        add(dataStackScroll);
        
        opcodeMap = new HashMap<>();
        opcodeMap.put("stop", 0);
        opcodeMap.put("set", 1);
        opcodeMap.put("load", 2);
        opcodeMap.put("store", 3);
        opcodeMap.put("add", 4);
        opcodeMap.put("zero", 5);
        opcodeMap.put("goto", 6);
        opcodeMap.put("setcolor", 7);
        opcodeMap.put("drawline", 8);
        opcodeMap.put("drawrect", 9);
        opcodeMap.put("fillrect", 10);
        opcodeMap.put("drawoval", 11);
        opcodeMap.put("filloval", 12);
        opcodeMap.put("read", 13);
        opcodeMap.put("write", 14);
        opcodeMap.put("call", 15);
        opcodeMap.put("return", 16);
        opcodeMap.put("pop", 17);
        opcodeMap.put("push", 18);
        
        operandMap = new HashMap<>();	
        operandMap.put("stop", 0);
        operandMap.put("setcolor", 0);
        operandMap.put("drawline", 0);
        operandMap.put("drawrect", 0);
        operandMap.put("drawoval", 0);
        operandMap.put("fillrect", 0);
        operandMap.put("filloval", 0);
        operandMap.put("return", 0);
        operandMap.put("set", 1);
        operandMap.put("load", 1);
        operandMap.put("store", 1);
        operandMap.put("add", 1);
        operandMap.put("goto", 1);
        operandMap.put("zero", 1);
        operandMap.put("read", 1);
        operandMap.put("write", 1);
        operandMap.put("call", 1);
        operandMap.put("pop", 1);
        operandMap.put("push", 1);

        programText.setEditable(false);
        console.setEditable(false);
        dataMemText.setEditable(false);
        dataMemText.setEditable(false);
    }
    
    JTextArea getConsole(){
        return console;
    }
    
    JTextArea getDataMemText(){
        return dataMemText;
    }
    
    JTextArea getStackText(){
        return stackText;
    }
    
    Map getDefMap(){
        return defMap;
    }
    
    /** 
     * Sets instruction file
     * @param file Active file
     * @return Returns instruction file
     */
    public File setFile(File file){ return this.file = file; }
    
    
    /**
     * Parses and verifies each line of file
     * @return Returns GVM instructions
     */
    public int[][] processFile(){
        
        //add timer
        long startTime = System.currentTimeMillis();
        long endTime;
        buildTime = 0;
        
        int lineNumber = 1;
        BufferedReader fileReader;
        String line;
        
        instructionList = new LinkedList<>();
        labelsToUpdate = new LinkedList<>();
        labelsMap = new HashMap<>();
        defMap = new HashMap<>();
        instructionCount = 0;
        
        defMap.put("ACC", 0);
        defMap.put("X", 1);
        defMap.put("Y", 2);
        defMap.put("WIDTH", 3);
        defMap.put("HEIGHT", 4);
        defMap.put("RED", 5);
        defMap.put("GREEN", 6);
        defMap.put("BLUE", 7);
        
        //Clear the console and program JTextFields
        programText.setText("");
        console.setText("");
        
        console.append("Starting assembly... \n");
        try {
            //check that the file exists and is readable
            if(file == null || !file.canRead()) throw new IOException();
            fileReader = new BufferedReader( new FileReader( file.getCanonicalPath()));
        }
        catch (IOException e){
            console.append("Error - could not read file " + file + "\n");
            return null;
        }
       
        //Processes each line, adding line number
        try {
            line = fileReader.readLine();
            while(line != null){
                int[] instruction;                
                programText.append(lineNumber + " " + line + "\n");
                instruction = processLine(line, lineNumber);
                if(instruction != null) {
                    instructionList.addLast(instruction);
                    ++instructionCount;
                }
                line = fileReader.readLine();
                ++lineNumber;
            }
        }
        catch(IOException e){
            console.append("Error - could not read file " + file + "\n");
            return null;
        }
        
        updateLabels();
        
        //converts list to integer array
        int[][] instructions = instructionsAsArray();
        
        //calculate build time
        endTime = System.currentTimeMillis();
        buildTime = (double)(endTime - startTime)/1000;
        
        errorReturn();
        return instructions; //returns the instructions as a double integer array
    }

    
    /**
     * Processes each line depending on instruction
     * @param line String representation of each line
     * @param lineNumber Integer indicating line number
     * @return Returns null after placing key/value pairs into hash map
     */
    private int[] processLine(String line, int lineNumber){
    
        //Check for # in each line and ignore it and whatever comes after
        String[] tempArray = line.split("#");
        line = new String(tempArray[0]);        
        String[] words = line.split("\\s+");
        
        if (words.length == 0)  //skips blank lines
         return null;
        
        if (words[0].length() == 0){  //removes leading whitespace and reprocesses string
            if(words.length <= 1) 
                return null; //base case
            String newString = new String("");
         
            for(int i = 1; i < words.length; i++){
                newString += words[i];
                newString += " ";
            }
        return processLine(newString, lineNumber);
        }
        
        if (words[0].charAt(0) == '#') //skips comments
         return null;
        
        //if line is a define instruction, check validity of format
        if (words[0].equals("define")){
            
            if (words.length != 3){
                if (words.length < 3)
                    console.append(lineNumber + ": Error - Too few operands for define.\n");
                if (words.length > 3)
                    console.append(lineNumber + ": Error - Too many operands for define.\n");
            return null;
            }
             
        Pattern firstOperand = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher matcher = firstOperand.matcher(words[1]);
        
        if (!matcher.matches()){
            console.append(lineNumber + ": Error - First operand for define must be an identifier.\n");
            return null;
        }
        
        Pattern secondOperand = Pattern.compile("^[-0-9][0-9]*");
        matcher = secondOperand.matcher(words[2]);
        
        if (!matcher.matches()){
        console.append(lineNumber + ": Error - Second operand for define must be an integer.\n");
        return null;
        }
        
        String key = new String (words[1]);
        int value = new Integer (Integer.parseInt(words[2]));

        if (defMap.containsKey(key)){
            console.append(lineNumber + ": Error - " + words[2] + " is already defined.\n");
            return null;
        }

        defMap.put(key, value);
        return null;

        }
        
        
        //check if first argument is opcode
        if (opcodeMap.containsKey(words[0])){ //the first value of the string is an opcode
            return buildInstruction(words, lineNumber);
        }
        else { //assume first word is a label
            if(words.length < 2){ //make sure statement is long enough to work with
                console.append(lineNumber + ": Error - opcode " + words[0] + " not recognized.\n");
                return null;
            }
            
            if(!opcodeMap.containsKey(words[1])){
                console.append(lineNumber + ": Error - opcode " + words[1] + " not recognized.\n");
                return null;
            }
            
            if(words.length != ((int)operandMap.get(words[1])) + 2){
                console.append(lineNumber + ": Error - improper number of operands for " + words[1] + ".\n");
                return null;
            }
            
            Pattern firstOperand = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
            Matcher matcher = firstOperand.matcher(words[0]);
            if(! matcher.matches()){
                console.append(lineNumber + ": Error - label " + words[0] +" not an identifier.\n");
                return null;
            }
            
            //now can assume that the string is in proper format
            String label = new String(words[0]);
            String[] truncString = new String[2];
            truncString[0] = new String(words[1]);
            if(words.length == 3){
                truncString[1] = new String(words[2]);
            }
            
            int[] instruction = buildInstruction(truncString, lineNumber);
            if(instruction != null){ //only add the label if the line is syntactically correct
                if (labelsMap.containsKey(label)){
                    console.append(lineNumber + ": Error - label " + label + " already defined.\n");
                }
                else {
                    labelsMap.put(label, instructionCount); //instructionCount is also the address of the current instruction
                }
            }
            return instruction;
        }
    }
  
    /**
     * Defines behavior for operand instructions
     * @param words String array of each line
     * @param lineNumber Integer representing each line number
     * @return Returns null or instruction, depending on validity of operation
     */
    private int[] buildInstruction(String[] words, int lineNumber){
        if(!opcodeMap.containsKey(words[0])){
            console.append(lineNumber + ": Error - opcode " + words[0] + " not recognized.\n");
            return null;
        }
        int numOperands = (int) operandMap.get(words[0]);
            if(words.length != (numOperands+1)){
                console.append(lineNumber + ": Error - improper number of operands for " + words[0] + ".\n");
                return null;
            }
            int[] instruction;
            switch(numOperands){
                case 0: //0 operand behavior
                    instruction = new int[1];
                    instruction[0] = (int) opcodeMap.get(words[0]);
                    return instruction;
                    
                case 1: //1 operand behavior                    
                    instruction = new int[2];
                    instruction[0] = (int) opcodeMap.get(words[0]);
                    if(words[0].equals("goto") || words[0].equals("zero") || words[0].equals("call")){
                        instruction[1] = -1; //dummy label - will be updated or removed by label updater
                        labelsToUpdate.addLast(new LabelData(lineNumber, words[1], instruction)); //flag instruction for updating
                        return instruction;
                    }
                    else{
                        Pattern operand = Pattern.compile("^[-0-9][0-9]*");
                        Matcher matcher = operand.matcher(words[1]);
                        if(matcher.matches()){
                            //value is an integer
                            instruction[1] = Integer.parseInt(words[1]);
                            return instruction;
                        }
                        else{
                            //check if value is a predefined value
                            if(!defMap.containsKey(words[1])){
                                console.append(lineNumber + ": Error - symbol " + words[1] + " not defined.\n");
                                return null;
                            }
                            else {
                                instruction[1] = (int) defMap.get(words[1]);
                                return instruction;
                            }
                        }
                    }
                default:
                    //program should not reach here
                    console.append(lineNumber + ": Error - improper opcode " + words[0] + ".\n");
                    return null;
            }          
    }
    
    /**
     * Prints build status depending on amount of errors
     */    
    private void errorReturn(){    
        int errorCount = 0;
        String text = console.getText();	
        String[] lines = text.split("\n");
        for (String i : lines){
            if (i.contains("Error"))
            errorCount++;
        }
        if (errorCount == 0)
            console.append("BUILD SUCCESSFUL. Assembly time: " + buildTime + " sec.\n" );
        else
            console.append("BUILD FAILED. Assembly time: " + buildTime + " sec. Number of invalid statements: " + errorCount);
    }

    /**
     * Processes label for each operation
     */
    private void updateLabels(){
        for(LabelData data : labelsToUpdate){
            String label = data.getLabel();
            
            //check if the label is a positive integer
            Pattern operand = Pattern.compile("[0-9]*");
            Matcher matcher = operand.matcher(label);
            if(matcher.matches()){
                data.setInstruction(Integer.parseInt(label), 1);
                continue;
            }
            //check if the label was defined at some point in the program
            if(!labelsMap.containsKey(label)){
                console.append("Error - undefined label " + label + " on line " + data.getLineNumber() + ".\n");
                instructionList.remove(data.getInstruction());
                continue;
            }
            //set the instruction to contain the correct address value
            data.setInstruction( (int)labelsMap.get(label), 1);
            
        }
    }
     
    /**
     * Converts instructions to array
     * @return Returns array of instructions
     */
    private int[][] instructionsAsArray(){
        if(instructionList.isEmpty()){
            console.append("Error - program cannot read instructions.\n");
            return null;
        }
        
        int[][] instructions = new int[instructionList.size()][];
        int index = 0;
        
        while(!instructionList.isEmpty()){
            int[] instruction = instructionList.removeFirst();
            instructions[index] = instruction;
            ++index;
        }
        return instructions;
    }
}





