/*
 *	File	: pc.c
 *
 *	Title	: Demo Producer/Consumer.
 *
 *	Short	: A solution to the producer consumer problem using
 *		pthreads.
 *
 *	Long 	:
 *
 *	Author	: Andrae Muys
 *
 *	Date	: 18 September 1997
 *
 *	Revised	:
 */

#include <pthread.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <semaphore.h>
#include <time.h>
#include <sys/time.h>
#include <math.h>

#define QUEUESIZE 10

#define p 60
#define c 100

#define PI 3.14159265

void *producer (void *args);
void *consumer (void *args);

typedef struct  {
  void * (*work)(void *);
  void * arg;
  struct timeval tv;
}workFunction;

typedef struct {
  //int buf[QUEUESIZE];
  workFunction buf[QUEUESIZE];

  long head, tail;
  int full, empty;
  pthread_mutex_t *mut;
  pthread_cond_t *notFull, *notEmpty;

  FILE * conTime;//file for consumer times!!!!!!!!!!!!!!!


} queue;

typedef struct {
  
  int Period;
  int TasksToExecute;
  int StartDelay;
  void * (*StartFcn)(void *);
  void * (*StopFcn)(void );
  void * (*TimerFcn)(void *);
  void * (*ErrorFcn)(void );
  void * UserData;

  long long tmInSec;
  int startAtUsed;

  float abs;
  queue * ff;
  pthread_t myThread;
  FILE * ata;
  FILE * TTQ;

  int isStarted;
}Timer;

queue *queueInit (void);
void queueDelete (queue *q);
void queueAdd (queue *q, workFunction in);
void queueDel (queue *q, workFunction *out);
//random functions
void *func1(void* arg);
void *func2(void* arg);
void *func3(void* arg);
//end of random functions
//for the timer
void timerInit(Timer* t,int prd, int t2ex,int sDelay, void *(*fcn)(void*),queue *k,FILE *at,FILE *ttq);
void *errFcn(void);
void *stopFcn(void);
int *startFcn(int k);
void start(Timer* t);
void startat(Timer * t, int y, int m, int d, int h, int min, int sec);

int countWorks=0;
double averageResponse=0;



int main ()
{ //***********INITIALIZATIONS***************|
	queue *fifo;
  fifo = queueInit ();

  struct timeval startt;
  struct timeval end;
  long long timeElapsed=0;

  //*******TEXT FILES*********
  
  FILE * A1;
  FILE * A01;
  FILE * A001;

  A1 = fopen ("ADDTOADD1.txt","w");
  A01 = fopen ("ADDTOADD01.txt","w");
  A001 = fopen ("ADDTOADD001.txt","w");
  
  FILE *TTQ1;
  FILE *TTQ01;
  FILE *TTQ001;
  
  TTQ1 = fopen ("TTQ1.txt","w");
  TTQ01 = fopen ("TTQ01.txt","w");
  TTQ001 = fopen ("TTQ001.txt","w");

  //**************************

	Timer *oneSec;
  Timer *pointOneSec;
  Timer *pointZeroOneSec;
    
  oneSec = (Timer *)malloc (sizeof (Timer));
  pointOneSec = (Timer *)malloc (sizeof (Timer));
  pointZeroOneSec = (Timer *)malloc (sizeof (Timer));

    timerInit(oneSec,1000,3600,0,func1,fifo,A1,TTQ1);
    timerInit(pointOneSec,100,36000,0,func2,fifo,A01,TTQ01);
    timerInit(pointZeroOneSec,10,360000,0,func3,fifo,A001,TTQ001);
  
  //*****************************************|  
    
	pthread_t con[c];
	
	if (fifo ==  NULL) {
		fprintf (stderr, "main: Queue Init failed.\n");
		exit (1);
	}
  //************STARTING THE THREADS*********|
	int w;
	for(w=0; w<c; w++){
        pthread_create(&con[w], NULL, consumer, fifo);//needs threadArgs item in place of fifo
        }
  gettimeofday(&startt,NULL);
  
  start(oneSec);
  start(pointOneSec);
  start(pointZeroOneSec);
  //startat(pointZeroOneSec,2020,10,2,19,15,52);
  //*****************************************|
  //*************JOIN MY THREADS*************|
  
  pthread_join(oneSec->myThread,NULL);
  pthread_join(pointOneSec->myThread,NULL);
  pthread_join(pointZeroOneSec->myThread,NULL);

	
  //*****************************************|
  
  printf("\n\n");
  
  
  //*****CLOSING TXT FILES*****
  fclose (A1);
  fclose (A01);
  fclose (A001);
  
  fclose(TTQ1);
  fclose(TTQ01);
  fclose(TTQ001);
  
  //***************************
  
  free(oneSec);
  free(pointOneSec);
  free(pointZeroOneSec);
  
  gettimeofday(&end,NULL);
  //************
  startt.tv_sec=end.tv_sec-startt.tv_sec;
  startt.tv_usec=end.tv_usec-startt.tv_usec;
  timeElapsed=startt.tv_sec*1000000+startt.tv_usec;
  //************
  printf("\n TIME PASSED: (us) %lli",timeElapsed);
  printf("\n");
  
  for(w=0; w<c; w++)pthread_join(con[w], NULL);
	queueDelete (fifo);

	return 0;
}

