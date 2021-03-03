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
#define LOOP 7000

#define p 60
#define c 250

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

} queue;



queue *queueInit (void);
void queueDelete (queue *q);
void queueAdd (queue *q, workFunction in);
void queueDel (queue *q, workFunction *out);
//random functions
void *func1(void* arg);
void *func2(void* arg);
void *func3(void* arg);
void *func4(void* arg);
void *func5(void* arg);
//end of random functions

int countWorks=0;
double averageResponse=0;

int main ()
{
	queue *fifo;
	
	pthread_t pro[p], con[c];

	fifo = queueInit ();
	if (fifo ==  NULL) {
		fprintf (stderr, "main: Queue Init failed.\n");
		exit (1);
	}

	int w;

	for(w=0; w<p; w++){
        pthread_create(&pro[w], NULL, producer, fifo);
        }
	for(w=0; w<c; w++){
        pthread_create(&con[w], NULL, consumer, fifo);
        }
    //joining the threads
	for(w=0; w<p; w++)pthread_join(pro[w], NULL);
	for(w=0; w<c; w++)pthread_join(con[w], NULL);

	queueDelete (fifo);

	return 0;
}

void *producer (void *q)
{
	queue *fifo;
	int i;

	int functionRandomSelect = ((unsigned int)pthread_self()%5);// metavlhth gia epilogh sunarthshs

    	void * (*workk[5])()={func1,func2,func3,func4,func5};//pinakas sunarthsewn pros epilogh

	fifo = (queue *)q;

	for (i = 0; i < LOOP; i++) {
	
/////////////////
    	workFunction inn; //ftiaxnw metavlhth tupou workfunction pou tha mpei sthn oura

    	inn.work=workk[functionRandomSelect]; // ginetai epilogh ths sunarthshs

    	inn.arg=pthread_self()%100; // vazoume to orisma ths sunarthshs pou epilexthike parapanw
/////////////////
    	pthread_mutex_lock (fifo->mut);

    	while (fifo->full) {
      		printf ("producer: queue FULL.\n");
      		pthread_cond_wait (fifo->notFull, fifo->mut);
    	}

    	queueAdd (fifo,inn) ; // edw mpainei mia douleia sthn oura kai apothikevetai h wra pou mphke

    	pthread_mutex_unlock (fifo->mut);
    	pthread_cond_signal (fifo->notEmpty);

	}

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
{ gettimeofday(&in.tv,NULL); // vlepw ti wra mphke h douleia pou evale o consumer sthn oura

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

void *func4(void* arg)//just print the arg
{
int a;
a=arg;
printf("\n I just got number %d \n",a);

}

void *func5(void* arg)//countdown from arg to zero
{
int x;
x=arg;
printf("\n Countdown: ");
for (x=arg;x>0;x--){
    printf("\t%d",x);
}
printf("\n");

}
