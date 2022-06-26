// // Finds the smallest element in the array of length R2 whose first element is at RAM[R1] and stores the result in R0.
// // (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// // Check for empty
// @R2
// D=M

// @END
// D;JEQ

// @n
// M=0

// // Set R0 to R1
// @R1
// D=M

// @R0
// M=D

// (LOOP)
//     // if n == R2, go to end
//     @n
//     D=M

//     @R2
//     D=D+1
//     D=D-M

//     @END
//     D;JEQ

//     // get next pointer
//     @n
//     D=M+1
//     M=D

//     @R1
//     A=D+M
//     D=M

//     // set R0 to min if it is smaller
//     @R0
//     D=D-M

//     @SETMINIMUM
//     D;JLT

//     @LOOP
//     0;JMP

// (SETMINIMUM)
//     @n
//     D=M

//     @R1
//     A=D+M
//     D=M

//     @R0
//     M=D

//     @LOOP
//     0;JMP

// (END)
//     @END
//     0;JMP

// ------------------

// Finds the smallest element in the array of length R2 whose first element is at RAM[R1] and stores the result in R0.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Check for empty
@R2
D=M

@END
D;JEQ

@n
M=0

@FIRSTELEMENT
M=0

(LOOP)
    // if n == 1
    @n
    D=M

    @FIRSTELEMENT
    D=M-D
    D;JEQ

    // if n == R2, go to end
    @n
    D=M

    @R2
    D=D+1
    D=D-M

    @END
    D;JEQ

    // get next pointer
    @n
    D=M+1
    M=D

    @R1
    A=D+M
    D=M

    @ISR1POSITIVE
    D;JGT

    @ISR1NEGATIVE
    D;JLT

(FIRSTELEMENT)
    // Set R0 as R1
    @R1
    A=D+M
    D=M
    
    @R0
    M=D

    @FIRSTELEMENT
    M=1
    M=-M

    @LOOP
    0;JMP

(ISR1POSITIVE)
    @R0
    D=M

    // since R1(positive) is bigger than R0(negative), we do nothing
    @LOOP
    D;JLT

    // set R0 to min if it is smaller
    @n
    D=M

    @R1
    A=D+M
    D=M

    @R0
    D=D-M

    @SETMINIMUM
    D;JLT

    @LOOP
    0;JMP

(ISR1NEGATIVE)
    @R0
    D=M

    // Swap since R1 is negative and R0 is positive
    @SWAP
    D;JGT

    // set R0 to min if it is smaller (when both are negative or positive)
    @n
    D=M

    @R1
    A=D+M
    D=M

    @R0
    D=D-M

    @SETMINIMUM
    D;JLT

    @LOOP
    0;JMP

(SWAP)
    // @R5 // R5 stores current minimum
    // D=M

    @n
    D=M

    @R1
    A=D+M
    D=M

    @R0
    M=D


    @LOOP
    0;JMP


(SETMINIMUM)
    @n
    D=M

    @R1
    A=D+M
    D=M

    @R0
    M=D

    @LOOP
    0;JMP

(END)
    @END
    0;JMP