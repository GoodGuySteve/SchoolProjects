# xSpim Memory Demo Program

#  Data Area
.data

welcome:
    .asciiz "\nThis program allows you to create arrays, get and set values, and find the minimum value in the array\n"

value:
    .asciiz "\nValue: "

array:
    .asciiz "\nArray contents: "

size:
    .asciiz "\nSize (bytes): "

index:
    .asciiz "\nIndex: "

address:
    .asciiz "\nAddress: "

data:
    .asciiz "\nData: "

comma:
    .asciiz ", "

prompt:
    .asciiz "\nSelect a function (malloc=0, setvalue=1, getvalue=2, findmin=3, exit=4): "

jTable:
    .word   F0, F1, F2, F3, F4

arr:
    .word   0x00000000

len:
    .word   0x00000000

# myArray declaration allocates & initializes a 16 integer array,
myArray:
        .word 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3

#Text Area (i.e. instructions)
.text

main:
    # Display the welcome message;  lui and ori constants are obtained
    #  manually from the global symbol table.  One will surely appreciate
    #  the la pseudoinstruction after this exercise.
    ori     $v0, $0, 4          
    la     $a0, welcome
    syscall

input:
    # ask which function to test
    ori     $v0, $0, 4          
    la      $a0, prompt
    syscall
    # read number
    ori     $v0, $0, 5
    syscall
    add     $s7, $v0, $0
    
    # print out which they are going to call
    ori     $v0, $0, 1          
    add     $a0, $s7, $0
    syscall

    # Implment a jump table to get to the function call
    sltiu   $t7, $s7, 5     # check for valid input
    beq     $t7, $0, input  # invalid input goes to prompt
    sll     $s7, $s7, 2     # multiply by 4 to increment PC on word boundary
    la      $t7, jTable     # load jump table
    add     $t7, $t7, $s7   # add the offset
    lw      $t7, 0($t7)     # load the address
    j       $t7             # jump

# malloc
F0:
    # Get size
    ori     $v0, $0, 4
    la      $a0, size
    syscall
    ori     $v0, $0, 5
    syscall
    la      $t0, len
    sw      $v0, 0($t0)     # store size in memory
    or      $a0, $0, $v0

    # Malloc array
    jal     malloc
    la      $t0, arr
    sw      $v0, 0($t0)     # store array address
    add     $t0, $0, $v0    # don't clobber address

    # print address
    ori     $v0, $0, 4
    la      $a0, address
    syscall
    ori     $v0, $0, 1
    or      $a0, $0, $t0
    syscall

    # Return to input
    j       input

# setvalue
F1:
    # Get index
    ori     $v0, $0, 4
    la      $a0, index
    syscall
    ori     $v0, $0, 5
    syscall
    or      $a1, $0, $v0

    # Get value
    ori     $v0, $0, 4
    la      $a0, value
    syscall
    ori     $v0, $0, 5
    syscall
    or      $a2, $0, $v0

    # Set value
    la      $t0, arr
    lw      $a0, 0($t0)
    jal     setvalue

    j       input

# getvalue
F2:
    # Get index
    ori     $v0, $0, 4
    la      $a0, index
    syscall
    ori     $v0, $0, 5
    syscall
    or      $a1, $0, $v0

    # Get value
    la      $t0, arr
    lw      $a0, 0($t0)
    jal     getvalue
    or      $t0, $0, $v0

    # Print value
    ori     $v0, $0, 4
    la      $a0, value
    syscall
    ori     $v0, $0, 1
    or      $a0, $0, $t0
    syscall
    j       input

# findmin
F3:
    # Get min
    la      $t0, arr
    lw      $a0, 0($t0)
    la      $t0, len
    lw      $a1, 0($t0)
    srl     $a1, $a1, 2
    jal     findmin
    or      $t0, $0, $v0

    # Print value
    ori     $v0, $0, 4
    la      $a0, value
    syscall
    ori     $v0, $0, 1
    or      $a0, $0, $t0
    syscall
    j       input

F4:
    # Exit
    ori     $v0, $0, 10
    syscall


# COPYFROMHERE - DO NOT REMOVE THIS LINE

malloc:
    # Allocate the number of bytes in $a0 and return the address in $v0
    #la      $v0, myArray    # Temporary test code. Replace this

	ori $v0, $0, 9
	syscall


    # Do not remove this line
    jr      $ra

setvalue:
    # Set the value at index $a1 of array addressed in $a0 to the value in $a2; no return

	ori $t0, $0, 4
	mult $a1, $t0
	mflo $a1

	#la $a0, $a0
	add $t0, $a1, $a0
	sw $a2, 0($t0)

    # Do not remove this line
    jr      $ra

getvalue:
    # Get the value at index $a1 of array addressed in $a0; return the value in $v0

	ori $t0, $0, 4
	mult $a1, $t0
	mflo $a1

	add $t0, $a1, $a0
	lw $v0, 0($t0)

    # Do not remove this line
    jr      $ra

findmin:
    # Find the minimum value in the array at $a0 of length $a1; return the value in $v0
	lw $v0, 0($a0)
loop:
	beq $a1, $0, end
	lw $t0, 0($a0)

	#if ($t0 < $v0) replace the minimum
	slt $t1, $t0, $v0
	beq $t1, $0, notmin
	
	or $v0, $0, $t0
	
notmin:
	addi $a1, $a1, -1
	addi $a0, $a0, 4
	j loop
end:

    # Do not remove this line
    jr      $ra

