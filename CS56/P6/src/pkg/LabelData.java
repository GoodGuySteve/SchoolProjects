package pkg;

/**
* Interface for setting/getting line numbers, labels, and instructions
* @author Steven Cabral
* @author Austin Smith
*/
public class LabelData{

    private int lineNumber;
    private String label;
    private int[] instruction;

    public LabelData(int lineNumber, String label, int[] instruction) {
        this.lineNumber = lineNumber;
        this.label = label;
        this.instruction = instruction;
    }

    void setLineNumber(int x){
        lineNumber = x;
    }

    void setLabel(String x){
        label = new String(x);	
    }

    void setInstruction(int instruction, int position){
        this.instruction[position] = instruction;	
    }

    int getLineNumber(){
        return this.lineNumber;
    }

    String getLabel(){
        return this.label;
    }

    int[] getInstruction(){
        return this.instruction;
    }

}