#include <stdio.h>
#include <stdlib.h>

// you may NOT #include additional files or use any of the functions in
// the string library (if you are unsure please ask)


/* Return the length of the c-string str.

   The null-terminating character '\0' does not count as part of the length.

   NOTE: You may NOT make ANY function calls.
*/
int stringlength(char *str) {
  // TODO: count and return the number of characters in str.
  return -1;
}


/* Return the number of instances of char c in c-string str.

   NOTE: You may NOT make ANY function calls.
*/
int countchars(char *str, char c) {
  // TODO: count and return the number of instances of c in str.
  return -1;
}


/* Return a new c-string that is the concatenation of c-strings str1 and str2.

   NOTE: The only functions you may call are those you already wrote.
*/
char *safestringconcat(char *str1, char *str2) {
  // TODO:
  //   * Allocate sufficient space for the new string
  //   * Copy str1 to the new string
  //   * Copy str2 to the new string
  //   * Return the new string
  return NULL;
}


/* The entry point to this program.

   Note: You should not make any changes to this function.
 */
int main(int argc, char **argv) {

  char *sa[] = {"Hello, how are you?",
                "I.m just fine, thank you.",
                "Monkeys are awesome.",
                "I complete agree, though I also really like platypi."};
  int i = 4;
  int size;
  FILE *file;
  char buf[256];

  printf("Enter a test string.\n");
  printf("To quit, make the first letter 'Q'\n");
  fgets(buf, 256, stdin);
  while(buf[0] != 'Q') {
      char *str;
      printf("String: %s", buf);
      printf("The length of string, counting return character: %d\n",
             stringlength(buf));

      printf("There are %d a's in the string\n",
             countchars(buf, 'a'));

      printf("There are %d c's in the string\n",
             countchars(buf, 'c'));

      str = safestringconcat(buf, sa[i++ % 4]);
      if (str == NULL)
        printf("safestringconcat not yet implemented\n");
      else {
        printf("Safe concat returned: %s\n", str);
        free(str);
      }

      printf("Enter a test string.\n");
      printf("To quit, make the first letter 'Q'\n");
      fgets(buf, 256, stdin);
    }
  return 0;
}
