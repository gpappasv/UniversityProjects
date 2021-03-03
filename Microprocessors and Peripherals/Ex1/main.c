#include <stdio.h>
#include <stdlib.h>
#include <string.h>

__asm int check(char *ptr)
	{

	PUSH {r4-r10}
  MOV r1,#0 // r1 will count the size of string (it will end up having the value r1=stringSize)

stringSize //loop to calculate the string size
  LDRB r2, [r0] //load of each element of the memory address that r0 points, to r2
  ADD r0,r0,#1 //r0++ to point to the next string element
  CMP r2, #0  //if r2 received the value 0, it means we reached the end of the string
	ADDNE r1,r1,#1 //if r2 didnt receive the value 0, the counter (r1) increments by 1 (if(r2!=0), r1++)
	BNE stringSize //return at the begining of the loop, if r2!=0 (which means we didnt reach the end of the string)

	SUB r0,r0,r1  
	SUB r0,r0,#1 //r0 now points at the first element of the string again (r0=r0-stringSize-1)
	
	MOV r5, r1 //r5 gets the value of r1, r1=stringSize, so r5=stringSize
	MOV r4, #0 // r4=0 (metrhths pou tha ftasei sto r5=size) (metrhths gia to loop parakatw)\r4=0 (r4 is the counter to help me stop the following loop when r4=stringSize)
	MOV r9, #0 // r9 will count how many comparisons will end in a match -> if r9 reaches the value of stringSize, it means our string is palindrome
	MOV r8, r5//r8=r5=size ->o r8 will be used as the offset of r0
	ADD r8,r8, #-1//correction of first offset value (we first need and offset=stringSize-1)

loop
	LDRB r6, [r0]//r6 stores the element of the string that r0 points at right now
	ADDS r0,r8//now I want r0 to point at the element that must be compared to the one that I just stored to r6 (offset of r0)
	LDRB r7, [r0] //r7 stores the element of the string that now (temporarily) r0 points at. r7 has the letter that will be compared with the one on r6
	SUBS r0, r8 //r0 now points at the element of the string that was pointing before the offset
	ADD r8,r8, #-2 //r8=offset, now (offset=offset-2) -> (offset correction)
	CMP r6, r7//is r6==r7? At this step I compare the letters of the string that I stored at r6 and r7, to check if i have a match
	ADDEQ r9, r9, #1 //if r6==r7, r9 counts one match
	ADDS r0, #1//increment ptr pointer (now r0 points at the next element of the string)
	ADD r4,r4, #1//r4=r4+1// my loop counter
	CMP r4,r5 //if(r4==r5)
	BNE loop


	CMP r9,r5//check if r9==r5==size. If r9==size, it means that every comparison happened on the loop, gave me a match, so string is palindrome
	MOVEQ r0, #1//if r9==r5, r0=1 ----  r0 gets value 1 or 0 (the result if the string is palindrome or not-> so now the function check() returns 0 or 1 to my main, depending on the string)
	MOVNE r0, #0//if r9==r5, r0=0 ----  r0 gets value 1 or 0 (the result if the string is palindrome or not-> so now the function check() returns 0 or 1 to my main, depending on the string)
	
	MOV32 r3, #536890000 //store a memory location at r3 register
	STRB r0, [r3]//at the memory location that r3 stored, I store the value of r0
	//LDRB r5, [r3]//testing an mphke swsta h timh sth dieuthinsi tou r3
	
	POP {r4-r10}

	BX lr // return to main


	}





int main()
{   //----variables
    char sequence[]="HeH";//string define
    char * seqPtr;
		int returnOfFunction;
    //-----------
		seqPtr=sequence;
		returnOfFunction=check(seqPtr); 
    
	return 0;
}






















