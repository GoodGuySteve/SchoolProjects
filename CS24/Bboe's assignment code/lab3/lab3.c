/*
  Your task is to write the code that discovers the bug in each of the
  following four functions.

  You have to think about the different states the array-based implementation
  may be in, and where a developer may have made a mistake. You'll notice that
  the implementation passes all the tests provided with test_list.c.

  In each of these test functions you are given a valid list that was
  constructed. Each of the list_* functions assume the first argument is a
  valid list structure, so do not waste time making the program crash by
  passing invalid values as the list parameter to the various functions.

  Note: In each function you have to ensure you only trigger the bug you're
  looking for. If more than one bug is triggered, you will not pass the test as
  you have not demonstrated that you can isolate the bug.
*/

int test_find_list_at_bug(struct List *list) {
  // TODO: Find the bug in list_at
  return 0;
}

int test_find_list_destruct_bug(struct List *list) {
  // TODO: Find the bug in list_destruct
  // Note: list_destruct is called automatically when this function returns
  return 0;
}

int test_find_list_push_back_bug(struct List *list) {
  // TODO: Find the bug in list_push_back
  return 0;
}

int test_find_list_remove_at_bug(struct List *list) {
  // TODO: Find the bug in list_remove_at
  return 0;
}
