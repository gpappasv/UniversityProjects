/* Γιώργος-Εφραίμ Παππάς ΑΕΜ:9124 gpappasv@ece.auth.gr 6970650963
 * Ναπολέων Παπουτσάκης  ΑΕΜ:9170 napoleop@ece.auth.gr 6974331167
  
 */

package gr.auth.ee.dsproject.pacman;

import java.util.ArrayList;

import gr.auth.ee.dsproject.node.Node91709124;

/**
 * <p>
 * Title: DataStructures2011
 * </p>
 * 
 * <p>
 * Description: Data Structures project: year 2011-2012
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: A.U.Th.
 * </p>
 * 
 * @author Michael T. Tsapanos
 * @version 1.0
 */

public class Creature implements gr.auth.ee.dsproject.pacman.AbstractCreature
{

  public String getName ()
  {
    return "Mine";
  }

  private int step = 1;
  private boolean amPrey;

  public Creature (boolean isPrey)
  {
    amPrey = isPrey;

  }
  
  // ΑΛΓΟΡΙΘΜΟΣ ΥΛΟΠΟΙΗΣΗΣ ΔΕΝΤΡΟΥ 
  
// συναρτηση που επιστρεφει την επομενη κινηση του pacman
  public int calculateNextPacmanPosition (Room[][] Maze, int[] currPosition)
  { 
    Node91709124 root = new Node91709124(-1,currPosition,0,null,Maze); // δημιουργια της ριζας του δεντρου
    createSubTreePacman(root.getDepth()+1, root, Maze, currPosition);  // κληση της συναρτησης createSubTreePacman για την τοποθετηση των παιδιων της ριζας του δεντρου. 
	
    double mostSutMove = ABprun(root,2,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,true); //κληση της συναρτησης ABprun με ορισματα root, βαθος δεντρου 2 α=-οο και b=+oo αρχιζοντας απο το max part (maxP=true)
   
    int moveToReturn=0; // δηλωση και αρχικοποιηση με 0 της μεταβλητης moveToReturn που θα κρατησει αργοτερα την κατευθυνση της  επομενης κινησης του pacman
       
    //διπλος βρογχος for για την ταυτοποιηση του node που περιεχει το evaluation που επιστραφηκε στην τιμη του mostSutMove 
    for(int i=0;i<root.getChildren().size();i++) {
    	for(int y=0;y<root.getChildren().get(i).getChildren().size();y++) {
    		
    		if (mostSutMove==root.getChildren().get(i).getChildren().get(y).getnodeEvaluation())
    			{moveToReturn=root.getChildren().get(i).getChildren().get(y).getNodeMove();
    		}
    	}
    	
    	
    }
       
    
    
    return moveToReturn;// επιστροφη της τελικης κινησης
    
    
    
  }
  
    

