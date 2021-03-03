#include <platform.h>
#include <gpio.h>
#include "delay.h"
#include "lcd.h"
#include "switches.h"
#include "leds.h"
#include <stdio.h>
#include "timer.h"
#include "stm32f4xx_hal.h" 
 
#define echopin PA_1
#define triggerpin PA_0
#define tempPin PC_0
#define ledHOT PA_6
#define ledCOLD PA_7
#define ledBOARD PA_5
#define HOTTEMP 26
#define COLDTEMP 22

volatile int countDistanceFlag=0;//is set to 1 every 100ms and allows sendPulse() to be activated

TIM_HandleTypeDef  TIM_Handle,T1;
uint32_t value;

float tempArray[24]={0};
volatile int tempFlag=0;//gets 1 when 5 seconds pass (TIM4 interrupt sets it to 1)
volatile float meanTemp2min=0;//keeps the mean temp from the last 2mins
volatile int counts24=0;//is incremented every 5 seconds -> reaches 23 -> 23 means 2 mins have passed
volatile int tenSecFlag=0;
volatile int tenSec=10;
int erasedMean=0;//permission to write the mean temp message
char lastTempMessage[20];
int alreadyWritten=0;
void timeEchoIR(int sources){

	
}
//*********TIMERS INIT**********
void initTIM4(){

		__TIM4_CLK_ENABLE();		
		TIM_Handle.Init.Prescaler = 1599;//clock is 16Mhz-> clock=100.000Hz -> time per tick 1/100.000
		TIM_Handle.Init.CounterMode = TIM_COUNTERMODE_UP;
		TIM_Handle.Init.Period = 50000;//every 5 sec -> interrupt
		TIM_Handle.Instance=TIM4;
		HAL_TIM_Base_Init(&TIM_Handle);
		HAL_TIM_Base_Start_IT(&TIM_Handle);
		HAL_NVIC_SetPriority(TIM4_IRQn,0,1);
		HAL_NVIC_EnableIRQ(TIM4_IRQn);

}

void initTIM3(){//for 300ms
__TIM3_CLK_ENABLE();
		T1.Init.Prescaler = 1599;
		T1.Init.CounterMode = TIM_COUNTERMODE_UP;
		T1.Init.Period = 3000; //(300ms=3000)
		T1.Instance=TIM3;
		HAL_TIM_Base_Init(&T1);
		HAL_TIM_Base_Start_IT(&T1);
		HAL_NVIC_SetPriority(TIM3_IRQn,0,1);
		HAL_NVIC_EnableIRQ(TIM3_IRQn);

}
//******************************

//*********SEND WAVE - SONIC SENSOR**********
void sendPulse(){//it needs 100ms delay on each call
    gpio_set(triggerpin,0);
		delay_us(2);
		gpio_set(triggerpin,1);
    delay_us(10);
    gpio_set(triggerpin,0);
			
}
 //******************************************

//*********TEMP SENSOR INITIALIZATION*********
int tempInit(void){//driver
    int response=0;
    gpio_set_mode(tempPin,Output);
    gpio_set(tempPin,0);
    delay_us(480);
    gpio_set_mode(tempPin,Input);
    delay_us(80);
    if(!gpio_get(tempPin))response=1;
    else response=-1;
    delay_us(400);
   
return response;
 
}

void tempWrite(uint8_t data){//driver
    gpio_set_mode(tempPin,Output);
    int i=0;
    for(i=0;i<8;i++){
    if((data &(1<<i))!=0){
    gpio_set_mode(tempPin,Output);
    gpio_set(tempPin,0);
    delay_us(1);
    gpio_set_mode(tempPin,Input);
    delay_us(60);
   
    }
    else {
    gpio_set_mode(tempPin,Output);
    gpio_set(tempPin,0);
    delay_us(60);
    gpio_set_mode(tempPin,Input);
    }
    }
   
}
 
