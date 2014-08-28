package lab5;

/**
* Class to convert strings to morse code
* @author Steven Cabral
* @author Austin Smith
*/
public class MorseCode {
		
		private static final String morseArray[] = {
		".- ",
		"-... ",
		"-.-. ",
		"-.. ",
		". ",
		"..-.",
		"--. ",
		".... ",
		".. ",
		".--- ",
		"-.- ",
		".-.. ",
		"-- ",
		"-. ",
		"--- ",
		".--. ",
		"--.- ",
		".-. ",
		"... ",
		"- ",
		"..- ",
		"...- ",
		".-- ",
		"-..- ",
		"-.-- ",
		"--.. ",
		"----- ",
		".---- ",
		"..--- ",
		"...-- ",
		"....- ",
		"..... ",
		"-.... ",
		"--... ",
		"---.. ",
		"----. "
		};
		
        public static void main(String[] args){

                MorseCode interpreter = new MorseCode();

                String phrase = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG 0 1 2 3 4 5 6 7 8 9";

                System.out.print(interpreter.toMorse(phrase));

        }

		/**
		* Converts a string in English to morse code
		* @param String in English alphabet
		* @return String in morse code
		*/
        public String toMorse(String phrase){

                String morseString = "";
				

                for (int i = 0; i < phrase.length(); i++ ){

                        char letter = phrase.charAt(i);

                        if(letter == ' '){
                            morseString += "   ";
                            continue;
                        }
                        int morseIndex = letter - 'A';
			if (morseIndex < 0){
                                morseIndex = letter - 48 + 26; //convert to digit
			}
			morseString += morseArray[morseIndex];
                }

                return morseString;

        }

}


