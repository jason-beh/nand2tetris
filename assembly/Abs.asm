// Calculates the absolute value of R1 and stores the result in R0.
// (R0, R1 refer to RAM[0], and RAM[1], respectively.)

@R1
D=M

@POSITIVE
D;JGT

D = -D
@R0
M=D

@END
0;JMP

(POSITIVE)
    @R0
    M=D

(END)
    @END
    0;JMP