  double ABprun(Node91709124 node,int treeDepth,double a, double b, boolean maxP)//ab pruning
	{
	  if (treeDepth == 0 || node.getChildren().isEmpty())//ελεγχος αν το βαθος δεντρου ειναι μηδεν η αν δεν εχει παιδια το node που μπηκε ως ορισμα στην συναρτηση
		  return node.getnodeEvaluation();//επιστροφη του evaluation του node σε περιπτωση που ισχυει η παραπανω συνθηκη
	  
	  if (maxP) {//ελεγχος αν ο ειμαστε στο max η στο min μερος του δεντρου
		  double v=Double.NEGATIVE_INFINITY;//βαζουμε στην τιμη του v το -οο
		  
		  for(int i=0;i<node.getChildren().size();i++) {//βρογχος for απο μηδεν εως τον αριθμο των παιδιων του node
			  
			if(v<ABprun(node.getChildren().get(i),treeDepth-1,a,b,false))//ελεγχος αν η μεταβλητη ν εχει μικροτερη τιμη απο την επιστροφη της συναρτησης ABprun για βαθος κατα ενα μικροτερο απο το βαθος που εχουμε τωρα
				v=ABprun(node.getChildren().get(i),treeDepth-1,a,b,false);//αποδοση της τιμης της επιστροφης της παραπανω κλησης στην ν αν ισχυει η συνθηκη
			
			if(v>a)a=v;// ελεγχος αν το ν απεκτησε μεγαλυτερη τιμη απο το a και αποδοση της τιμης του ν στο a αν ισχυει η συνθηκη
				if(b<=a)break;//αν το b<=a κανουμε διακοπη του βρογχου φορ
				
		  }
		  return v;//επιστροφη της τιμης του ν
		  
	  }
	  else //αν η ABprun κληθηκε για επιπεδο που πρεπει να επιλεχθει το ελαχιστο evaluation γινονται τα παρακατω
	  {
		  double v=Double.POSITIVE_INFINITY;//αποδοση της τιμης +oo στην ν
		  
		  for(int i=0;i<node.getChildren().size();i++) {//βρογχος for απο μηδεν εως τον αριθμο των παιδιων του node
			  
			if(v>ABprun(node.getChildren().get(i),treeDepth-1,a,b,false))//ελεγχος αν η μεταβλητη ν εχει μεγαλυτερη τιμη απο την επιστροφη της συναρτησης ABprun για βαθος κατα ενα μικροτερο απο το βαθος που εχουμε τωρα
				v=ABprun(node.getChildren().get(i),treeDepth-1,a,b,false);//αποδοση της τιμης της επιστροφης της παραπανω κλησης στην ν αν ισχυει η συνθηκη
			
			if(v<b)b=v;// ελεγχος αν το ν απεκτησε μεγαλυτερη τιμη απο το b και αποδοση της τιμης του ν στο b αν ισχυει η συνθηκη
				if(b<=a)break;//αν το b<=a κανουμε διακοπη του βρογχου φορ
				
		  }
		  return v;//επιστροφη της τιμης του ν
		  
	  }
		  
		  
		  
	  
	} 
  
