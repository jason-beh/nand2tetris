// Calculates R1 + R2 - R3 and stores the result in R0.
// (R0, R1, R2, R3 refer to RAM[0], RAM[1], RAM[2], and RAM[3], respectively.)

// R0 = R0 + R1
@R1
D=M
@R0
M=M+D

// R0 = R0 + R2
@R2
D=M
@R0
M=M+D

// R0 = R0 - R3
@R3
D=M
@R0
M=M-D