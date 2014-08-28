.text

# COPYFROMHERE

storevalues:

        # TODO: translate each line into mips
        # use only regs $v0-$v1, $t0-$t7, $a0-$a3
        # you may assume nothing about their starting values

        # globalA = 6
		la $t0, globalA
		ori $t1, $0, 6
		sw $t1, 0($t0)
		
        # globalB = 6
		la $t0, globalB
		ori $t1, $0, 6
		sw $t1, 0($t0)
		
        # globalC = 30
		la $t0, globalC
		ori $t1, $0, 30
		sw $t1, 0($t0)
		
        # globalD = 30
		la $t0, globalD
		ori $t1, $0, 30
		sw $t1, 0($t0)
		
        # do not remove this line
        jr $ra

storeregvalues:
        # TODO:  In this case, instead of storing constants,
        # we are storing register values into the variables
        # use only regs $v0-$v1, $t0-$t7,
        # you may assume that $a0-$a3 have already been set

        # globalA = $a3
		la $t0, globalA
		sw $a3, 0($t0)
		
        # globalB = $a2
		la $t0, globalB
		sw $a2, 0($t0)
		
        # globalC = $a1
		la $t0, globalC
		sw $a1, 0($t0)
		
        # globalD = $a0
		la $t0, globalD
		sw $a0, 0($t0)
		
        # do not remove this line
        jr $ra


copyvalues:
        # TODO: translate each line into mips
        # use only regs $v0-$v1, $t0-$t7, $a0-$a3
        # you may assume nothing about their starting values
		
        # globalA = globalB
		la $t0, globalA
		la $t1, globalB
		lw $t2, 0($t1)
		sw $t2, 0($t0)
		
        # globalD = globalC
		la $t0, globalD
		la $t1, globalC
		lw $t2, 0($t1)
		sw $t2, 0($t0)

        # do not remove this line
        jr $ra


operations:
        # TODO: translate each line into mips
        # use only regs $v0-$v1, $t0-$t7, $a0-$a3
        # you may assume nothing about their starting values


        # globalA = globalC + globalB
		la $t0, globalC
		lw $t2, 0($t0)
		la $t0, globalB
		lw $t1, 0($t0)
		add $t1, $t1, $t2
		la $t0, globalA
		sw $t1, 0($t0)
		
        # globalD = globalB + globalA
		la $t0, globalB
		lw $t2, 0($t0)
		la $t0, globalA
		lw $t1, 0($t0)
		add $t1, $t1, $t2
		la $t0, globalD
		sw $t1, 0($t0)
		
        # do not remove this line
        jr $ra


arrays:
        # TODO: translate each line into mips
        # use only regs $v0-$v1, $t0-$t7, $a2-$a3
        # you may assume nothing about their starting values
        # $a0 and $a1 have already been set for you

        #myArray[0] = $a0
		la $t0, myArray
		sw $a0, 0($t0)
		
        #myArray[1] = $a1
		sw $a1, 4($t0)
		
        #myArray[2] = globalD
		la $t1, globalD
		lw $t1, 0($t1)
		sw $t1, 8($t0)
		
        #globalB = myArray[3]
		lw $t2, 12($t0)
		la $t1, globalB
		sw $t2, 0($t1)

        # do not remove this line
        jr $ra

arraycalcs:
        # TODO: translate each line into mips
        # use only regs $v0-$v1, $t0-$t7, $a0-$a3
        # you may assume nothing about their starting values

        # globalB = myArray[0] + myArray[5]
		la $t0, myArray
		la $t1, globalB
		lw $t2, 0($t0)
		lw $t3, 20($t0)
		add $t2, $t2, $t3
		sw $t2, 0($t1)

        # do not remove this line
        jr $ra

