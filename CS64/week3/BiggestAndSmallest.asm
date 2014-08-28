# xSpim Demo Program
# 
#   CPE 315
#   fall 2001
#
# By: Dan Stearns
# Date:  
# Modifications: 
#	4/10/03 knico Tabbed code
#	4/10/03 knico Modified to use s registers instead of t registers
#           
#
# declare global so programmer can see actual addresses.

#  Data Area - allocate and initialize variables
.data

	# TODO: Complete these declarations / initializations
prompt:
	.asciiz "Enter the next number:\n"
biggest:
	.asciiz "Biggest:  "
smallest:
	.asciiz "\nSmallest:  "


#Text Area (i.e. instructions)
.text

main:

	# take the first number
	la $a0, prompt
	ori $v0, $0, 4
	syscall

	ori $v0, $0, 5
	syscall

	#store input in min($s0) and max($s1)
	or $s0, $0, $v0
	or $s1, $0, $v0
	
	ori $t0, $0, 3
	
	#begin loop - 3 iterations
loop:
	beq $t0, $0, endloop
	
	la $a0, prompt
	ori $v0, $0, 4
	syscall

	ori $v0, $0, 5
	syscall

	#check whether value is greater than max
	slt $t1, $v0, $s1
	bne $t1, $0, checkMin
greaterThanMax:
	#if input is greater than max
	or $s1, $v0, $0
	j end
checkMin:
	slt $t1, $s0, $t0
	bne $t1, $0, end
lessThanMin:
	#if the input is less than max
	or $s0, $v0, $0
	j end
end:
	addi $t0, $t0, -1
	j loop
endloop:
	#print min($s0) and max ($s1)
	la $a0, biggest
	ori $v0, $0, 4
	syscall
	
	or $a0, $s1, $0
	ori $v0, $0, 1
	syscall
	
	la $a0, smallest
	ori $v0, $0, 4
	syscall
	
	or $a0, $s0, $0
	ori $v0, $0, 1
	syscall
	
	#Exit
	ori $v0, $0, 10
	syscall
