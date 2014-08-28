package pkg;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Steven Cabral
 * @author Austin Smith
 * Class calculates the mathematical GCD of two numbers via 2 recursive methods
 *
 */

public class GCD {
	
	/**
	 * Instrumental method for finding the GCD of two integers using a stack
	 * @param x as an integer
	 * @param y as an integer
	 * @param stack
	 * @return the GCD of x and y
	 */
	public static int instFindGCD(int x, int y, LinkedList<Point> stack){

		if(y == 0){
			System.out.println("Base case encountered ...");
			printStack(stack);
			return x;
		}
		else{
			System.out.println("Pushing ...");
			stack.push(new Point(y, x%y));
			printStack(stack);

			int retval = instFindGCD(y, x%y, stack);
			System.out.println("Popping ...");
			stack.pop();
			printStack(stack);
			return retval;
		}
	}
	
	/**
	 * Finds the GCD of two integers x and y
	 * @param x as integer
	 * @param y as integer
	 * @return the GCD of x and y
	 */
	public int findGCD(int x, int y){
		if(y == 0) return x;
		else return (findGCD(y, x%y));
	}
	
	/**
	 * Prints out the passed stack
	 * @param stack
	 * 
	 */
	public static void printStack(LinkedList<Point> stack){
		Point[] stackArray = stack.toArray(new Point[0]);
		for(int i=0; i < stackArray.length; ++i){
			int index = stackArray.length - i - 1;
			System.out.println("Level " + i + " arguments: " + stackArray[index].x + " " + stackArray[index].y);
		}
		System.out.println(" ");
	}
	
	/**
	 * Main function for testing
	 * @param args
	 */
	public static void main (String[] args){
		LinkedList<Point> stack = new LinkedList();

		testPoint(25, 5, stack);
		testPoint(16, 14, stack);
		testPoint(70, 42, stack);
		testPoint(-70, -42, stack);
		testPoint(1024*3, 512*5, stack);
	}
	
	/**
	 * Tests individual points easily for the GCD functions
	 * @param x
	 * @param y
	 * @param stack
	 */
	private static void testPoint(int x, int y, LinkedList<Point> stack){
		int gcd;
		//flips x and y for better-looking output (matches Professor's code)
		if(y > x){
			int temp = y;
			y = x;
			x = temp;
		}
		stack.push(new Point(x, y));
		printStack(stack);
		gcd = instFindGCD(x, y, stack);
		System.out.println("gcd( " + x + ", " + y + " ): " + gcd);
		stack.pop();
	}
	
}
