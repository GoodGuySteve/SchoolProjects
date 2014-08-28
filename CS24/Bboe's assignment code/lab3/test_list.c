#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"

#define THOUSANDS 8192

struct Test {
  int (*function)(struct List *list);
  char *name;
};

/* Constructs the list, calls the test functions, and destructs the list */
int wrapper(int (*function)(struct List *)) {
  struct List *list = list_construct();
  if (!list) {
    printf("The constructor failed. All tests depend on the constructor.\n");
    return 0;
  }
  int retval = function(list);
  list_destruct(list);
  return retval;
}

/* THE ACTUAL TESTS

   The test functions should all have the same prototype as the following
   functions. They should return 1 on success, and 0 on failure.

   In order to run the test, the test function name as well as a name for the
   test must be added to the TESTS array which is declared immediately after
   all the test functions.
 */


#include "lab3.c"  // Remember we generally do not want to include .c files


/* If this test does not pass, all other tests will fail as they depend on
   construction and destruction. */
int test_construct_destruct(struct List *list) {
  return 1;  // The wrapper takes care of the actual test.
}

int test_at_0_after_push_back_one(struct List *list) {
  list_push_back(list, "0");
  char *at = list_at(list, 0);
  if (!at)
    return 0;
  int retval = strcmp(at, "0") == 0;
  list_remove_at(list, -1);
  return retval;
}

int test_at_minus1_after_construct(struct List *list) {
  return list_at(list, -1) == NULL;
}

int test_at_minus1_after_push_back_one(struct List *list) {
  list_push_back(list, "0");
  int retval = list_at(list, -2) == NULL;
  list_remove_at(list, -1);
  return retval;
}

int test_at_neg1_after_push_back_one(struct List *list) {
  list_push_back(list, "0");
  char *at = list_at(list, -1);
  if (!at)
    return 0;
  int retval = strcmp(at, "0") == 0;
  list_remove_at(list, -1);
  return retval;
}

int test_at_plus1_after_construct(struct List *list) {
  return list_at(list, 0) == NULL;
}

int test_at_plus1_after_push_back_one(struct List *list) {
  list_push_back(list, "0");
  int retval = list_at(list, 1) == NULL;
  list_remove_at(list, -1);
  return retval;
}

int test_get_size_after_construct(struct List *list) {
  return list_get_size(list) == 0;
}

int test_get_size_after_push_back_one(struct List *list) {
  list_push_back(list, "0");
  int retval = list_get_size(list) == 1;
  list_remove_at(list, -1);
  return retval;
}

int test_is_empty_after_construct(struct List *list) {
  return list_is_empty(list) == 1;
}

int test_is_empty_after_push_back_one(struct List *list) {
  list_push_back(list, "0");
  int retval = list_is_empty(list) == 0;
  list_remove_at(list, -1);
  return retval && list_is_empty(list) == 1;
}

int test_output_after_construct(struct List *list) {
  char *buf;
  size_t buf_n;
  FILE *prev_stdout = stdout;
  stdout = open_memstream(&buf, &buf_n);  // Capture stdout
  list_output(list);
  fclose(stdout);
  stdout = prev_stdout;  // Reset stdout
  int retval = strcmp("", buf) == 0;
  free(buf);
  return retval;
}

int test_output_after_push_back_one(struct List *list) {
  list_push_back(list, "");
  char *buf;
  size_t buf_n;
  FILE *prev_stdout = stdout;
  stdout = open_memstream(&buf, &buf_n);  // Capture stdout
  list_output(list);
  fclose(stdout);
  stdout = prev_stdout;  // Reset stdout
  int retval = strcmp("  0: \n", buf) == 0;
  list_remove_at(list, -1);
  free(buf);
  return retval;
}

int test_push_back_remove_at_0(struct List *list) {
  if (list_push_back(list, "") != 1)
    return 0;
  char *at = list_remove_at(list, 0);
  if (!at)
    return 0;
  return strcmp(at, "") == 0;
}

int test_push_back_remove_at_neg1(struct List *list) {
  if (list_push_back(list, "") != 1)
    return 0;
  char *at = list_remove_at(list, -1);
  if (!at)
    return 0;
  return strcmp(at, "") == 0;
}

int test_remove_at_0_after_construct(struct List *list) {
  return list_remove_at(list, 0) == NULL;
}

int test_remove_at_neg1_after_construct(struct List *list) {
  return list_remove_at(list, -1) == NULL;
}


/* Add the function name of you test function, and the string of the name of
   the test to include them in the testing process.
 */
struct Test TESTS[] = {
  test_construct_destruct, "construct_destruct",
  test_at_0_after_push_back_one, "at_0_after_push_back_one",
  test_at_minus1_after_construct, "at_minus1_after_construct",
  test_at_minus1_after_push_back_one, "at_minus1_after_push_back_one",
  test_at_neg1_after_push_back_one, "at_neg1_after_push_back_one",
  test_at_plus1_after_construct, "at_plus1_after_construct",
  test_at_plus1_after_push_back_one, "at_plus1_after_push_back_one",
  test_get_size_after_construct, "get_size_after_construct",
  test_get_size_after_push_back_one, "get_size_after_push_back_one",
  test_is_empty_after_construct, "is_empty_after_construct",
  test_is_empty_after_push_back_one, "is_empty_after_push_back_one",
  test_output_after_construct, "output_after_construct",
  test_output_after_push_back_one, "output_after_push_back_one",
  test_push_back_remove_at_0, "push_back_remove_at_0",
  test_push_back_remove_at_neg1, "push_back_remove_at_neg1",
  test_remove_at_0_after_construct, "remove_at_0_after_construct",
  test_remove_at_neg1_after_construct, "remove_at_neg1_after_construct",
  test_find_list_at_bug, "find_list_at_bug",
  test_find_list_destruct_bug, "find_list_destruct_bug",
  test_find_list_push_back_bug, "find_list_push_back_bug",
  test_find_list_remove_at_bug, "find_list_remove_at_bug"
};


/* This main program takes in a starting test number (and optional end test
   number) to run. Tests are considered passed if the test function returns a
   non-zero value.
 */
int main(int argc, char *argv[]) {
  if (argc < 2 || argc > 3) {
    printf("Usage: test TEST_START [TEST_END]\n");
    return 1;
  }
  int start = atoi(argv[1]);
  int end = start;
  int num_tests = sizeof(TESTS) / sizeof(struct Test);
  if (argc == 3) {
    end = atoi(argv[2]);
    if (end < 0 || end >= num_tests)
      end = num_tests - 1;
  }
  while (start <= end) {
    int retval;
    printf("Test %02d: %-30s ", start, TESTS[start].name);
    fflush(stdout);
    retval = wrapper(TESTS[start].function);
    char *status = retval ? "Passed" : "Failed";
    printf("%s\n", status);
    ++start;
  }
  return 0;
}
