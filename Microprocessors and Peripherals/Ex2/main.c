#include <platform.h>
#include <gpio.h>
#include "delay.h"
#include "lcd.h"
#include "switches.h"
#include "leds.h"

#define MODE// leds will turn on, wait until user presses the button to turn off (response time is the time needed for the user to turn leds off) 

volatile int signal=0;//changes from 0 to 1 when we get an interrupt (when the button is pressed)
static int runCount=0;//to count our 5 response tests
static long responseTime=0;
static long rtArray[5]={0,0,0,0,0};//array with size=5 to store our 5 response time results
static long meanResponseTime=0;
	
void button_press_isr(int sources){//my interrupt handler
gpio_set(P_DBG_ISR, 1);
if ((sources << GET_PIN_INDEX(P_SW)) & (1 << GET_PIN_INDEX(P_SW))) {	
signal=1;//when the button is pressed, signal goes from 0 to 1

}
gpio_set(P_DBG_ISR, 0);
}	

__asm void saveToMemory(long meanTime){//function to write the mean response time from the 5 tests into memory
	
	MOV32 r2, #0x20000451//load r2 with a memory address
	STR r0, [r2]//store the value we want to that memory address
	BX lr//return to main
	
}


int main(void) {
	//initialize
	switches_init();
	leds_init();
	gpio_set_trigger(P_SW,Rising);
	gpio_set_callback(P_SW,button_press_isr);
	//----------
	
	signal=0;//gpio_set_callback triggers the signal so I reset it to 0
	
	gpio_set_mode(P_DBG_ISR,Output);
	gpio_set_mode(P_DBG_MAIN,Output);
	
	__enable_irq();
	
	int i=0;
	int delay;
	
#ifndef MODE
	leds_set(1,0,0);//In MODE 0, led needs to start from On. In MODE 1, we firstly need it in off state (and wait it to be on to respond with the switch)
#endif
	while (1) {//My "main" while which "restarts" the program
		
		runCount=0;//To start the next round of response time tests (runCount counts to 5)
		delay_ms(5000);//a delay for us to get ready
#ifndef MODE
		leds_set(0,0,0);
#endif
#ifdef MODE	
		leds_set(1,0,0);//turns led on for the 1st out of 5 tests to begin
#endif
		
		while(runCount<5){//////////////// during this while we run 5 response time tests
			gpio_toggle(P_DBG_MAIN);//????
			
			if(signal){//if signal gets the value 1, it means we had an interrupt, so we need to execute the instructions inside the "if"
			
			rtArray[runCount]=responseTime;//save the value of responseTime variable
									
			responseTime=0;//"restart" our responseTime variable		
			runCount++;//counts from 0 to 4 (5 rounds of our response time test)
			signal=0;
			
#ifndef MODE
			leds_set(1,0,0);
#endif
#ifdef MODE
			leds_set(0,0,0);//turn led off
#endif
			
			delay=2000+(rand()%3000);//delay ranges from 2000 to 5000ms to make our led turn on at random times (so that user doesnt expect it)
			delay_ms(delay);
			
#ifndef MODE	
			if(runCount<5)leds_set(0,0,0);
#endif
#ifdef MODE
			if(runCount<5)leds_set(1,0,0);
#endif
			
			
			}

			responseTime++;
			delay_cycles(10000);
					
		}///////////////////////////////
		
		for(i=0;i<5;i++){
		meanResponseTime+=(rtArray[i]);//meanResponseTime now holds the sum of the 5 response times we got (actually it holds the sum of the values of responseTime variable) 
		}
		
		meanResponseTime/=5;//devide the sum of 5 response times by 5 to get the mean response time per try
		meanResponseTime*=10000;//if meanResponseTime = 100, it means delay_cycles(10000) was executed 100 times, so meanResponseTime*10.000=100*10.000=1.000.000 cycles until button was pressed.
		meanResponseTime*=0.0000625;//16Mhz clock => 1000*(1/16.000.000)=0.0000625ms per cycle => meanResponseTime*0.0000625=(cyclesElapsed*0.0000625) milliseconds -> (response time in milliseconds)
		
		saveToMemory(meanResponseTime);
		delay_ms(5000);//Just a delay until the next round of 5 tests begins 
		meanResponseTime=0;
	}
	
	
	
	}
	
	
	
	
	
	
	


// *******************************ARM University Program Copyright © ARM Ltd 2016*************************************   