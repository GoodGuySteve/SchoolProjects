/**
* Generic class for storing and utilizing a pair of objects of any type.
* @author Steven Cabral
* @author Austin Smith
*
*/

public class Pair <F, S>{

	private F first;
	private S second;
	
	Pair(){}
	
	Pair(F first, S second){
		this.first = first;
		this.second = second;
	}
	
	public F getFirst() {
		return first;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public S getSecond() {
		return second;
	}

	public void setSecond(S second) {
		this.second = second;
	}

	public static void main(String[] args) {
		
		Pair<String, Integer> pair1 = new Pair<>(new String("Hello!"), new Integer(15));
		Pair<Double, Integer[]> pair2 = new Pair<>();
		Integer[] integerArray = {3, 1, 4};
		pair2.setFirst(3.14);
		pair2.setSecond(integerArray);
		
		System.out.println(pair1.getFirst() + ", " + pair1.getSecond());
		System.out.println(pair2.getFirst() + ", {" + pair2.getSecond()[0] + ", " + pair2.getSecond()[1] + ", " + pair2.getSecond()[2] + "}");
	
	}

}
