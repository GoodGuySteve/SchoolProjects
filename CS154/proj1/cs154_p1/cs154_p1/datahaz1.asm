addi $3 $0 1000
addi $4 $0 200
sub $5 $3 $4
sw $5 0($0)
lw $7 0($0)
addi $8 $7 11