  void createSubTreePacman (int depth, Node91709124 parent, Room[][] Maze, int[] currPacmanPosition)
  {


		int moveToReturn=(int)(Math.random()*4);//τυχαια αρχικοποιηση της μεταβλητης που θα δηλωσει την επομενη κινηση του πακμαν σε περιπτωση που ολες οι πιθανες κινησεις θεωρουνται αδυνατες (δηλαδη σε περιπτωση που τον περικυκλωσουν τα φαντασματα
		
		
		
		ArrayList<Node91709124> avpos=new ArrayList <Node91709124>();//δημιουργια arraylist αντικειμενων που θα αποθηκευτουν τα αντικειμενα τυπου Node91709124 οπου θα περιεχουν κινηση που θεωρειται επιτρεπτη
		
		Node91709124[] obj=new Node91709124[4];//πινακας αντικειμενων τυπου Node91709124 τεσσαρων θεσεων
		obj[0]=new Node91709124(0,currPacmanPosition,depth,null,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 0
		obj[1]=new Node91709124(1,currPacmanPosition,depth,null,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 1
		obj[2]=new Node91709124(2,currPacmanPosition,depth,null,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 2
		obj[3]=new Node91709124(3,currPacmanPosition,depth,null,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 3
		
		for(int i=0;i<4;i++){//for που τρεχει απο μηδεν εως 3 ωστε να καλυψουμε ολες τις πιθανες κατευθυνσεις
			if(Maze[currPacmanPosition[0]][currPacmanPosition[1]].walls[i]==1)//ελεγχος αν στην κατευθυνση που δηλωνει η τιμη του i υπαρχει τοιχος
				if(Maze[obj[i].getNodeX()][obj[i].getNodeY()].isGhost()==false)//ελεγχος αν στην θεση που προκειται να παει ο πακμαν (που δηλωνει η τιμη του i) προκειται να βρεθει φαντασμα (ωστε να απορριφθει σε περιπτωση που συγκρουονται πακμαν με φαντασμα)
					avpos.add(obj[i]);//αν στην κατευθυνση που δηλωνει το i δεν προκειται να βρεθει φαντασμα, αποθηκευουμε το αντιστοιχο αντικειμενο με την αντιστιχη τιμη κατευθυνσης στην arraylist καθως αποτελει μια πιθανη κινηση του πακμαν. Οποια αντικειμενα απο αυτα που δημιουργησαμε παραπανω αποθηκευτουν τελικα στην arraylist, οι κατευθυνσεις αυτων θα αξιολογηθουν αργοτερα
			
		}
		
		
		
	
		
		
		Room[][][] copies = new Room[avpos.size()][][]; // δηλωση τρισδιαστατος πινακας οπου περιεχει τοσα αντιφραφα του Maze οσα και οι διαθεσιμες κινησεις του pacman.Σε καθε, θεση της πρωτης διαστασης αποθηκευουμε το διδιαστατο πινακα του maze
		
		
		int [] nextPositions = new int[2]; // δηλωση πινακα μεγεθους 2 οπου θα εισαχθουν οι συντεταγμενες της επομενης κινησης του pacman
		
		//βρογχος for που τρεχει απο 0 μεχρι τον αριθμο των διαθεσιμων κινησεων του pacman
		for(int i=0;i<avpos.size();i++) {
			
			 copies[i] = PacmanUtilities.copy(Maze); // αποθηκευση του αντιγραφου του maze στην i θεση του πινακα των αντιγραφων
			 nextPositions = PacmanUtilities.evaluateNextPosition(Maze,currPacmanPosition,avpos.get(i).getNodeMove(),PacmanUtilities.borders);//αποθηκευση στον πινακα nextPositions της επομενης θεσης του pacman μετα την κινηση με κληση της συναρτησης evaluateNextPosition
			 Node91709124 test = new Node91709124(avpos.get(i).getNodeMove(),currPacmanPosition,0,null,Maze);//δημιουργια αντικειμενου τυπου Node91709124 με ονομα test
			 PacmanUtilities.movePacman(copies[i],currPacmanPosition ,nextPositions); //εξομοιωση της κινησης του pacman με κλιση της συναρτησης movePacman
			 Node91709124 child = new  Node91709124 (avpos.get(i).getNodeMove(),currPacmanPosition,parent.getDepth() +1 ,parent, copies[i]);// δημιουργια ενος node,σαν παιδι του γονεα-node που ηρθε σαν ορισμα της συναρτησης createSubTreePacman
			child.setnodeEvaluation(test.getnodeEvaluation());//εξισωνουμε την τιμη του evaluate του child με αυτη του test
			child.setnodeXY(test.getNodeX(),test.getNodeY());//εξισωνουμε τις τιμες nodeX nodeY του child με αυτες του test
			 parent.setChildren(child); // τοποθετηση του child στο arraylist του parent με κληση της αντιστοιχης setter 
			 createSubTreeGhosts (child.getDepth()+1,child,copies[i],child.getCurrentGhostsPos()); 	//κληση της συναρτησης 	createSubTreeGhosts για τη δημιουργια του υποδεντρου των φαντασματων
	
		}
		
		
		
		
		

  }
 
  void createSubTreeGhosts (int depth, Node91709124 parent, Room[][] Maze, int[][] currGhostsPosition)
  {
	 
	  
	  ArrayList<int[][]> ghostAvMoves = new ArrayList<int[][]>();//δηλωση arraylist οπου θα αποθηκευτουν αργοτερα ολες οι δυνατες κινησεις των φαντασματων
	 
	  ghostAvMoves = PacmanUtilities.allGhostMoves(Maze,currGhostsPosition);//βαζουμε τις καταλληλες τιμες στο arraylist ghostAvMoves με κληση της συναρτησης PacmanUtilities.allGhostMoves
	  int[][] prevGhostPos=currGhostsPosition;//διατηρουμε την τωρινη θεση των φαντασματων για χρηση παρακατω
	  Room[][][] copies = new Room[ ghostAvMoves.size()][][];//δημιουργια πινακα τυπου room οπου θα αποθηκευτουν τα αντιγραφα του maze
	  
	  for(int i =0; i<ghostAvMoves.size();i++) {//for απο 0 εως τον αριθμο των δυνατων κινησεων των φαντασματων που δηλωνεται απο το size του παραπανω arraylist
			 copies[i]=PacmanUtilities.copy (Maze);//δημιουργια αντιγραφου και αποθηκευση του στηv i θεση του copies
			 
			 PacmanUtilities.moveGhosts(copies[i],currGhostsPosition,ghostAvMoves.get(i));//προσομοιωση της κινησης των φαντασματων με βαση την τετραδα κινησεων που οριζει η θεση i του arraylist			 
			  int[] pacmanPos= new int [2];
			  //βαζουμε στον πινακα pacmanPos τις συντεταγμενες του πακμαν στον αντιγραφο του maze που βαλαμε ως ορισμα κατα την κληση της createSubTreeGhost
			  pacmanPos[0]=parent.getNodeX();
			  pacmanPos[1]=parent.getNodeY();
			 Node91709124 newNode  = new  Node91709124(parent.getNodeMove(),pacmanPos,depth,parent,copies[i]);//δημιουργια του νεου node με βαση τις παραπανω κινησεις των φαντασματων
			 newNode.setnodeEvaluation(0);//μηδενιζουμε την τιμη του evaluation του παραπανω node
			 parent.setChildren(newNode);//θετουμε το newNode ως παιδι του parent (του αντικειμενου που μπηκε δηλαδη σαν ορισμα κατα την κληση αυτης της συναρτησης)
			 int ghostCounterCloser=0;// μεταβλητη που κραταει τον αριθμο των φαντασματων που πλησιασαν τον πακμαν
			 //ελεγχος ξεχωριστα για το καθε φαντασμα αν η κινηση που εκανε παραπανω καταφερε να τον φερει πιο κοντα στον πακμαν
			 if(Math.sqrt((prevGhostPos[0][0]-pacmanPos[0])*(prevGhostPos[0][0]-pacmanPos[0])+(prevGhostPos[0][1]-pacmanPos[1])*(prevGhostPos[0][1]-pacmanPos[1]))>(currGhostsPosition[0][0]-pacmanPos[0])*(currGhostsPosition[0][0]-pacmanPos[0])+(currGhostsPosition[0][1]-pacmanPos[1])*(currGhostsPosition[0][1]-pacmanPos[1]))ghostCounterCloser++;
			 if(Math.sqrt((prevGhostPos[1][0]-pacmanPos[0])*(prevGhostPos[1][0]-pacmanPos[0])+(prevGhostPos[1][1]-pacmanPos[1])*(prevGhostPos[1][1]-pacmanPos[1]))>(currGhostsPosition[1][0]-pacmanPos[0])*(currGhostsPosition[1][0]-pacmanPos[0])+(currGhostsPosition[1][1]-pacmanPos[1])*(currGhostsPosition[1][1]-pacmanPos[1]))ghostCounterCloser++;
			 if(Math.sqrt((prevGhostPos[2][0]-pacmanPos[0])*(prevGhostPos[2][0]-pacmanPos[0])+(prevGhostPos[2][1]-pacmanPos[1])*(prevGhostPos[2][1]-pacmanPos[1]))>(currGhostsPosition[2][0]-pacmanPos[0])*(currGhostsPosition[2][0]-pacmanPos[0])+(currGhostsPosition[2][1]-pacmanPos[1])*(currGhostsPosition[2][1]-pacmanPos[1]))ghostCounterCloser++;
			 if(Math.sqrt((prevGhostPos[3][0]-pacmanPos[0])*(prevGhostPos[3][0]-pacmanPos[0])+(prevGhostPos[3][1]-pacmanPos[1])*(prevGhostPos[3][1]-pacmanPos[1]))>(currGhostsPosition[3][0]-pacmanPos[0])*(currGhostsPosition[3][0]-pacmanPos[0])+(currGhostsPosition[3][1]-pacmanPos[1])*(currGhostsPosition[3][1]-pacmanPos[1]))ghostCounterCloser++;
			 //με τις παραπανω if ειδαμε ποσα φαντασματα πλησιασαν τον πακμαν και θετουμε την τιμη του evaluation του newNode με βαση αυτο και το nodeEvaluation του parent
			 newNode.setnodeEvaluation(ghostCounterCloser*(-12)+parent.getnodeEvaluation());
			 		 
			 
			 			 
		 } 
		  
		  
		  
		  
		  
	  }
	  
	 
  
  
  
  
  

  public int[] getPacPos (Room[][] Maze)
  {
    int[] pacmanPos = new int[2];
    for (int i = 0; i < PacmanUtilities.numberOfRows; i++) {
      for (int j = 0; j < PacmanUtilities.numberOfColumns; j++) {
        if (Maze[i][j].isPacman()) {
          pacmanPos[0] = i;
          pacmanPos[1] = j;
          return pacmanPos;
        }
      }
    }
    return pacmanPos;
  }

  public boolean[] comAvPos (Room[][] Maze, int[][] currentPos, int[] moves, int currentGhost)
  {

    boolean[] availablePositions = { true, true, true, true };

    int[][] newPos = new int[4][2];

    for (int i = 0; i < 4; i++) {

      if (Maze[currentPos[currentGhost][0]][currentPos[currentGhost][1]].walls[i] == 0) {
        availablePositions[i] = false;
        continue;
      }

      if (PacmanUtilities.flagColision(Maze, currentPos[currentGhost], i)) {
        availablePositions[i] = false;
      }

      else if (currentGhost == 0)
        continue;

      else {
        switch (i) {
        case Room.WEST:
          newPos[currentGhost][0] = currentPos[currentGhost][0];
          newPos[currentGhost][1] = currentPos[currentGhost][1] - 1;
          break;
        case Room.SOUTH:
          newPos[currentGhost][0] = currentPos[currentGhost][0] + 1;
          newPos[currentGhost][1] = currentPos[currentGhost][1];
          break;
        case Room.EAST:
          newPos[currentGhost][0] = currentPos[currentGhost][0];
          newPos[currentGhost][1] = currentPos[currentGhost][1] + 1;
          break;
        case Room.NORTH:
          newPos[currentGhost][0] = currentPos[currentGhost][0] - 1;
          newPos[currentGhost][1] = currentPos[currentGhost][1];

        }

        for (int j = (currentGhost - 1); j > -1; j--) {
          switch (moves[j]) {
          case Room.WEST:
            newPos[j][0] = currentPos[j][0];
            newPos[j][1] = currentPos[j][1] - 1;
            break;
          case Room.SOUTH:
            newPos[j][0] = currentPos[j][0] + 1;
            newPos[j][1] = currentPos[j][1];
            break;
          case Room.EAST:
            newPos[j][0] = currentPos[j][0];
            newPos[j][1] = currentPos[j][1] + 1;
            break;
          case Room.NORTH:
            newPos[j][0] = currentPos[j][0] - 1;
            newPos[j][1] = currentPos[j][1];
            // break;
          }

          if ((newPos[currentGhost][0] == newPos[j][0]) && (newPos[currentGhost][1] == newPos[j][1])) {

            availablePositions[i] = false;
            continue;
          }

          if ((newPos[currentGhost][0] == currentPos[j][0]) && (newPos[currentGhost][1] == currentPos[j][1]) && (newPos[j][0] == currentPos[currentGhost][0])
              && (newPos[j][1] == currentPos[currentGhost][1])) {

            availablePositions[i] = false;

          }
        }
      }
    }

    return availablePositions;
  }

  public int comBestPos (boolean[] availablePositions, int[] pacmanPosition, int[] currentPos)
  {

    int[] newVerticalDifference = new int[2];
    for (int i = 0; i < 2; i++)
      newVerticalDifference[i] = currentPos[i] - pacmanPosition[i];

    int[] distanceSquared = new int[4];

    for (int i = 0; i < 4; i++) {
      if (availablePositions[i] == true) {

        switch (i) {
        case Room.WEST:
          newVerticalDifference[1]--;
          break;
        case Room.SOUTH:
          newVerticalDifference[0]++;
          break;
        case Room.EAST:
          newVerticalDifference[1]++;
          break;
        case Room.NORTH:
          newVerticalDifference[0]--;
          break;
        }
        distanceSquared[i] = newVerticalDifference[0] * newVerticalDifference[0] + newVerticalDifference[1] * newVerticalDifference[1];
      } else
        distanceSquared[i] = PacmanUtilities.numberOfRows * PacmanUtilities.numberOfRows + PacmanUtilities.numberOfColumns * PacmanUtilities.numberOfColumns + 1;
    }

    int minDistance = distanceSquared[0];
    int minPosition = 0;

    for (int i = 1; i < 4; i++) {
      if (minDistance > distanceSquared[i]) {
        minDistance = distanceSquared[i];
        minPosition = i;
      }

    }

    return minPosition;
  }

  public int[] calculateNextGhostPosition (Room[][] Maze, int[][] currentPos)
  {

    int[] moves = new int[PacmanUtilities.numberOfGhosts];

    int[] pacmanPosition = new int[2];

    pacmanPosition = getPacPos(Maze);
    for (int i = 0; i < PacmanUtilities.numberOfGhosts; i++) {
      moves[i] = comBestPos(comAvPos(Maze, currentPos, moves, i), pacmanPosition, currentPos[i]);
    }

    return moves;

  }

  public boolean[] checkCollision (int[] moves, int[][] currentPos)
  {
    boolean[] collision = new boolean[PacmanUtilities.numberOfGhosts];

    int[][] newPos = new int[4][2];

    for (int i = 0; i < moves.length; i++) {

      if (moves[i] == 0) {
        if (currentPos[i][1] > 0) {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = currentPos[i][1] - 1;
        } else {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = PacmanUtilities.numberOfColumns - 1;
        }

      } else if (moves[i] == 1) {
        if (currentPos[i][0] < PacmanUtilities.numberOfRows - 1) {
          newPos[i][0] = currentPos[i][0] + 1;
          newPos[i][1] = currentPos[i][1];
        } else {
          newPos[i][0] = 0;
          newPos[i][1] = currentPos[i][1];
        }
      } else if (moves[i] == 2) {
        if (currentPos[i][1] < PacmanUtilities.numberOfColumns - 1) {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = currentPos[i][1] + 1;
        } else {
          newPos[i][0] = currentPos[i][0];
          newPos[i][1] = 0;

        }
      } else {
        if (currentPos[i][0] > 0) {
          newPos[i][0] = currentPos[i][0] - 1;
          newPos[i][1] = currentPos[i][1];
        } else {

          newPos[i][0] = PacmanUtilities.numberOfRows - 1;
          newPos[i][1] = currentPos[i][1];

        }
      }

      collision[i] = false;
    }

    for (int k = 0; k < moves.length; k++) {

    }

    for (int i = 0; i < moves.length; i++) {
      for (int j = i + 1; j < moves.length; j++) {
        if (newPos[i][0] == newPos[j][0] && newPos[i][1] == newPos[j][1]) {

          collision[j] = true;
        }

        if (newPos[i][0] == currentPos[j][0] && newPos[i][1] == currentPos[j][1] && newPos[j][0] == currentPos[i][0] && newPos[j][1] == currentPos[i][1]) {

          collision[j] = true;
        }

      }

    }
    return collision;
  }

}
