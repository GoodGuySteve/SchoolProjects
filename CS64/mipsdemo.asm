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
# Notes:
#        The constants in the address loading sequence (lui/ori)
#        are entered from the global symbol table because...
#        this assembler lacks assembly-time arithmetic operations
#
#

# declare global so programmer can see actual addresses.
.globl welcome
.globl prompt
.globl sumText

#  Data Area
.data

welcome:
	.asciiz " This program adds two numbers \n\n"

prompt:
	.asciiz " Enter an integer: "

sumText: 
	.asciiz " \n Sum = "

# MyArray declaration allocates a 9 integer array, 
# initialized to the values listed below

MyArray:
        .word 0x20040002 0x20080001 0x200b0001 0x8b502a 
        .word 0x15400003 0x1084020 0x20840000 0x800fffa 
        .word 0x1001020 

#Text Area (i.e. instructions)
.text

main:

	# Display the welcome message;  lui and ori constants are obtained
	#  manually from the global symbol table.  One will surely appreciate
	#  the la pseudoinstruction after this exercise.
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0
	syscall

	# Display prompt
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x22
	syscall

	# Read 1st integer
	ori     $v0, $0, 5
	syscall

	# Clear $s0 for the sum
	ori     $s0, $0, 0	

	# Add 1st integer to sum 
	# (could have put 1st integer into $s0 and skipped clearing it above)
	addu    $s0, $v0, $s0
	
	# Display prompt
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x22
	syscall

	# Read 2nd integer 
	ori	$v0, $0, 5			
	syscall
	# $v0 now has the value of the second integer
	
	# Add 2nd integer to sum
	addu    $s0, $v0, $s0 

	# Display the sum text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x36
	syscall
	
	# Display the sum
	ori     $v0, $0, 1			
	add 	$a0, $s0, $0	 
	syscall
	
	# Exit
	ori     $v0, $0, 10
	syscall