void *producer (void *q)
{ Timer *tm;
	int i;
  tm=(Timer *)q;
  struct timeval timeDiff;
  struct timeval startTimeTQ;
  struct timeval endTimeTQ;
  struct timeval extraFix;

  tm->Period*=1000;

  float absoluteMean=0;

  long long addToadd=0;
  long long offset=0;
  long long extraOffset=0;
  long long sleptFor=tm->Period;
 
  long long aTimeVariable=0;
  workFunction inn; //ftiaxnw metavlhth tupou workfunction pou tha mpei sthn oura
  /////////////////   
    	inn.work=tm->TimerFcn; // ginetai epilogh ths sunarthshs

    	inn.arg=tm->UserData; // vazoume to orisma ths sunarthshs pou epilexthike parapanw
/////////////////

    timeDiff.tv_usec=0;
    timeDiff.tv_sec=0;
    startTimeTQ.tv_sec=0;
    startTimeTQ.tv_usec=0;
    endTimeTQ.tv_sec=0;
    endTimeTQ.tv_usec=0;
//for the startat function
    if(tm->startAtUsed==1){
      struct timeval timeSA;
      long long usecTime;
      long long sleep;
      
      gettimeofday(&timeSA,NULL);
      usecTime=(long long)timeSA.tv_sec*1000000+(long long)timeSA.tv_usec;
      sleep=(tm->tmInSec*1000000)-usecTime;

      printf("Will wait for %lli ",sleep);
      usleep(sleep);//perimene oso xrono xreiastei oste na ksekinhseis thn wra pou oristhke

    }
////////////////////////////
	for (i = 0; i < tm->TasksToExecute; i++) {

      if(i==0)usleep(tm->StartDelay); // Delay prwths ekteleshs ths TimerFcn

      gettimeofday(&startTimeTQ,NULL);//started trying to add to Queue at:
      //************************************************************************
      pthread_mutex_lock (tm->ff->mut);
    	
      while (tm->ff->full) {
      		printf ("producer: queue FULL.\n");
          tm->ErrorFcn();
      		pthread_cond_wait (tm->ff->notFull, tm->ff->mut);
    	}
      if(i>0)
      {
        timeDiff.tv_sec=inn.tv.tv_sec;//apothikevw thn prohgoumenh wra pou mphke sthn queueAdd
        timeDiff.tv_usec=inn.tv.tv_usec; //apothikevw thn prohgoumenh wra pou mphke sthn queueAdd
      }
      gettimeofday(&inn.tv,NULL); // vlepw ti wra mphke h douleia pou evale o producer sthn oura
      queueAdd (tm->ff,inn) ; // edw mpainei mia douleia sthn oura 
      gettimeofday(&endTimeTQ,NULL);//ended trying to add to Queue at:

    	pthread_mutex_unlock (tm->ff->mut);
      pthread_cond_signal (tm->ff->notEmpty);
      //*************************************************************************
      
      //Calculating the times we need*******************
      aTimeVariable=((endTimeTQ.tv_sec-startTimeTQ.tv_sec)*1000000+(endTimeTQ.tv_usec-startTimeTQ.tv_usec));//TIME TO QUEUE
      fprintf(tm->TTQ,"\n%lli",aTimeVariable);
      //************************************************

      if(i>0){
        timeDiff.tv_sec=inn.tv.tv_sec-timeDiff.tv_sec;//twra kserw posh wra mou phre apo queueAdd se queueAdd, dhladh posh einai h pragmatikh periodos ths producer
        timeDiff.tv_usec=inn.tv.tv_usec-timeDiff.tv_usec;      
      
        addToadd=timeDiff.tv_sec*1000000+timeDiff.tv_usec;//addToadd = timeFromQaddtoQadd 
        printf("\nTime Diff compared to period (ADD to ADD) %lld in us",addToadd);//testing
        fprintf(tm->ata,"\n%lld",addToadd);//save add to add time
        
        offset=addToadd-tm->Period;//now we have the offset. Its the value of timeFromQaddtoQadd minus period -> Kseroume oti apo qadd se qadd exoume driftCorrection seconds
        absoluteMean = (offset>0) ? ((absoluteMean*(i-1)+offset)/i):((absoluteMean*(i-1)-offset)/i);
        
        gettimeofday(&extraFix,NULL);
        
        extraFix.tv_sec=extraFix.tv_sec-inn.tv.tv_sec;
        extraFix.tv_usec=extraFix.tv_usec-inn.tv.tv_usec;
        
        extraOffset=extraFix.tv_sec*1000000+extraFix.tv_usec;
        if(((sleptFor-offset)-extraOffset)>0)
        usleep((sleptFor-offset)-extraOffset);
        
      }else usleep(tm->Period);
      sleptFor=sleptFor-offset;
  }
  tm->StopFcn();
  printf("\n Period: %d absoluteMean: %f\n",tm->Period,absoluteMean);
  tm->abs=absoluteMean;
	return (NULL);

}

