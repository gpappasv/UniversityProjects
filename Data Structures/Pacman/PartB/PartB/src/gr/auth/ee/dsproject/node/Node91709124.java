/* Γιώργος-Εφραίμ Παππάς ΑΕΜ:9124 gpappasv@ece.auth.gr 6970650963
 * Ναπολέων Παπουτσάκης  ΑΕΜ:9170 napoleop@ece.auth.gr 6974331167
  
 */


package gr.auth.ee.dsproject.node;

import gr.auth.ee.dsproject.pacman.*;


// δηλωση κλασης Node91709124 με τις μεταβλητες και τα πρωτοτυπα των συναρτησεων που αυτη περιεχει
public class Node91709124 
{
	
  int nodeX;
  int nodeY;
  int nodeMove;
  double nodeEvaluation;
  int[][] currentGhostPos = new int[4][2];
  int[][] flagPos= new int[4][2];
  boolean[] currentFlagStatus=new boolean[4];
  public int getNodeX(){return nodeX;}
  public int getNodeY(){return nodeY;}
  public void setNodeMove(int i){nodeMove=i;}
  public int getNodeMove(){return nodeMove;}
  public double getnodeEvaluation(){return nodeEvaluation;}
  
// συναρτηση αρχικων συνθηκων για την αρχικοποιηση με μηδενικες τιμες ολων των μεταβλητων.
  public Node91709124 (Room[][] Maze) 
  {
    nodeX=0;
    nodeY=0;
    nodeMove=0;
    nodeEvaluation=0;
    currentGhostPos=findGhosts(Maze);
    flagPos=findFlags(Maze);
    currentFlagStatus=checkFlags(Maze);
  }
//συναρτηση αρχικων συνθηκων με καταλληλα ορισματα(1 ορισμα για καθε μεταβλητη και ιδιου τυπου με αυτη) για την αρχικοποιηση των μεταβλητων με τις τιμες που θελει ο χρηστης.
  public Node91709124 (int direction,  int[] currPosition, Room[][] Maze) // Constructor
  {
	  switch (direction) {//η direction θα δεχτει μια τιμη εκ των 0, 1, 2, 3 και με την switch θα μεταβαλουμε την τιμη του nodeX και την τιμη του nodeY διαφορετικα αναλογως της τιμης της direction

		case 0:
			nodeX = currPosition[0] ;
			nodeY = currPosition[1] - 1;
			break;
		case 1:
			nodeX = currPosition[0] + 1;
			nodeY = currPosition[1] ;
			break;
		case 2:
			nodeX = currPosition[0] ;
			nodeY = currPosition[1] +1;
			break;
		case 3:
			nodeX = currPosition[0]-1;
			nodeY = currPosition[1] ;
			break;
		}

    nodeMove=direction;
   
    currentGhostPos=findGhosts(Maze);
    flagPos=findFlags(Maze);
    currentFlagStatus=checkFlags(Maze);
   nodeEvaluation=evaluate(direction,currPosition,Maze);
  } 
  //συναρτηση για την ευρεση των θεσεων των φαντασματων .
  private int[][] findGhosts (Room[][] Maze)
  {
  
 int rowghostPos=0;// δηλωση μεταβλητης  οπου θα περιεχεται ο αριθμος της σειρας του πινακα που η συναρτηση επιστρεφει και αρχικοποιση της με 0.
    int[][] ghostPos =new int[4][2];// δηλωση του διδιαστατου πινακα με τις θεσεις τν φαντασματων που θα επιστρεφει η συναρτηση.
   // δυο βρογχοι for για το σκαναρισμα ολου του Maze
    for(int i=0;i< PacmanUtilities.numberOfRows;i++) {
     for(int j=0;j< PacmanUtilities.numberOfColumns; j++) {
       // αποδοση της τιμης που επιστρεφει η συναρτηση isGhost() στη μεταβλητη τυπου boolean isGhost.
      if (Maze[i][j].isGhost())// ελεγχος της μεταβλητης isGhost που θα εχει τιμη 1 αν υπαρχει φαντασμα στην αντιστοιχη θεση.
      {
    	  ghostPos[rowghostPos][0]=i;//αποθηκευση της τετμιμενης Χ στην πρωτη στηλη της αντιστοιχης σειρας του πινακα επιστροφης.
    	  ghostPos[rowghostPos][1]=j;//αποθηκευση της τετμιμενης Υ στη δευτερη στηλη της αντιστοιχης σειρας του πινακα επιστροφης.
       rowghostPos++;// αυξηση της μεταβλητης που περιεχει τον αριθμο της σειρας του πινακα επιστροφης.
      
      }
      }
    }
    return ghostPos; //επιστροφη του πινακα με τις θεσεις των φαντασματων στο Maze.
  }

