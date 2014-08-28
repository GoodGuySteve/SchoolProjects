package pkg;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
* Graphics Virtual Machine
*
* @author Steven Cabral
* @author Austin Smith
* Code skeleton by Pete Cappello
*/

public class GVM {
// symbolic program constants
static final int STOP = 0;
static final int SET = 1;
static final int LOAD = 2;
static final int STORE = 3;
static final int ADD = 4;
static final int ZERO = 5;
static final int GOTO = 6;
static final int SETCOLOR = 7;
static final int DRAWLINE = 8;
static final int DRAWRECT = 9;
static final int FILLRECT = 10;
static final int DRAWOVAL = 11;
static final int FILLOVAL = 12;
static final int READ = 13;
static final int WRITE = 14;
static final int CALL = 15;
static final int RETURN = 16;
static final int POP = 17;
static final int PUSH = 18;

static final int ACC = 0;
static final int X = 1;
static final int Y = 2;
static final int WIDTH = 3;
static final int HEIGHT = 4;
static final int RED = 5;
static final int GREEN = 6;
static final int BLUE = 7;
static final int ROW_COUNTER = 8;
static final int COL_COUNTER = 9;
static final int SHAPE_TYPE = 10;

static final int IMAGE_SIZE = 800;
static final int GRID_SIZE = IMAGE_SIZE/17;

// attribute type declarations
private final int[] dataMem = new int[100];
private int[][] programMem;
private int instAddress = 0;
private Color color;
private final BufferedImage image;
private final Graphics graphics;

private JTextArea console = new JTextArea("");
private JTextArea dataMemText = new JTextArea("");
private JTextArea stackText = new JTextArea("");
private Map defMap = new HashMap<>();

private final LinkedList<Integer> stack = new LinkedList<>();
private final LinkedList<Integer> returnStack = new LinkedList<>();

// initialize GVM with white screen and black color
GVM() {
    image = new BufferedImage( IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB );
    color = new Color(0,0,0);
    this.graphics = image.getGraphics();
    graphics.setColor(new Color(255,255,255));
    graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
    graphics.setColor(new Color(0,0,0));

    //intialize default define values
    defMap.put("ACC", 0);
    defMap.put("X", 1);
    defMap.put("Y", 2);
    defMap.put("WIDTH", 3);
    defMap.put("HEIGHT", 4);
    defMap.put("RED", 5);
    defMap.put("GREEN", 6);
    defMap.put("BLUE", 7);
}
 
 Image getImage() { return image; }
 
 void setConsole(JTextArea console){
     this.console = console;
 }
 
 void setDefMap(Map defMap){
     this.defMap = defMap;
 }
 
 void setDataMemText(JTextArea dataMemText){
     this.dataMemText = dataMemText;
 }
 
 void setStackText(JTextArea stackText){
     this.stackText = stackText;
 }
 
 void printDataMem(){
     for (Object x: defMap.keySet()){
         dataMemText.append( dataMem[(int)defMap.get(x)] + " " + x + "\n" );
     }
 }
 
 void printStack(){
     for (Integer frame : stack){
         stackText.append(frame + "\n");
     }
     stackText.append("\nData Stack\n");
 }
 
