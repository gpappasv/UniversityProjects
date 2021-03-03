#include <iostream>
#include <vector>
#define lm 8
#define ln 8
#define lp 8

#define m 2<<(lm-1)
#define n 2<<(ln-1)
#define p 2<<(lp-1)

//void mult_hw(int *A, int *B, int *AB);

const int nn=n;
const int pp=p;
const int mm=m;


extern "C" {
void copy1(int BA[n][m],int *A1 ){
    //BRAM---------

    for(int i = 0; i < n; i++){
    #pragma HLS loop_tripcount min=nn max=nn
        for(int j = 0; j < m; j++){
        #pragma HLS loop_tripcount min=mm max=mm
        #pragma HLS UNROLL factor=2
        #pragma HLS PIPELINE II=1

            BA[i][j]=A1[i*m+j];

        }
    }//BRAM--------


}
void copy2(int BB[m][p],int *B1){
    //BRAM---------

    for(int i = 0; i < m; i++){
    #pragma HLS loop_tripcount min=mm max=mm
        for(int j = 0; j < p; j++){
        #pragma HLS loop_tripcount min=pp max=pp
        #pragma HLS UNROLL factor=2
        #pragma HLS PIPELINE II=1

BB[i][j]=B1[i*p+j];}

    }//BRAM--------


}
void mult_hw(int *A, int *B, int *AB)//
{


int BRAM_A[n][m];
int BRAM_B[m][p];


#pragma HLS ARRAY_PARTITION variable=BRAM_A complete dim=2
#pragma HLS ARRAY_PARTITION variable=BRAM_B complete dim=1

#pragma HLS INTERFACE m_axi port = A offset = slave bundle = gmem
#pragma HLS INTERFACE m_axi port = B offset = slave bundle = gmem
#pragma HLS INTERFACE m_axi port = AB offset = slave bundle = gmem
#pragma HLS INTERFACE s_axilite port = A bundle = control
#pragma HLS INTERFACE s_axilite port = B bundle = control
#pragma HLS INTERFACE s_axilite port = AB bundle = control
//#pragma HLS INTERFACE s_axilite port = size bundle = control
#pragma HLS INTERFACE s_axilite port = return bundle = control

//#pragma HLS ARRAY_RESHAPE variable=BRAM_A complete dim=2
//#pragma HLS ARRAY_RESHAPE variable=BRAM_B  complete  dim=1
//#pragma HLS ARRAY_RESHAPE variable=BRAM_AB complete dim=2

#pragma HLS DATAFLOW

copy1(BRAM_A,A);

copy2(BRAM_B,B);



for(int i = 0; i < n; i++){
#pragma HLS loop_tripcount min=nn max=nn
    for(int j = 0; j < p; j++){
#pragma HLS loop_tripcount min=pp max=pp

        int result = 0;
#pragma HLS UNROLL factor=2
#pragma HLS PIPELINE II=1
        for(int k = 0; k < m; k++){
#pragma HLS loop_tripcount min=mm max=mm

        result+= BRAM_A[i][k]*BRAM_B[k][j];
        }
        AB[i*p+j] = result;

    }
}

}
}