  private int[][] findFlags (Room[][] Maze)//συναρτηση που βρισκει τις συντεταγμενες απο τις σημαιες στο Maze
  {

 int rowflagPos=0;//μεταβλητη που θα τρεξει στον πινακα flagPos
    int[][] flagPos =new int[4][2];//πινακας 4 επι 2 (4 σημαιες και 2 στηλες, μια για την x και μια για την y της εκαστοτε σημαιας)
    for(int i=0;i<PacmanUtilities.numberOfRows;i++) {//τρεχουμε με 2 for σε ολον τον Maze
     for(int j=0;j<PacmanUtilities.numberOfColumns; j++) {
      
      if (Maze[i][j].isFlag())//ελεγχουμε αν στην εκαστοτε θεση υπαρχει σημαια με την συναρτηση isFlag
      {
        flagPos[rowflagPos][0]=i;//οταν βρουμε σημαια σε καποιο σημειο i,j του maze αποθηκευουμε τις i,j στον πινακα flagPos
        flagPos[rowflagPos][1]=j;
        rowflagPos++;//αυξηση του rowflagPos καθε φορα που βρισκουμε σημαια ωστε να κατεβουμε μια γραμμη στον πινακα flagPos και να αποθηκευσουμε τα δεδομενα για την νεα σημαια
      
      }
      }
    }
    return flagPos;//επιστροφη του πινακα flagPos με τις συντεταγμενες της καθε σημαιας
  }

  private boolean[] checkFlags (Room[][] Maze)//συναρτηση που ελεγχει αν η εκαστοτε σημαια στο maze εχει αποκτηθει απο τον πακμαν
  {
   int rowStatFlags=0;//μεταβλητη που θα τρεχει στον πινακα statFlags και θα αυξανει καθε φορα που βρισκουμε σημαια στον maze
   boolean statFlags [] = new boolean[4];//πινακας boolean που θα αποθηκευσει την κατασταση της καθε σημαιας που βρισκουμε μεσα στο maze
   for(int i=0;i<PacmanUtilities.numberOfRows;i++) {//for που τρεχει σε ολο το maze
      for(int j=0;j<PacmanUtilities.numberOfColumns; j++) {
       if(Maze[i][j].isFlag()==true)//αν υπαρχει σημαια στο i,j//if που ελεγχει αν στην θεση i,j του maze υπαρχει σημαια
    	 {statFlags[rowStatFlags] = Maze[i][j].isCapturedFlag();//αποθηκευση στην θεση που δειχνει η rwoStatFlags στον πινακα της καταστασης της σημαιας με χρηση της isCapturedFlag
         rowStatFlags++;}
       }
      }
   
   return statFlags;//επιστροφη του πινακα καταστασης των σημαιων
  }
  
 
  
  
  
