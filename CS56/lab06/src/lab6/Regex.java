
package lab6;

/**
 *
 * @author Austin Smith
 * @author Steven Cabral
 * @author Nevena
 */


import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.JOptionPane;

public class Regex {

    public static void main(String[] args){
        while (true) {
            
            
            String regex = "[A-Za-z0-9._]+@[a-z0-9.-]+\.[a-z]{2,4}";
            String enteredString = JOptionPane.showInputDialog("Enter your regex: ");
            Pattern pattern = Pattern.compile(regex);

            enteredString = JOptionPane.showInputDialog("Enter input string to search: ");
            Matcher matcher = pattern.matcher(enteredString);

            //[a-z0-9._+-]+@[a-z0-9.-]+\.[a-z]{2,4}
            
            boolean found = false;
            while (matcher.find()) {
                System.out.printf("I found the text" +
                    " \"%s\" starting at " +
                    "index %d and ending at index %d.%n",
                    matcher.group(),
                    matcher.start(),
                    matcher.end());
                found = true;
            }
            if(!found){
                System.out.printf("No match found.%n");
            }
        }
    }
}
