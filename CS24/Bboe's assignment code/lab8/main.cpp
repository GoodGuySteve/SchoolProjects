#include <cmath>
#include <iomanip>
#include <iostream>
#include <map>
using namespace std;

map<int, int> counts;


/* Given a sorted deallocatable array `left` with `left_n` elements, and a
   sorted deallocatable array `right` with `right_n` elements, return a new
   sorted deallocatable array that contains all the elements of `left` and
   `right`.

   The `left` and `right` arrays must be deallocatable because they are
   deallocated as part of this function.
*/
template <class T>
T *merge(T *left, int left_n, T *right, int right_n) {
  int n = left_n + right_n;
  if (counts.find(n) != counts.end())
    counts[n] += 1;
  else
    counts[n] = 1;

  int idx = 0, left_idx = 0, right_idx = 0;
  T *retval = new T[n];
  while (left_idx < left_n || right_idx < right_n) {
    if (left_idx < left_n && right_idx < right_n) {  // Choose the smaller
      if (left[left_idx] <= right[right_idx])
        retval[idx++] = left[left_idx++];
      else
        retval[idx++] = right[right_idx++];
    }
    else if (left_idx < left_n) // No more on the right
      retval[idx++] = left[left_idx++];
    else  // No more on the left
      retval[idx++] = right[right_idx++];
  }
  delete [] left;
  delete [] right;
  return retval;
}

#include "recursion.cpp"  // just so you only write code recursion.cpp


template <class T>
void output_array(const T *array, int n) {
  for (int i = 0; i < n; ++i) {
    if (i != 0 && i % 16 == 0)
      cout << endl;
    cout << array[i] << " ";
  }
  if (n > 0)
    cout << endl;
}


int main(int argc, char* argv[]) {
  int n = argc - 1;
  string *items = new string[n];
  for (int i = 1; i < argc; ++i) {
    items[i - 1] = argv[i];
  }
  cout << "Original array\n";
  output_array(items, n);
  cout << endl;

  if (n > 0) {
    cout << "There are " << count(items, n, items[0]) << " items with value " \
         << items[0] << " in the array.\n";
  }


  string *sorted = merge_sort(items, n);

  cout << "Sorted array\n";
  output_array(sorted, n);
  cout << endl;

  delete [] items;
  delete [] sorted;

  // Output merge size associated with count
  int total = 0;
  int width = (int)log10(n) + 1;
  cout << "For " << n << " items there were the following merges:\n";
  for (map<int, int>::iterator it = counts.begin(); it != counts.end(); ++it) {
    cout << " * ";
    cout << setw(width) << it->second;
    cout << " merge(s) of ";
    cout << setw(width) << it->first << " items\n";
    total += it->first * it->second;
  }
  cout << endl;

  // Output statistics
  if (total > 0) {
    cout << "Total: " << total << endl;
    cout << fixed << setprecision(2);
    cout << "n*log(n) = " << (double)n * log2(n) << endl;
  }
  else
    cout << "There were no merges.\n";

  return 0;
}
