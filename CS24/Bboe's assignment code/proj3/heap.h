#include <algorithm>
#include <vector>


/* This class is internally used by the MaxHeap class. You should not need to
   make instances of it directly.
 */
template <class T, class U>
class PriorityContainer {
public:
  T item;
  U priority;
  PriorityContainer(T item, U priority): item(item), priority(priority) {}
  bool operator<(const PriorityContainer<T, U> &other) const {
    return priority < other.priority;
  }
};


/* Provide a MaxHeap class that is a simple wrapper around the data structures
   and operations provided in the C++ standard template library (STL).
 */
template <class T, class U>
class MaxHeap {
private:
  std::vector<PriorityContainer<T, U> > vector;
public:
  MaxHeap(): vector() {}
  void insert(T item, U priority) {
    vector.push_back(PriorityContainer<T, U>(item, priority));
    std::push_heap(vector.begin(), vector.end());
  }
  /* Return a boolean indicating whether or not the heap is empty. */
  bool empty() {
    return vector.empty();
  }
  /* Return (but do not remove) the highest priority item from the heap. */
  T peek() {
    return vector.front().item;
  }
  /* Remove and return the highest priority item from the heap. */
  T remove() {
    std::pop_heap(vector.begin(), vector.end());
    T retval = vector.back().item;
    vector.pop_back();
    return retval;
  }
};
