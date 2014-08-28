package assign2;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

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
	private int[] dataMem = new int[100];
	private int[][] programMem;
	private int instAddress = 0;
	private Color color;
	private BufferedImage image;
	private Graphics graphics;


    // initialize GVM with white screen and black color
	GVM() {         
	    image = new BufferedImage( IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB );
	    color = new Color(0,0,0);
	    this.graphics = image.getGraphics();
	    graphics.setColor(new Color(255,255,255));
	    graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
	    graphics.setColor(new Color(0,0,0));
	}
 
 Image getImage() { return image; }
 
 void load()
 {
     programMem = new int[][] {
    	 /* uncomment code to draw original drawing
         { SET, 20 },  // ACC <- 10
         { STORE, X },   // STORE ACC -> X
         { STORE, Y },   // STORE ACC -> Y
         { STORE, WIDTH }, // STORE ACC -> WIDTH
         { STORE, HEIGHT }, // STORE ACC -> HEIGHT
         { DRAWRECT }, // DRAWRECT
         { SET, 255 },  // ACC <- 255
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
         { STOP  }     // STOP
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
		 {SET, 1},  //20
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
     
 void step()
 { 
	 if(programMem[instAddress][0] != STOP){
		 executeInstruction(programMem[instAddress++]);
	 }
 } 
 
 void run()
 {
	 while(programMem[instAddress][0] != STOP){
		 step();
	 }
 }
 
 private void executeInstruction( int[] instruction )
 {
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
	 default:
		 break;
	 }
 }
}
