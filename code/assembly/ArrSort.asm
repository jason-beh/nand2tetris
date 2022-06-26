// Sorts the array of length R2 whose first element is at RAM[R1] in ascending order in place. Sets R0 to True (-1) when complete.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

@swapped //16
M=1

// Check for empty
@R2
D=M

@END
D;JEQ

(WHILE)
    // Exit out of loop if swapped is false
    @swapped
    D=M

    @END
    D;JEQ

    // Set swapped to false
    @swapped
    M=0

    @R2
    D=M-1

    @R13
    M=D

    // Set i as starting index
    @R1
    D=M

    @R12
    M=D

    // Set lastIndex to the last element
    @R13
    M=M+D

    @LOOP
    0;JMP

(LOOP)
    // if i == lastindex, exit loop
    @R12
    D=M

    @R13
    D=D-M

    @WHILE
    D;JEQ

    // check if current ith number is greater than the next ith number
    @R12
    D=M

    @R1
    A=D
    D=M
    
    @R5 // temp store
    M=D

    @ISR5POSITIVE
    D;JGT

    @ISR5NEGATIVE
    D;JLT

(ISR5POSITIVE)
    @R12
    D=M

    @R1 // next number
    A=D+1
    D=M

    @SWAP // swap because prev number is positive and current number is negative
    D;JLT

    @R5
    D=M-D

    @SWAP // swap when prev - current > 0
    D;JGT

    // Increment i to get the next ith number
    @R12
    M=M+1

    // Iterate again
    @LOOP
    0;JMP

(ISR5NEGATIVE)
    @R12
    D=M

    @R1 // next number
    A=D+1
    D=M

    @INCREMENT // go back to loop when prev num is negative and current number is positive
    D;JGT

    @R5
    D=M-D

    @SWAP
    D;JGT

    // Increment i to get the next ith number
    @R12
    M=M+1

    // Iterate again
    @LOOP
    0;JMP

(INCREMENT)
    @R12
    M=M+1

    @LOOP
    0;JMP

(SWAP)
    @swapped
    M=1

    @R12
    D=M

    @R9 // temp next index
    M=D+1

    @R1
    A=D
    D=M

    @R10 // temp current number
    M=D

    @R9 // get next number
    A=M
    D=M

    @R11 // temp next number
    M=D
    D=M

    @R12
    A=M
    M=D

    @R10
    D=M

    @R9
    A=M
    M=D

    // Increment i to get the next ith number
    @R12
    M=M+1

    // Iterate again
    @LOOP
    0;JMP

(END)
    @R0
    M=-1

    @END
    0;JMP

//-----------------

// @R2
// D=M

// @END
// D;JEQ

// @n
// M=0

// @FIRSTELEMENT
// M=0

// @OUTERINDEX
// M=-1

// (OUTERLOOP)
//     @FIRSTELEMENT
//     D=M-D

//     @SWAPELEMENT
//     D;JNE

//     @OUTERINDEX
//     D=M+1
//     M=D

//     @n
//     M=D

//     @R2
//     D=D+1
//     D=D-M

//     @END
//     D;JEQ

//     @LOOP
//     0;JMP

// (SWAPELEMENT)
//     @R13
//     M=D

// (LOOP)
//     // if n == 1
//     @n
//     D=M

//     @FIRSTELEMENT
//     D=M-D
//     D;JEQ

//     // if n == R2, go to end
//     @n
//     D=M

//     @R2
//     D=D+1
//     D=D-M

//     @OUTERLOOP
//     D;JEQ

//     // get next pointer
//     @n
//     D=M+1
//     M=D

//     @R1
//     A=D+M
//     D=M

//     @ISR1POSITIVE
//     D;JGT

//     @ISR1NEGATIVE
//     D;JLT

// (FIRSTELEMENT)
//     // Set R0 as R1
//     @R1
//     A=D+M
//     D=M
    
//     @R13
//     M=D

//     @FIRSTELEMENT
//     M=1
//     M=-M

//     @LOOP
//     0;JMP

// (ISR1POSITIVE)
//     @R13
//     D=M

//     // since R1(positive) is bigger than R0(negative), we do nothing
//     @LOOP
//     D;JLT

//     // set R0 to min if it is smaller
//     @n
//     D=M

//     @R1
//     A=D+M
//     D=M

//     @R13
//     D=D-M

//     @SETMINIMUM
//     D;JLT

//     @LOOP
//     0;JMP

// (ISR1NEGATIVE)
//     @R13
//     D=M

//     // Swap since R1 is negative and R0 is positive
//     @SWAP
//     D;JGT

//     // set R0 to min if it is smaller (when both are negative or positive)
//     @n
//     D=M

//     @R1
//     A=D+M
//     D=M

//     @R13
//     D=D-M

//     @SETMINIMUM
//     D;JLT

//     @LOOP
//     0;JMP

// (SWAP)
//     // @R5 // R5 stores current minimum
//     // D=M

//     @n
//     D=M

//     @R1
//     A=D+M
//     D=M

//     @R13
//     M=D

//     @LOOP
//     0;JMP

// (SETMINIMUM)
//     @n
//     D=M

//     @R1
//     A=D+M
//     D=M

//     @R13
//     M=D

//     @LOOP
//     0;JMP

// (END)
//     @R0
//     M=-1

//     @END
//     0;JMP