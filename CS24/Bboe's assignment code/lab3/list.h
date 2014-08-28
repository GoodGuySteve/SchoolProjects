#ifndef LIST_H
#define LIST_H

/* The list structure is used to store a list of strings. */

#ifdef ARRAY
/* The array implementation should use a dynamic array and resize it as
   necessary.
*/
struct List {
  int _allocated;
  int _size;
  char **_items;
};
#else
/* The linked implementation should use a linked set of Nodes. */
struct Node {
  char *_data;
  struct Node *_next;
};

struct List {
  struct Node *_head;
};
#endif

/* Return the value of the item at the indicated position, or NULL if the
   position is invalid.

   Negative numbers are supported to mean position from the end of the
   list. For instance -1 is the last element in the list and -SIZE would be the
   first element. -(SIZE + 1) is invalid.
 */
char *list_at(struct List *list, int position);

/* Allocate a List structure and return it for use. Return NULL if allocation
   fails. */
struct List *list_construct();

/* Free any memory allocated by the list, including the List structure
   itself.

   An interesting question is what to do with the character strings. Do they
   need to be freed? Well that depends, the pointers to the c-strings being
   added to the list may be dynamically allocated (on the heap) in which case
   they are freeable, but it's also perfectly valid to pass a pointer to a
   c-string that exists in the initialization section (code+), or to a c-string
   that exists on the stack of some function; in either of these cases they
   cannot be freed.

   Thus your destruct function need not worry about freeing the memory
   associated with the c-strings themselves. The function needs to only free
   memory that the list allocated.
*/
void list_destruct(struct List *list);

/* Return the size, or number of elements, in the list. */
int list_get_size(struct List *list);

/* Return 1 if the list is empty, 0 otherwise */
int list_is_empty(struct List *list);

/* Output the contents of the list.

   The format string should be "%3d: %s\n" where the first parameter is the
   position of the item, starting with 0, and the second is the item itself.
 */
void list_output(struct List *list);

/* Add item to the end of the list. Return 1 upon success or 0 upon failure.

   Note: We are simply adding the pointer to the c-string to the list. You
   should not duplicate the c-string.
 */
int list_push_back(struct List *list, char *item);

/* Remove and return the element at the indicated position. Return NULL if the
   position is not valid.

   Negative numbers are supported to mean position from the end of the
   list. For instance -1 is the last element in the list and -SIZE would be the
   first element. -(SIZE + 1) is invalid.
*/
char *list_remove_at(struct List *list, int position);


#endif /* LIST_H */
