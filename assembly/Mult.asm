// This file is based on part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: Mult.asm

// Multiplies R1 and R2 and stores the result in R0.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// -5 * -3 = 15
// -5 * 3 = -15
// 5 * -3 = -15
// 5 * 3 = 15

@R0
D=0

// check for zeros
@R1
D=M

@END
D;JEQ

@R2
D=M

@END
D;JEQ

@R2
D=M

@SETR2POSITIVE
D;JLT

@LOOP
0;JMP

(SETR2POSITIVE)
    D=-D
    @R2
    M=D

    @R1
    D=M
    D=-D
    M=D

    @LOOP
    0;JMP

(LOOP)
    @R1
    D=M

    @R0
    M=M+D

    @R2
    D=M
    M=D-1
    D=M
    
    @LOOP
    D;JGT

    @END
    D;JEQ

(END)
    @END
    0;JMP