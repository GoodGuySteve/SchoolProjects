# xSpim Memory Demo Program

#  Data Area
.data

welcome:
    .asciiz "\nThis program allows you to create arrays, get and set values, and find the maximum value in the array\n"

value:
    .asciiz "\nValue: "

array:
    .asciiz "\nArray contents: "

size:
    .asciiz "\nSize: "

index:
    .asciiz "\nIndex: "

address:
    .asciiz "\nAddress: "

data:
    .asciiz "\nData: "

comma:
    .asciiz ", "

prompt:
    .asciiz "\nSelect a function (malloc=0, getvalue=1, setvalue=2, findmax=3, exit=4): "

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
    add     $a0, $v0, $0
    la      $t0, len
    sw      $a0, 0($t0)     # store size in memory

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

# getvalue
F1:
    # Get address
    ori     $v0, $0, 4
    la      $a0, address
    syscall
    ori     $v0, $0, 5
    syscall
    or      $t0, $0, $v0     # protect from $a0

    # Get index
    ori     $v0, $0, 4
    la      $a0, index
    syscall
    ori     $v0, $0, 5
    syscall
    or      $a1, $0, $v0

    # Get value
    or      $a0, $0, $t0    # put address in $a0
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

# setvalue
F2:
    # Get address
    ori     $v0, $0, 4
    la      $a0, address
    syscall
    ori     $v0, $0, 5
    syscall
    or      $t0, $0, $v0

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
    or      $a0, $0, $t0    # put address in $a0
    jal     setvalue

    j       input

# findmax
F3:
    # Get address
    ori     $v0, $0, 4
    la      $a0, address
    syscall
    ori     $v0, $0, 5
    syscall
    or      $t0, $0, $v0     # protect from $a0

    # Get size
    ori     $v0, $0, 4
    la      $a0, size
    syscall
    ori     $v0, $0, 5
    syscall
    or      $a1, $0, $v0

    # Get max
    or      $a0, $0, $t0    # put address in $a0
    jal     findmax
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

# COPYFROMHERE do not remove this line