void *consumer (void *q)
{
	queue *fifo;
	int i;
	workFunction d;
  struct timeval tElapsed;
  long long elapsed;
  double ms;

	fifo = (queue *)q;
  
	while(1) {
    	pthread_mutex_lock (fifo->mut);
    	while (fifo->empty) {
      	//printf ("consumer: queue EMPTY.\n");
      	pthread_cond_wait (fifo->notEmpty, fifo->mut);
    }
    queueDel (fifo, &d);//parelave o consumer th douleia

    gettimeofday(&tElapsed,NULL);//krataw thn wra pou o consumer parelave th douleia

    elapsed=((tElapsed.tv_sec-d.tv.tv_sec)*1000000+(tElapsed.tv_usec-d.tv.tv_usec));     
    countWorks++;
    averageResponse=(double)(averageResponse*(countWorks-1)+elapsed)/(double)(countWorks);
    ms=averageResponse/1000;
    printf("\ngot in %lld us | average response is %f ms | and count: %ld\n",elapsed,ms,countWorks);
    fifo->conTime = fopen("consumerTime.txt","a"); //append file
    fprintf(fifo->conTime,"\n%lli",elapsed);//write on file
    fclose(fifo->conTime);//close

    pthread_mutex_unlock (fifo->mut);
    pthread_cond_signal (fifo->notFull);
	
	d.work(d.arg);//ektelei th douleia
    
	}

	return (NULL);
}

