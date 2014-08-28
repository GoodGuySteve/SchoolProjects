#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"

#define linesize 256

/* Remove a trailing newline if one exists */
void str_rstrip(char *str) {
  int index = strlen(str) - 1;
  if (str[index] == '\n')
    str[index] = '\0';
}

int main(int argc, char *argv[]) {
  FILE *input;

  if (argc == 1) {
    /* If there are no additional arguments read from stdin */
    input = stdin;
  }
  else if (argc == 2) {
    /* If there is a single argument, attempt to read from that file */
    if (!(input = fopen(argv[1], "r"))) {
      perror(argv[1]);
      return 1;
    }
  }
  else {
    /* If there are too many arguments, print the usage statement */
    printf("Usage: proj1 [input_file]\n");
    return 1;
  }

  struct List *list = list_construct();
  if (!list) {
    perror("Cannot create list");
    return 1;
  }

  /* Read in lines one at a time, storing them in the List until either the end
   * of the file, or until a line is too long (larger than 255 characters)
   */
  int lineno = 1;
  char buf[linesize + 1];
  printf("Enter a string to add to the list (or empty to stop): ");
  fflush(stdout);
  while (fgets(buf, linesize + 1, input)) {
    if (strlen(buf) == linesize) {
      printf("Line %d is too long. Goodbye!\n", lineno);
      return 1;
    }
    ++lineno;
    str_rstrip(buf);
    if (strcmp(buf, "") == 0)
      break;
    // We use strdup on the next line to make a copy of the string from
    // the buffer as the buffer is overwritten. Note that strdup returns
    // dynamically allocated memory that must be freed.
    if (!list_push_back(list, strdup(buf))) {
      perror("Could not add item to the list");
      return 1;
    }
    printf("Enter a string to add to the list (or empty to stop): ");
    fflush(stdout);
  }

  list_output(list);

  int size = list_get_size(list);

  // Output the first, middle, and last elements
  printf(" First+: %s\n", list_at(list, 0));
  printf(" First-: %s\n", list_at(list, -size));
  printf("Middle+: %s\n", list_at(list, size / 2));
  printf("Middle-: %s\n", list_at(list, -size / 2));
  printf("  Last+: %s\n", list_at(list, size - 1));
  printf("  Last-: %s\n", list_at(list, -1));

  // Verify invalid positions
  printf("     -1: %s\n", list_at(list, -(size + 1)));
  printf("     +1: %s\n", list_at(list, size));

  // Remove at invalid positions
  printf("remove -1: %s\n", list_remove_at(list, -(size + 1)));
  printf("remove +1: %s\n", list_remove_at(list, size));

  // Remove some items
  if (list_get_size(list) < 1)  // No point in continuing
    goto exit;
  char *tmp;
  tmp = list_remove_at(list, size / 2);  // from the middle
  printf("Removed: %s\n", tmp);
  list_output(list);
  free(tmp);
  if (list_get_size(list) < 1)  // No point in continuing
    goto exit;
  tmp = list_remove_at(list, 0);  // from the begining
  printf("Removed: %s\n", tmp);
  list_output(list);
  free(tmp);
  while (!list_is_empty(list)) {
    tmp = list_remove_at(list, -1);
    printf("Removed: %s\n", tmp);
    list_output(list);
    free(tmp);
  }

 exit:
  /* Clean up the list */
  list_destruct(list);
  return 0;
}
