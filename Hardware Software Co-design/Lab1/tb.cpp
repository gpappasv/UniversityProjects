#include <iostream>
#include <stdlib.h>
#include <time.h>
#include "file.h"
#include <ap_int.h>
#include <chrono>
#define uint8 ap_uint<8>
#define uint32 ap_uint<32>

#define uint8 ap_uint<8>
#define uint32 ap_uint<32>

void mult_sw(uint8 A[n][m], uint8 B[m][p], uint32 AB[n][p])
{

for(int i = 0; i < n; i++){

	for(int j = 0; j < p; j++){

		int result = 0;
		for(int k = 0; k < m; k++){
			result += A[i][k]*B[k][j];

		}
		AB[i][j] = result;
	}
}

}

int main(int argc, char** argv)
{
uint8 testA[n][m],testB[m][p];
uint32 testABsw[n][p];
uint32 testABhw[n][p];


//RANDOM A,B INIT
for(int i = 0; i < n; i++)
	for(int j = 0; j < m; j++)
		testA[i][j] = rand() % 256;
		//testA[i][j]=i*j;

for(int i = 0; i < m; i++)
	for(int j = 0; j < p; j++)
		testB[i][j] = rand() % 256;
		//testB[i][j]=i*j+i-j;
//----------------

//sw
auto time0 = std::chrono::high_resolution_clock::now();
mult_sw(testA,testB,testABsw);
auto time1 = std::chrono::high_resolution_clock::now();
//hw
mult_hw(testA,testB,testABhw);

bool match = true;

std::cout << "SW result: ";
std::cout << "\n";
for(int i = 0; i < n; i++ ){
	for(int j = 0; j < p; j++){
		std::cout << testABsw[i][j];
		std::cout << "\t";
	}std::cout << "\n";}
*
std::cout << "HW result: ";
std::cout << "\n";
for(int i = 0; i < n; i++ ){
	for(int j = 0; j < p; j++){
		std::cout << testABhw[i][j];
		std::cout << "\t";
	}std::cout << "\n";}


for(int i = 0; i < n; i++ )
	for(int j = 0; j < p; j++){
		if(testABsw[i][j] !=testABhw[i][j]){
			std::cout << "ERROR: ["<<i<<"]["<<j<<"]\n";
			match = false;

		}
	}
if(match == true)std::cout <<"TEST PASSED\n";
else std::cout <<"TEST FAILED\n";

auto swTime = std::chrono::duration_cast<std::chrono::microseconds>( time1 - time0 ).count();
    std::cout << "SW mult time elapsed in us : " << swTime << std::endl;
}