uint8_t tempRead(void){//driver
   
    uint8_t value=0;
    gpio_set_mode(tempPin,Input);
   
    int i=0;
   
    for(i=0;i<8;i++){
    gpio_set_mode(tempPin,Output);
    gpio_set(tempPin,0);
    delay_us(2);
    gpio_set_mode(tempPin,Input);
       
    if(gpio_get(tempPin))value|=1<<i;
   
    delay_us(60);
       
}
return value;
}
 
void displayTemp(float temp){//just to display temp
 
    char str[20]={0};
    lcd_clear();
    lcd_set_cursor(0,0);
    sprintf(str,"TEMP: %.2f C",temp);
    lcd_print(str);
}
 
float tempSensor(){//reads temp and prints it (calling displayTemp)
    int tempReturn;//gets the tempInit return value ->its about our driver
    uint8_t tempByte1,tempByte2;
    uint16_t TEMP;
    float temperature;
   
    tempReturn=tempInit();
    delay_ms(1);
    tempWrite(0xCC);
    tempWrite(0x44);
    delay_ms(800);//800
    tempReturn=tempInit();
    delay_ms(1);
    tempWrite(0xCC);
    tempWrite(0xBE);   
    tempByte1=tempRead();
    tempByte2=tempRead();
       
    if(!tenSecFlag)lcd_clear();
    if(!tenSecFlag)lcd_set_cursor(0,0);   
    TEMP=(tempByte2<<8)|(tempByte1);
		leds_set(0,0,0);
    temperature=(float)TEMP/16;
    
		sprintf(lastTempMessage,"");
		
		if(temperature>=HOTTEMP)
			{//*****when temp is high (HOTTEMP defines high temperature)******
			
			if(!tenSecFlag)lcd_clear();
			if(!tenSecFlag)lcd_set_cursor(0,0);
			gpio_set(ledHOT,1);//Red led turns on when temperature is higher than HOTTEMP
			if(temperature>(HOTTEMP+3))gpio_set(ledBOARD,1);//if more than HOTTEMP+3 turn board led on
			if(!tenSecFlag){//flag to prevent overwriting the -mean temperature- message. When tenSecFlag is high, we are on the 10 sec window that the mean temperature value is displayed.
			lcd_print("      HOT");//prints "HOT" when temperature is higher than HOTTEMP
			sprintf(lastTempMessage,"      HOT");
			}
			//*****************************************************************
			}  
		if(temperature<=COLDTEMP)
			{ //*****when temp is low (COLDTEMP defines low temperature)********
			if(!tenSecFlag)lcd_clear();
			if(!tenSecFlag)lcd_set_cursor(0,0);
			gpio_set(ledCOLD,1);//if less than COLDTEMP turn led on
			if(!tenSecFlag){//flag to prevent overwriting the -mean temperature- message. When tenSecFlag is high, we are on the 10 sec window that the mean temperature value is displayed.
			lcd_print("      COLD");//prints "COLD" when temperature is lower than HOTTEMP
			sprintf(lastTempMessage,"      COLD");
			}
			}//*****************************************************************  
    
		return temperature;
}
//*********************************************

//******TIMER HANDLERS**********
void TIM4_IRQHandler(){//5sec
	HAL_TIM_IRQHandler(&TIM_Handle);
	tempFlag=1;//now we can take a new temp measurement

}
void TIM3_IRQHandler(){//its for 100ms delay on every sendPulse()
	HAL_TIM_IRQHandler(&T1);
	countDistanceFlag=1;//sets the flag, now we are allowed to measure distance

}
void tmr_isr(){
tenSec--;
if(tenSec==0){
	tenSec=10;
	tenSecFlag=0;
	timer_disable();

}	
}
//******************************
void printMeanValue(){

		char str[20]={0};
		erasedMean=0;
		lcd_clear();
		lcd_set_cursor(0,0);
		sprintf(str,"Mean is: %.2f",meanTemp2min);
		lcd_print(str);
}

