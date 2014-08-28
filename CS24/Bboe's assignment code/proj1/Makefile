main:
	@echo "Run any one of:"
	@echo "    make array"
	@echo "    make linked"
	@echo "    make test_array"
	@echo "    make test_linked"

all: array linked

array: array_list.c main.c
	clang -DARRAY array_list.c main.c -o array_list

linked: linked_list.c main.c
	clang linked_list.c main.c -o linked_list

tests: test_array test_linked

test_array: array_list.c test_list.c
	clang -DARRAY array_list.c test_list.c -o test_array

test_linked: linked_list.c test_list.c
	clang linked_list.c test_list.c -o test_linked

clean:
	rm -f array_list linked_list test_array test_linked