  private double evaluate (int move,int[] currentPos,Room[][] Maze)//συναρτηση αξιολογησης κινησεων
  {

    double evaluation = 0;// αρχικοποιει την τιμη της αξιολογησης
  
    //-------------Κριτηριο 1: αποσταση απο τοιχους
    
    // σε περιπτωση που η επομενη κινηση του pacman θα τον παει διπλα σε τοιχο, αφαιρουμε 5 βαθμους απο την τιμη της evaluation, καθως θα περιοριστει ο pacman.
    if(nodeX==0 || nodeY==0 || nodeX==PacmanUtilities.numberOfRows || nodeY==PacmanUtilities.numberOfColumns) evaluation=evaluation-5;
    // σε περιπτωση που η επομενη κινηση φερει τον pacman σε μια γωνια, αφαιρουμε επιπλεον 10 βαθμους απο την τιμη της evaluation.
    if((nodeX==0 && nodeY==0) || (nodeX==PacmanUtilities.numberOfRows && nodeY==PacmanUtilities.numberOfColumns)||(nodeX==PacmanUtilities.numberOfRows && nodeY==0)||(nodeX==0 && nodeY==PacmanUtilities.numberOfColumns)) evaluation=evaluation-10;
    // αν η επoμενη κινηση τεινει να φερει τον pacman προς το κεντρο της πιστας (ειτε ως προς χ ειτε ως προς y) προσθετουμε 10 βαθμους στην τιμη της evaluation
    if(nodeX>currentPos[0] && currentPos[0]<(PacmanUtilities.numberOfRows/2)) evaluation=evaluation+10;
    if(nodeX<currentPos[0] && currentPos[0]>(PacmanUtilities.numberOfRows/2)) evaluation=evaluation+10;
    if(nodeY>currentPos[1] && currentPos[1]<(PacmanUtilities.numberOfColumns/2)) evaluation=evaluation+10;
    if(nodeY<currentPos[1] && currentPos[1]>(PacmanUtilities.numberOfColumns/2)) evaluation=evaluation+10;
    //αν η κινηση που αξιολογειται απομακρυνει τον pacman (ειτε ως προς χ ειτε ως προς y) απο το κεντρο, αφαιρουμε 4 βαθμους απο την αξιολογηση της κινησης αυτης.
    if(nodeX<currentPos[0] && currentPos[0]<PacmanUtilities.numberOfRows/2) evaluation=evaluation-4;
    if(nodeX>currentPos[0] && currentPos[0]>PacmanUtilities.numberOfRows/2) evaluation=evaluation-4;
    if(nodeY<currentPos[1] && currentPos[1]<PacmanUtilities.numberOfColumns/2) evaluation=evaluation-4;
    if(nodeY>currentPos[1] && currentPos[1]>PacmanUtilities.numberOfColumns/2) evaluation=evaluation-4;
    //-------------Τελος Κριτηριου 1

    //-----------------------------Κριτηριο 2 με βαση την αποσταση του pacman απο τις σημαιες
    int availableflags=0;//μεταβλητη που θα μετρησει ποσες σημαιες ειναι διαθεσιμες ακομα στο παιχνιδι
    boolean[] curFlSt= new boolean[4];//πινακας που θα αποθηκευσει ακριβως στην επομενη γραμμη του κωδικα την κατασταση της καθε σημαιας στο παιχνιδι με χρηση της checkFlags
    		curFlSt=checkFlags(Maze);
    for(int i=0;i<4;i++){//for η οποια τρεχει στον πινακα currentFlagStatus και μετραει μεσω της availableflags ποσες σημαιες ειναι ακομα ενεργες στο παιχνιδι
    	if (curFlSt[i]==false)availableflags++;//η μεταβλητη availableflags θα αποκτησει τιμη τελικα ιση με τον αριθμο των ενεργων σημαιων
    	 	
    	
    }
    

  
    
    double[] flagDistanceFromCurPos=new double[availableflags];//πινακας μεγεθους ισου με τις ενεργες σημαιες οπου θα αποθηκευσει την αποσταση του pacman απο καθε σημαια πριν κινηθει προς την κατευθυνση που αξιολογουμε
    double[] flagDistanceFromNextPos=new double[availableflags];//πινακας μεγεθους ισου με τις ενεργες σημαιες οπου θα αποθηκευσει την αποσταση του pacman απο καθε σημαια αφου κινηθει προς την κατευθυνση που αξιολογουμε
    int[][] flagP=new int[4][2];//πινακας που θα αποθηκευσει στην αμεσως επομενη γραμμη του κωδικα τις συντεταγμενες της καθε σημαιας για τον υπολογισμο των αποστασεων που προαναφεραμε
    flagP=findFlags(Maze);
    for(int i=0,k=0;i<4;i++){
    	if(!curFlSt[i]){//ελεγχουμε αν η σημαια στην θεση i του currentFlagStatus ειναι ενεργη ωστε να αποθηκευσουμε την αποσταση του πριν και του μετα της κινησης του pacman στους παραπανω πινακες
    		flagDistanceFromCurPos[k]=(Math.sqrt((flagP[i][0]-currentPos[0])*(flagP[i][0]-currentPos[0])+(flagP[i][1]-currentPos[1])*(flagP[i][1]-currentPos[1])));
    		flagDistanceFromNextPos[k]=(Math.sqrt((flagP[i][0]-nodeX)*(flagP[i][0]-nodeX)+(flagP[i][1]-nodeY)*(flagP[i][1]-nodeY)));
    		k++;}//μετρητης k που αυξανει καθε φορα που βρισκουμε ενεργη σημαια (θα φτασει μεχρι την τιμη availableflags, οσο δηλαδη και το μεγεθος των πινακων που αποθηκευουν τις αποστασεις)
    }
    
    int minDistance=0;//μεταβλητη ακεραιου που θα αποθηκευσει παρακατω την θεση του πινακα flagDistanceFromCurPos με την ελαχιστη τιμη (δηλαδη βλεπουμε ποια θεση του πινακα αναφερεται στην κοντινοτερη σημαια στον pacman, πριν κινηθει)
    if (availableflags>0){//αν η τιμη των ενεργων σημαιων ειναι μηδεν θα εχουμε προβλημα με τους πινακες που χρησιμοποιουν ειτε τις συντεταγμενες των ενεργων σημαιων ειτε με αυτους που εχουμε υπολογισει τις αποστασεις απο το πριν και το μετα της κινησης του πακμαν
    for(int i=0;i<availableflags;i++){
    	//ευρεση της θεσης του flagDistanceFromCurPos με την ελαχιστη τιμη αποστασης
    	if(flagDistanceFromCurPos[minDistance]>flagDistanceFromCurPos[i])minDistance=i;
       	
    	}
    
    if(flagDistanceFromCurPos[minDistance]<flagDistanceFromNextPos[minDistance]) evaluation=evaluation-17;//αν ο pacman απομακρυνθηκε απο την κοντινοτερη σε αυτον σημαια,μετα την κινηση που αξιολογουμε, τοτε αφαιρουμε 17 βαθμους απο το evaluation
    if(flagDistanceFromCurPos[minDistance]>flagDistanceFromNextPos[minDistance]) evaluation=evaluation+38; //αν ο pacman πλησιασε την κοντινοτερη σε αυτον σημαια,μετα την κινηση που αξιολογουμε, τοτε προσθετουμε 38 βαθμους στο evaluation
    }
    //--------------------------------Τελος κριτηριου 2
    
    //--------------------------------Κριτηριο 3: Αποσταση απο τα φαντασματα
   
    double[] ghostDistanceFromCurPos=new double[4];//πινακας που θα αποθηκευσει την αποσταση του pacman απο καθε φαντασμα πριν να κινηθει
    double[] ghostDistanceFromNextPos=new double[4];// πινακας που θα αποθηκευσει την αποσταση του pacman απο καθε φαντασμα αφου κινηθει
    int[][] currGhostPos=findGhosts(Maze);//πινακας που αποθηκευει τις συντεταγμενες του καθε φαντασματος
    for(int i=0;i<4;i++){
    	//υπολογισμος της αποστασης του pacman απο καθε φαντασμα πριν και μετα της κινησης του κατα την κατευθυνση που αξιολογουμε
    	ghostDistanceFromCurPos[i]=Math.sqrt((currGhostPos[i][0]-currentPos[0])*(currGhostPos[i][0]-currentPos[0])+(currGhostPos[i][1]-currentPos[1])*(currGhostPos[i][1]-currentPos[1]));
    	ghostDistanceFromNextPos[i]=Math.sqrt((currGhostPos[i][0]-nodeX)*(currGhostPos[i][0]-nodeX)+(currGhostPos[i][1]-nodeY)*(currGhostPos[i][1]-nodeY));
    	
    }
    
    int minDistance1=0;
    
    for(int i=0;i<4;i++){
    	//ευρεση της θεσης του ghostDistanceFromCurPos με την ελαχιστη τιμη αποστασης
    	if(ghostDistanceFromCurPos[minDistance1]>ghostDistanceFromCurPos[i])minDistance1=i;
    	}
    
    
    	
    
    
    
    if(ghostDistanceFromCurPos[minDistance1]<ghostDistanceFromNextPos[minDistance1]) evaluation=evaluation+47;//αν ο pacman απομακρυνθηκε απο το κοντινοτερο του φαντασμα, τοτε +47 στην evaluation
    if(ghostDistanceFromCurPos[minDistance1]>ghostDistanceFromNextPos[minDistance1]) evaluation=evaluation-20;//αν ο pacman πλησιασε το κοντινοτερο του φαντασμα, τοτε -20 στην evaluation

    
    
    //---------------------------------Τελος κριτηριου 3
    
    //
    // -------------------αρχη κριτηριου 4
    if(availableflags>0)
    	//εδω βλεπουμε εαν η αποσταση του πακμαν απο την κοντινοτερη σε αυτον σημαια ειναι μικροτερη απο την αποσταση μεταξυ του κοντινοτερου φαντασματος σε αυτον και της κοντινοτερης σε αυτον σημαιας
    	if(ghostDistanceFromNextPos[minDistance1]>flagDistanceFromNextPos[minDistance] && (flagDistanceFromCurPos[minDistance]>flagDistanceFromNextPos[minDistance]))
    		{evaluation=evaluation+40;			
    		}
    //με την if της γραμμης 226 θελουμε να δουμε αν θα προλαβει ο πακμαν να πιασει την σημαια που ειναι κοντα του πριν το φαντασμα τον φτασει. Με το +40 που προσθετουμε στην evaluation ουσιαστικα "ακυρωνουμε" την ισχυ του κριτηριου της αποστασης απο το φαντασμα και δινουμε μεγαλυτερη βαση στην αποκτηση της σημαιας
    
    
    	return evaluation;//επιστροφη της τελικης τιμης αξιολογησης της κινησης

  }
  
  

}