void initStuff(){
		initTIM4();
		initTIM3();//for distance -> every 300ms
		switches_init();
    leds_init();
		tempInit();
    lcd_init();
		timer_init(1000000);  
		timer_set_callback(tmr_isr);
    gpio_set_mode(echopin,PullDown);
    gpio_set_mode(triggerpin,Output);
    gpio_set(triggerpin,0);
    gpio_set_callback(echopin,timeEchoIR);
    gpio_set_trigger(echopin,Rising);
	
		gpio_set(ledBOARD,1);
		gpio_set(ledHOT,1);
		gpio_set(ledCOLD,1);
		__enable_irq();
		int erasedMean=0;
		
		//enableIRQ triggers those variables ->
		tenSecFlag=0;
		tempFlag=0;
		countDistanceFlag=0;
		tenSec=10;
    //enableIRQ triggers those variables->
		lcd_print("Im your smart   thermometer");
		}

int main(void) {
		
		int i=0;
		int forVariable=0;//just to be used in for loops
		initStuff();
		
		char str[20]={0}; 
		char strD[20]={0};
		
		
		
		while(1){
				//**************DISTANCE SENSOR***************
				if(countDistanceFlag){
					
					i=0;//keeps the distance result
					countDistanceFlag=0;//resets the flag to 0
					sendPulse();//supplies trigger pin with a 10uS pulse
					
					while(!gpio_get(echopin)){//echo pin stays at 0 while waiting for the sonic wave to return
					}
						
					while(gpio_get(echopin)){//echo pin is high (1) while it detects the ultrasonic wave
						i++;
					}
					i/=31;//Dividing i with 31, turns the i value in centimeters / 31 was found after testing
					if(i<=7){//if proximity <7cm
					alreadyWritten=1;//flag to prevent out program from updating the LCD at a fast rate
					displayTemp(tempArray[(counts24-1)]);//show the current temp
					
					if(meanTemp2min==0)sprintf(strD,"    MEAN IS: NULL");//also show the mean value (when Mean is not calculated yet)
					else sprintf(strD,"   MEAN IS: %.2f",meanTemp2min);//also show the mean value
					lcd_print(strD);//(printing of the mean value)
					erasedMean=1;
					
					}
					if(i>7){
						if(alreadyWritten)
							if(!tenSecFlag){
					lcd_clear();
					lcd_set_cursor(0,0);
					lcd_print(lastTempMessage);
					alreadyWritten=0;
					}
					
					}
				}
				//**************DISTANCE SENSOR***************
				
				//***************TEMP SENSOR***************
				if (tempFlag){//tempFlag gets 1 every 5 seconds
				erasedMean=0;
				tempFlag=0;//reset it
					
				if(counts24<24)
					{//if during the 2min window
				tempArray[counts24]=tempSensor();//get temp in the array
					}
				else if(counts24==24)
					{//if we counted 24 temps ->2minutes passed ...
				counts24=0;//counter resets to zero
				meanTemp2min=0;//mean value resets to zero->it will get new value soon
				
				for(forVariable=0;forVariable<24;forVariable++)//this for loop adds all the 24 temp measurements in the meanTemp2min variable
						{
				meanTemp2min+=tempArray[forVariable];//we add every temperature measurement
						}
				meanTemp2min/=24;//now we have the mean value
				
				tempArray[counts24]=tempSensor();//get the 24 temp (actually the 0 temp, after we got the count24 reset)
				//->print the mean value to the LCD--------------------------------------------------
				printMeanValue();//function that prints the mean temperature value at the LCD
				tenSecFlag=1;//flag that lets the program know that the 10 second window (to display the mean value at the LCD) is open
				timer_enable();
				//->----------------------------------------------------------------------
				}//we just got the mean value of the past 2mins (24*5sec)
				counts24++;
				}
				//***************TEMP SENSOR***************
				
				//To keep the mean value for 10 seconds on LCD
				if(i>7)
					if(tenSecFlag)
						if(erasedMean){
							printMeanValue();
							alreadyWritten=1;
						}
				
		}
   
 
}