 void load()
 {
     programMem = new int[][] {
     /* uncomment code to draw original drawing
{ SET, 20 }, // ACC <- 10
{ STORE, X }, // STORE ACC -> X
{ STORE, Y }, // STORE ACC -> Y
{ STORE, WIDTH }, // STORE ACC -> WIDTH
{ STORE, HEIGHT }, // STORE ACC -> HEIGHT
{ DRAWRECT }, // DRAWRECT
{ SET, 255 }, // ACC <- 255
{ STORE, RED }, // ACC -> RED
{ SETCOLOR }, // SETCOLOR to red
{ LOAD, X }, // ACC <- X
{ ADD, X }, // ACC += X
{ STORE, X }, // ACC -> X
{ STORE, Y }, // ACC -> Y
{ STORE, WIDTH }, // STORE ACC -> WIDTH
{ STORE, HEIGHT }, // STORE ACC -> HEIGHT
{ DRAWOVAL }, // FILLRECT
{ LOAD, X }, // ACC <- X
{ ADD, X }, // ACC += X
{ STORE, X }, // ACC -> X
{ STORE, Y }, // ACC -> Y
{ STORE, WIDTH }, // STORE ACC -> WIDTH
{ STORE, HEIGHT }, // STORE ACC -> HEIGHT
{ FILLOVAL }, // FILLRECT
{ LOAD, X }, // ACC <- X
{ ADD, X }, // ACC += X
{ STORE, X }, // ACC -> X
{ STORE, Y }, // ACC -> Y
{ STORE, WIDTH }, // STORE ACC -> WIDTH
{ STORE, HEIGHT }, // STORE ACC -> HEIGHT
{ FILLRECT }, // FILLRECT
{ LOAD, X }, // ACC <- X
{ ADD, X }, // ACC += X
{ STORE, X }, // ACC -> X
{ STORE, Y }, // ACC -> Y
{ STORE, WIDTH }, // STORE ACC -> WIDTH
{ STORE, HEIGHT }, // STORE ACC -> HEIGHT
{ DRAWLINE }, // FILLRECT
{ STOP } // STOP
*/
         
        {SET, 0}, //0
        {STORE, X},
        {STORE, Y},
        {STORE, SHAPE_TYPE},
        {SET, GRID_SIZE},
        {STORE, WIDTH}, //5
        {STORE, HEIGHT},
        {SET, 17},
        {STORE, ROW_COUNTER},
        {SET, 17},
        {STORE, COL_COUNTER},//10
        //draw shape method
        {LOAD, SHAPE_TYPE},
        {ZERO, 23},
        {SET, 0},
        {STORE, BLUE},
        {STORE, GREEN}, //15
        {SET, 255},
        {STORE, RED},
        {SETCOLOR},
        {FILLOVAL},
        {SET, 1}, //20
        {STORE, SHAPE_TYPE},
        {GOTO, 30},
        {SET, 0},
        {STORE, RED},
        {STORE, GREEN}, //25
        {STORE, BLUE},
        {SETCOLOR},
        {FILLRECT},
        {STORE, SHAPE_TYPE},
        //loop over columns
        {SET, -1}, //30
        {ADD, COL_COUNTER},
        {STORE, COL_COUNTER},
        {LOAD, X},
        {ADD, WIDTH},
        {STORE, X}, //35
        {LOAD, COL_COUNTER},
        {ZERO, 11},
        {SET, 0},
        {STORE, X},
        //loop over rows
        {SET,-1}, //40
        {ADD, ROW_COUNTER},
        {STORE, ROW_COUNTER},
        {LOAD, Y},
        {ADD, HEIGHT},
        {STORE, Y}, //45
        {LOAD, ROW_COUNTER},
        {ZERO, 9},

        {STOP}

     };
 }
 
 void load(int[][] instructions){
     programMem = instructions;
     instAddress = 0;
 }
     
 void step(){
    if(programMem[instAddress][0] != STOP){
    executeInstruction(programMem[instAddress++]);
    }
}
 
 void run(){
    double startTime = System.currentTimeMillis();
    double endTime;
    double runTime;
    console.append("Starting run...\n");
    while(programMem[instAddress][0] != STOP){
    step();
    }
    endTime = System.currentTimeMillis();
    runTime = (double)(endTime - startTime)/1000;
    console.append("Finished executing run. Run time: " + runTime + " sec.\n");
}

 /**
  * Executes GVM Instructions
  * @param instruction Integer array of instructions
  */
 private void executeInstruction( int[] instruction ){
    switch(instruction[0]){
        case SET:
            dataMem[0] = instruction[1];
            break;
        case LOAD:
            dataMem[0] = dataMem[instruction[1]];
            break;
        case STORE:
            dataMem[instruction[1]] = dataMem[0];
            break;
        case ADD:
            dataMem[0] += dataMem[instruction[1]];
            break;
        case ZERO:
            if (dataMem[0] != 0){
                instAddress = instruction[1];
            }
            break;
        case GOTO:
            instAddress = instruction[1];
            break;
        case SETCOLOR:
            this.color = new Color(dataMem[RED], dataMem[GREEN], dataMem[BLUE]);
            break;
        case DRAWLINE:
            graphics.setColor(this.color);
            graphics.drawLine(dataMem[X], dataMem[Y], dataMem[X]+dataMem[WIDTH], dataMem[Y]+dataMem[HEIGHT]);
            break;
        case DRAWRECT:
            graphics.setColor(this.color);
            graphics.drawRect(dataMem[X], dataMem[Y],dataMem[WIDTH], dataMem[HEIGHT]);
            break;
        case FILLRECT:
            graphics.setColor(this.color);
            graphics.fillRect(dataMem[X], dataMem[Y], dataMem[WIDTH], dataMem[HEIGHT]);
            break;
        case DRAWOVAL:
            graphics.setColor(this.color);
            graphics.drawOval(dataMem[X], dataMem[Y],dataMem[WIDTH], dataMem[HEIGHT]);
            break;
        case FILLOVAL:
            graphics.setColor(this.color);
            graphics.fillOval(dataMem[X], dataMem[Y],dataMem[WIDTH], dataMem[HEIGHT]);
            break;
        case READ:
            String input = JOptionPane.showInputDialog("Enter an integer:");
            dataMem[instruction[1]] = Integer.parseInt(input);
            break;
        case WRITE:
            console.append(dataMem[instruction[1]] + "\n");
            break;
        case CALL:
            returnStack.push(instAddress);
            instAddress = instruction[1];
            break;
        case RETURN:
            instAddress = returnStack.pop();
            break;
        case POP:
            dataMem[instruction[1]] = stack.pop();
            break;
        case PUSH:
            stack.push(dataMem[instruction[1]]);
            break;
        default:
            break;
    }
 }
}

