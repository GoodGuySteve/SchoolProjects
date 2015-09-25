top: addi $16 $0 1
addi $17 $0 -1
addi $18 $0 -1
addi $19 $0 -1
addi $20 $0 1
addi $21 $0 -1
addi $8 $0 3
sw $8 12($0)
addi $9 $0 3
lw $10 12($0)
beq $10 $9 skip1
addi $16 $0 -1
skip1: beq $10 $9 skip2
addi $2 $0 15
skip2: beq $2 $20 top
