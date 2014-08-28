/* Both of these functions must be implemented recursively. You may not use any
   loops in this file.
*/


/* Return the number of times `item` is found in `array`.

   `n` corresponds to the number of elements in `array`.
*/
template <class T>
int count(const T *array, int n, T item) {
  // TODO: Implement this function.
  return -1;
}


/* Return a a copy of array with its items sorted.

   Recursively divide the problem into smaller problems and then merge the
   sorted lists together using the provided `merge` function.
*/
template <class T>
T *merge_sort(const T *array, int n) {
  if (n == 0)
    return NULL;
  // TODO: Implement the remainder of this function.
  // For now this just returns a dummy array of the same size.
  T *tmp = new T[n];
  while (n > 0)
    tmp[--n] = array[0];
  return tmp;
}
