# xSpim Memory Demo Program

#  Data Area
.data

welcome:
	.asciiz "This program loads, stores, and operates on variables\nLooking at the comments in the code, perform the operations specified."


update:
	.asciiz "\nThe variable values now are: "
comma:
	.asciiz ", "

enter:
	.asciiz "\nEnter a value: "

# global variables
#globalA:
#        .word 0x00
#globalB:
#        .word 0x05
#globalC:
#        .word 0x10
#globalD:
#        .word 0x20

# myArray declaration allocates & initializes a 9 integer array,
#myArray:
#        .word 0x20040002 0x20080001 0x200b0001 0x8b502a
#        .word 0x15400003 0x1084020 0x20840000 0x800fffa
#        .word 0x1001020


#Text Area (i.e. instructions)
.text

main:

	# Display the welcome message;  lui and ori constants are obtained
	#  manually from the global symbol table.  One will surely appreciate
	#  the la pseudoinstruction after this exercise.
	ori     $v0, $0, 4			
	la     $a0, welcome
	syscall

	# print out the initial values
	ori     $v0, $0, 4			
	la     $a0, update
	syscall

	# load and print out globalA
	ori     $v0, $0, 1			
	la	$t0, globalA
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalB
	ori     $v0, $0, 1			
	la	$t0, globalB
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalC
	ori     $v0, $0, 1			
	la	$t0, globalC
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalD
	ori     $v0, $0, 1			
	la	$t0, globalD
	lw	$a0, 0($t0)
	syscall

	# wipe regs clean
	add $0, $0, $v0
	add $0, $0, $v1
	add $0, $0, $a0
	add $0, $0, $a1
	add $0, $0, $a2
	add $0, $0, $a3
	add $0, $0, $t0
	add $0, $0, $t1
	add $0, $0, $t2
	add $0, $0, $t3
	add $0, $0, $t4
	add $0, $0, $t5
	add $0, $0, $t6
	add $0, $0, $t7
	add $0, $0, $s7
	add $0, $0, $s6
	add $0, $0, $s5
	add $0, $0, $s4
	add $0, $0, $s3
	add $0, $0, $s2
	add $0, $0, $s1
	add $0, $0, $s0


	# first function call
	jal storevalues


	# print the variables now
	# print out the values
	ori     $v0, $0, 4			
	la     $a0, update
	syscall
	# load and print out globalA
	ori     $v0, $0, 1			
	la	$t0, globalA
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalB
	ori     $v0, $0, 1			
	la	$t0, globalB
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalC
	ori     $v0, $0, 1			
	la	$t0, globalC
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalD
	ori     $v0, $0, 1			
	la	$t0, globalD
	lw	$a0, 0($t0)
	syscall

	# display prompt
	ori     $v0, $0, 4			
	la     $a0, enter
	syscall
	# read number
	ori	$v0, $0, 5			
	syscall
	# move to $a0
	add 	$s0, $v0, $0

	# display prompt
	ori     $v0, $0, 4			
	la     $a0, enter
	syscall
	# read number
	ori	$v0, $0, 5			
	syscall
	# move to $a0
	add 	$s1, $v0, $0

	# display prompt
	ori     $v0, $0, 4			
	la     $a0, enter
	syscall
	# read number
	ori	$v0, $0, 5			
	syscall
	# move to $a0
	add 	$s2, $v0, $0

	# display prompt
	ori     $v0, $0, 4			
	la     $a0, enter
	syscall
	# read number
	ori	$v0, $0, 5			
	syscall
	# move to $a0
	add 	$a3, $v0, $0

	add	$a0, $s0, $0
	add	$a1, $s1, $0
	add	$a2, $s2, $0

	# wipe regs clean
	add $0, $0, $v0
	add $0, $0, $v1
	add $0, $0, $t0
	add $0, $0, $t1
	add $0, $0, $t2
	add $0, $0, $t3
	add $0, $0, $t4
	add $0, $0, $t5
	add $0, $0, $t6
	add $0, $0, $t7
	add $0, $0, $s7
	add $0, $0, $s6
	add $0, $0, $s5
	add $0, $0, $s4
	add $0, $0, $s3
	add $0, $0, $s2
	add $0, $0, $s1
	add $0, $0, $s0


	jal storeregvalues

	# print out the values
	ori     $v0, $0, 4			
	la     $a0, update
	syscall
	# load and print out globalA
	ori     $v0, $0, 1			
	la	$t0, globalA
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalB
	ori     $v0, $0, 1			
	la	$t0, globalB
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalC
	ori     $v0, $0, 1			
	la	$t0, globalC
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalD
	ori     $v0, $0, 1			
	la	$t0, globalD
	lw	$a0, 0($t0)
	syscall


	# wipe the registers clean
	add $v0, $0, $0
	add $v1, $0, $0
	add $a0, $0, $0
	add $a1, $0, $0
	add $a2, $0, $0
	add $a3, $0, $0
	add $t0, $0, $0
	add $t1, $0, $0
	add $t2, $0, $0
	add $t3, $0, $0
	add $t4, $0, $0
	add $t5, $0, $0
	add $t6, $0, $0
	add $t7, $0, $0
	add $t8, $0, $0
	add $t9, $0, $0
	add $s0, $0, $0
	add $s1, $0, $0
	add $s2, $0, $0
	add $s3, $0, $0
	add $s4, $0, $0
	add $s5, $0, $0
	add $s6, $0, $0
	add $s7, $0, $0


	# second function call
	jal copyvalues

	# print out the values
	ori     $v0, $0, 4			
	la     $a0, update
	syscall
	# load and print out globalA
	ori     $v0, $0, 1			
	la	$t0, globalA
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalB
	ori     $v0, $0, 1			
	la	$t0, globalB
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalC
	ori     $v0, $0, 1			
	la	$t0, globalC
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalD
	ori     $v0, $0, 1			
	la	$t0, globalD
	lw	$a0, 0($t0)
	syscall

	# wipe regs clean
	add $0, $0, $v0
	add $0, $0, $v1
	add $0, $0, $a0
	add $0, $0, $a1
	add $0, $0, $a2
	add $0, $0, $a3
	add $0, $0, $t0
	add $0, $0, $t1
	add $0, $0, $t2
	add $0, $0, $t3
	add $0, $0, $t4
	add $0, $0, $t5
	add $0, $0, $t6
	add $0, $0, $t7
	add $0, $0, $s7
	add $0, $0, $s6
	add $0, $0, $s5
	add $0, $0, $s4
	add $0, $0, $s3
	add $0, $0, $s2
	add $0, $0, $s1
	add $0, $0, $s0

	# third function call
	jal operations

	# print out the values
	ori     $v0, $0, 4			
	la     $a0, update
	syscall
	# load and print out globalA
	ori     $v0, $0, 1			
	la	$t0, globalA
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalB
	ori     $v0, $0, 1			
	la	$t0, globalB
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalC
	ori     $v0, $0, 1			
	la	$t0, globalC
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out globalD
	ori     $v0, $0, 1			
	la	$t0, globalD
	lw	$a0, 0($t0)
	syscall

	# get some values for the array
	# display prompt
	ori     $v0, $0, 4			
	la     $a0, enter
	syscall
	# read number
	ori	$v0, $0, 5			
	syscall
	# move to $a0
	add 	$s0, $v0, $0

	# display prompt
	ori     $v0, $0, 4			
	la     $a0, enter
	syscall
	# read number
	ori	$v0, $0, 5			
	syscall
	# move to $a0
	add 	$a1, $v0, $0

	add 	$a0, $s0, $0

	# wipe regs clean
	add $0, $0, $v0
	add $0, $0, $v1
	add $0, $0, $a2
	add $0, $0, $a3
	add $0, $0, $t0
	add $0, $0, $t1
	add $0, $0, $t2
	add $0, $0, $t3
	add $0, $0, $t4
	add $0, $0, $t5
	add $0, $0, $t6
	add $0, $0, $t7
	add $0, $0, $s7
	add $0, $0, $s6
	add $0, $0, $s5
	add $0, $0, $s4
	add $0, $0, $s3
	add $0, $0, $s2
	add $0, $0, $s1
	add $0, $0, $s0
	


	jal arrays

	# print out the values
	ori     $v0, $0, 4			
	la     $a0, update
	syscall
	# load and print out globalA
	ori     $v0, $0, 1			
	la	$t0, globalA
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out myArray[0]
	ori     $v0, $0, 1			
	la	$t0, myArray
	lw	$a0, 0($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out myArray[1]
	ori     $v0, $0, 1			
	lw	$a0, 4($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out myArray[2]
	ori     $v0, $0, 1			
	lw	$a0, 8($t0)
	syscall
	# print a comma
	ori     $v0, $0, 4			
	la     $a0, comma
	syscall
	# load and print out myArray[3]
	ori     $v0, $0, 1			
	lw	$a0, 12($t0)
	syscall

	# wipe regs clean
	add $0, $0, $v0
	add $0, $0, $v1
	add $0, $0, $a0
	add $0, $0, $a1
	add $0, $0, $a2
	add $0, $0, $a3
	add $0, $0, $t0
	add $0, $0, $t1
	add $0, $0, $t2
	add $0, $0, $t3
	add $0, $0, $t4
	add $0, $0, $t5
	add $0, $0, $t6
	add $0, $0, $t7
	add $0, $0, $s7
	add $0, $0, $s6
	add $0, $0, $s5
	add $0, $0, $s4
	add $0, $0, $s3
	add $0, $0, $s2
	add $0, $0, $s1
	add $0, $0, $s0

	jal arraycalcs

	# print out the values
	ori     $v0, $0, 4			
	la     $a0, update
	syscall
	# load and print out globalA
	ori     $v0, $0, 1			
	la	$t0, globalA
	lw	$a0, 0($t0)
	syscall

	# Exit
ori     $v0, $0, 10
	syscall