queue *queueInit (void)
{
  queue *q;

  q = (queue *)malloc (sizeof (queue));
  if (q == NULL) return (NULL);

  q->empty = 1;
  q->full = 0;
  q->head = 0;
  q->tail = 0;
  q->mut = (pthread_mutex_t *) malloc (sizeof (pthread_mutex_t));
  pthread_mutex_init (q->mut, NULL);
  q->notFull = (pthread_cond_t *) malloc (sizeof (pthread_cond_t));
  pthread_cond_init (q->notFull, NULL);
  q->notEmpty = (pthread_cond_t *) malloc (sizeof (pthread_cond_t));
  pthread_cond_init (q->notEmpty, NULL);


  return (q);
}

void queueDelete (queue *q)
{
  pthread_mutex_destroy (q->mut);
  free (q->mut);
  pthread_cond_destroy (q->notFull);
  free (q->notFull);
  pthread_cond_destroy (q->notEmpty);
  free (q->notEmpty);
  free (q);

}

void queueAdd (queue *q, workFunction in)
{ 

  q->buf[q->tail] = in;
  q->tail++;
  if (q->tail == QUEUESIZE)
    q->tail = 0;
  if (q->tail == q->head)
    q->full = 1;
  q->empty = 0;

  return;
}

void queueDel (queue *q, workFunction *out)
{
  *out = q->buf[q->head];

  q->head++;
  if (q->head == QUEUESIZE)
    q->head = 0;
  if (q->head == q->tail)
    q->empty = 1;
  q->full = 0;

  return ;
}

//random functions create

void *func1(void* arg)//sin
{   double x,ret,val;

    int argu=arg;
    val=PI/180;
    x=argu;
    ret=sin(x*val);

    printf("The sine of %lf is %lf degrees", x, ret);
}

void *func2(void* arg)//sqrt
{
    double value;
    double result;
    int vlue;
    vlue=arg;
    value=vlue;

    result = sqrt(value);

    printf("The Square Root of %f is %f\n", value, result);
}

void *func3(void* arg)//find divisors
{ int n=arg;
  printf("The divisors of %d are: \n",n);
    for (int i=1;i<=n;i++)
        if (n%i==0)
            printf("%d ",i);
}

void timerInit(Timer* t,int prd, int t2ex,int sDelay, void *(*fcn)(void*), queue *k,FILE *at,FILE *ttq){//just to add values to my timer

t->Period=prd; //maybe needs pointer (o Period dhladh na ginei pointer)
t->StartDelay=sDelay;
t->TasksToExecute=t2ex;
t->TimerFcn=fcn;
t->StartDelay=sDelay;
t->ErrorFcn=errFcn;
t->StopFcn=stopFcn;
t->StartFcn=startFcn;
t->UserData=t->StartFcn(t->Period);

t->isStarted=0;//becomes 1 when the timer is started

t->ff=k;
t->ata=at;
t->TTQ=ttq;
t->abs=0;

t->startAtUsed=0;
t->tmInSec=0;
}

void start(Timer * t){//starts the timer and runs timerFcn for the first time
    t->isStarted=1;
    pthread_create(&(t->myThread),NULL,producer,t);
}

void *errFcn(){
    printf("\n I'M FIFO AND I'M FULL \n");
}

void *stopFcn(void){

    printf("\n LAST CALL -> IM STOPFCN \n");

}
int *startFcn(int k){//calculates userdata -> Userdata will be 2*the sqrt of period value

return (int)(2*sqrt(k));

}

void startat(Timer * t, int y, int m, int d, int h, int min, int sec){
    
  struct tm time;
  time_t tOfDay;
  y=y-1900;
  m=m-1;
  h=h-2;
  sec=sec+1;
  time.tm_year = y; 
  time.tm_mon = m;          
  time.tm_mday = d;          
  time.tm_hour = h;
  time.tm_min = min;
  time.tm_sec = sec;
  time.tm_isdst = -1;        
  tOfDay = mktime(&time);
  t->tmInSec = (tOfDay);
  t->startAtUsed=1;
 
  pthread_create (&(t->myThread), NULL, producer, t);
}