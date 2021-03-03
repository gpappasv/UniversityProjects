#ifndef MULT_HW_H
#define MULT_HW_H

#define lm 8
#define ln 8
#define lp 8

#define m 2<<(lm-1)
#define n 2<<(ln-1)
#define p 2<<(lp-1)




void mult_hw(int A[n][m], int B[m][p], int AB[n][p]);

#endif
