
#include <stdio.h>

int main(){
int i,fact;
fact=1;

printf("Print factorial of numbers 1 to 10: \n");

for(i=1;i<=10;i++){

fact=i*fact;
printf("%d! equals: %d\n",i,fact);

}

return 0;

}
