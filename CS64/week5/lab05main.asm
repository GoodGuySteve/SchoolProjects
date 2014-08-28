# xSpim Memory Demo Program

#  Data Area
.data

welcome:
   .asciiz "Welcome.  This program performs various functions on an array.\n"

value:
    .asciiz "\nValue: "

array:
    .asciiz "\nArray contents: "

result:
    .asciiz "\nResult: "

comma:
    .asciiz ", "

prompt:
    .asciiz "\nSelect a function (countvalues=0, findsecondlargest=1, countbs=2, exit=3): "

jTable:
    .word   F0, F1, F2, F3

arr:
    .word   0x00000000

len:
    .word   0x00000000

# myArray declaration allocates & initializes a 16 integer array,
myArray:
        .word 1 56 1 80 3 4 0 81 2 5 4 84 1 23 7

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

# count values
F0:
    #read in the index
    ori $v0, $0, 4
    la 	$a0, value
    syscall
    # read in number
    ori     $v0, $0, 5
    syscall


    add $a2, $v0, $0
    la $a0, myArray
    addi $a1, $0, 16
    
    jal countInstances

    # move and protect result
    add $s0, $v0, $0

    # print address
    ori     $v0, $0, 4
    la      $a0, result
    syscall
    ori     $v0, $0, 1
    or      $a0, $0, $s0
    syscall

    # Return to input
    j       input

# getvalue
F1:
    la $a0, myArray
    addi $a1, $0, 16
    
    jal secondLargest

    # move and protect result
    add $s0, $v0, $0

    # print address
    ori     $v0, $0, 4
    la      $a0, result
    syscall
    ori     $v0, $0, 1
    or      $a0, $0, $s0
    syscall

    # Return to input
    j       input

F2:
    la $a0, myArray
    addi $a1, $0, 16
    
    jal countBs

    # move and protect result
    add $s0, $v0, $0

    # print address
    ori     $v0, $0, 4
    la      $a0, result
    syscall
    ori     $v0, $0, 1
    or      $a0, $0, $s0
    syscall

    # Return to input
    j       input
F3:
    # Exit
    ori     $v0, $0, 10
    syscall

# COPYFROMHERE do not remove this line

countInstances:
	
	#$a0 is array, $a1 is length, $a2 is value
	#move stack pointer to allocate space
	#local variables: int i, int count
	addi $sp, $sp, -32

	or $t0, $0, $0 #$t0 = i
	or $t1, $0, $0 #$t1 = count	

countInstancesLoop: 
	slt $t2, $t0, $a1
	beq $t2, $0 endCountInstancesLoop
	sll $t3, $t0, 2 #$t3 = 4*i
	
	add $t3, $t3, $a0 
	lw $t4, 0($t3)
	bne $t4, $a2, notInstance
	addi $t1, $t1, 1
notInstance:
	addi $t0, $t0, 1
	j countInstancesLoop

endCountInstancesLoop:
	addi $sp, $sp, 32
	or $v0, $0, $t1
	jr $ra


secondLargest:

	#$a0 is the array address, $a1 is the length
	#local vars: int i($t0), int largest ($t8), int nextLargest($v0)
	addi $sp, $sp, -36

	or $t0, $0, $0 #$t0 = i = 0
	slti $t1, $a1, 2
	beq $t0, $0, lengthBiggerThan2

	ori $v0, $0, 1
	sll $v0, $v0, 31
	j secondLargestEnd		

lengthBiggerThan2:
	lw $t1, 0($a0)
	lw $t2, 4($a0)
	slt $t3, $t2, $t1
	beq $t3, $0, secondLargestFirstElse
	
	#array[0] > array[1]
	or $v0, $0, $t2 
	or $t9, $0, $t1
	j secondLargestFirstFi

secondLargestFirstElse:
	#array[0] <= array[1]
	or $v0, $0, $t1
	or $t9, $0, $t1

secondLargestFirstFi:
	ori $t0, $0, 2	

secondLargestLoop:
	slt $t1, $t0, $a1
	beq $t1, $0, secondLargestEnd
	
	sll $t2, $t0, 2
	add $t2, $t2, $a0
	lw $t2, 0($t2)

	slt $t1, $t2, $t9 
	bne $t1, $0, secondLargestSecondElse 	
		
	or $v0, $0, $t9
	or $t9, $0, $t2
	j secondLargestEndLoop

secondLargestSecondElse:
	#else if statement
	slt $t1, $v0, $t2
	beq $t1, $0, secondLargestEndLoop
	or $v0, $0, $t2
	j secondLargestEndLoop

secondLargestEndLoop:
	addi $t0, $t0, 1
	j secondLargestLoop

secondLargestEnd:
	addi $sp, $sp, 36
	jr $ra


countBs:
	#$t0 = i, $v0 = count
	or $t0, $0, $0
	or $v0, $0, $0

countBsLoop:
	#loop condition
	slt $t1, $t0, $a1
	beq $t1, $0, countBsEnd

	#$t2 = array[i]
	sll $t2, $t0, 2
	add $t2, $t2, $a0
	lw $t2, 0($t2)

	#if (val !< 80 && val < 89) count++
	slti $t1, $t2, 80
	bne $t1, $0, countBsEndLoop
	slti $t1, $t2, 89
	beq $t1, $0, countBsEndLoop
	addi $v0, $v0, 1

countBsEndLoop:
	addi $t0, $t0, 1
	j countBsLoop

countBsEnd:
	jr      $ra

