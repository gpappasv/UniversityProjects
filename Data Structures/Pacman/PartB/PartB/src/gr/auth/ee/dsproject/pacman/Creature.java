/* Γιώργος-Εφραίμ Παππάς ΑΕΜ:9124 gpappasv@ece.auth.gr 6970650963
 * Ναπολέων Παπουτσάκης  ΑΕΜ:9170 napoleop@ece.auth.gr 6974331167
  
 */

package gr.auth.ee.dsproject.pacman;
import java.util.ArrayList;


import gr.auth.ee.dsproject.node.*;
/**
 * <p>Title: DataStructures2011</p>
 *
 * <p>Description: Data Structures project: year 2011-2012</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: A.U.Th.</p>
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

  public int calculateNextPacmanPosition (Room[][] Maze, int[] currPosition)
  {
	int moveToReturn=(int)(Math.random()*4);//τυχαια αρχικοποιηση της μεταβλητης που θα δηλωσει την επομενη κινηση του πακμαν σε περιπτωση που ολες οι πιθανες κινησεις θεωρουνται αδυνατες (δηλαδη σε περιπτωση που τον περικυκλωσουν τα φαντασματα
	
	
	
	ArrayList<Node91709124> avpos=new ArrayList <Node91709124>();//δημιουργια arraylist αντικειμενων που θα αποθηκευτουν τα αντικειμενα τυπου Node91709124 οπου θα περιεχουν κινηση που θεωρειται επιτρεπτη
	
	Node91709124[] obj=new Node91709124[4];//πινακας αντικειμενων τυπου Node91709124 τεσσαρων θεσεων
	obj[0]=new Node91709124(0,currPosition,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 0
	obj[1]=new Node91709124(1,currPosition,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 1
	obj[2]=new Node91709124(2,currPosition,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 2
	obj[3]=new Node91709124(3,currPosition,Maze);//αντικειμενο Node91709124 με κινηση προς την κατευθυνση που δηλωνει το 3
	
	for(int i=0;i<4;i++){//for που τρεχει απο μηδεν εως 3 ωστε να καλυψουμε ολες τις πιθανες κατευθυνσεις
		if(Maze[currPosition[0]][currPosition[1]].walls[i]==1)//ελεγχος αν στην κατευθυνση που δηλωνει η τιμη του i υπαρχει τοιχος
			if(Maze[obj[i].getNodeX()][obj[i].getNodeY()].isGhost()==false)//ελεγχος αν στην θεση που προκειται να παει ο πακμαν (που δηλωνει η τιμη του i) προκειται να βρεθει φαντασμα (ωστε να απορριφθει σε περιπτωση που συγκρουονται πακμαν με φαντασμα)
				avpos.add(obj[i]);//αν στην κατευθυνση που δηλωνει το i δεν προκειται να βρεθει φαντασμα, αποθηκευουμε το αντιστοιχο αντικειμενο με την αντιστιχη τιμη κατευθυνσης στην arraylist καθως αποτελει μια πιθανη κινηση του πακμαν. Οποια αντικειμενα απο αυτα που δημιουργησαμε παραπανω αποθηκευτουν τελικα στην arraylist, οι κατευθυνσεις αυτων θα αξιολογηθουν αργοτερα
		
	}
		
	double[] evals=new double[avpos.size()];//πινακας που θα δεχθει τις τιμες της αξιολογησης των κατευθυνσεων που κριθηκαν αποδεκτες προηγουμενως (μεγεθους ισου με το πληθος αντικειμενων της arraylist
	if(avpos.size()>0){//αν τελικα αποθηκευτηκε εστω και ενα αντικειμενο στον arraylist μπορουμε να προχωρησουμε στην επομενη for (αλλιως θα τρεξουμε σε πινακα μηδενικου μεγεθος, αρα θα εχουμε σφαλμα)
	for(int i=0;i<avpos.size();i++){
		evals[i]=avpos.get(i).getnodeEvaluation();//αποθηκευση της αξιολογησης της εκαστοτε δυνατης κινησης μεσω της getnodeEvaluation που επιστρεφει την evaluation μεταβλητη του εκαστοτε αντικειμενου που αποθηκευτηκε στην arraylist
		
	}
	
	double max=evals[0];//η μεταβλητη max θα αποθηκευσει τελικα την μεγιστη τιμη απο τις αξιολογησεις. εδω γινεται μια απλη αρχικοποιηση της με το πρωτο στοιχειο του πινακα του οποιου το μεγιστο θα υπολογισουμε
	int indexx=0;
	for(int i=0;i<avpos.size();i++){//ευρεση της μεγιστης τιμης του πινακα evals[] και αποθηκευση της θεσης οπου βρισκεται στην μεταβλητη indexx
		if(max<evals[i]){
			max=evals[i];
			indexx=i;
		}
	}
	moveToReturn=avpos.get(indexx).getNodeMove();//αποθηκευση της κατευθυνσης που δηλωνει το αντικειμενο, που, τελικα, εχει την μεγαλυτερη τιμη evaluation (παιρνουμε δηλαδη την τιμη της μεταβλητης NodeMove του αντικειμενου που βρισκεται στην θεση indexx του arraylist avpos
	avpos.clear();//εκκαθαριση του περιεχομενου του arraylist
	}
	
	
	
	
    
    
    return moveToReturn;//επιστροφη της τελικης τιμης που θα δηλωσει την κατευθυνση του πακμαν

  }

  public int[] calculateNextGhostPosition (Room[][] Maze, int[][] currentPos)
  {

    int[] moves = new int[4];
    int[] eval = new int[4];
    boolean[] ghostColision = new boolean[PacmanUtilities.numberOfGhosts];

    // System.out.println("Ghosts Current Positions ");
    for (int i = 0; i < PacmanUtilities.numberOfGhosts; i++) {
      // System.out.println("ghost x = "+currentPos[i][0]+" ghost y = "+ currentPos[i][1]);
    }
    // System.out.println(" ");

    int pacmanX = 0;
    int pacmanY = 0;

    for (int i = 0; i < PacmanUtilities.numberOfRows; i++)
      for (int j = 0; j < PacmanUtilities.numberOfColumns; j++) {
        if (Maze[i][j].isPacman()) {
          pacmanX = i;
          pacmanY = j;

        }
      }

    // pacmanX = 0;
    // pacmanY = 0;

    // System.out.println(pacmanX);
    // System.out.println(pacmanY);
    // System.out.println("");

    eval = newGhostDir(pacmanX, pacmanY, currentPos);
    ghostColision = checkCollision(eval, currentPos);

    for (int i = 0; i < PacmanUtilities.numberOfGhosts; i++) {
      while (!evaluateNewDirection(currentPos, i, eval, Maze) || ghostColision[i] || !checkFlag(currentPos, i, eval, Maze)) {
        eval[i] = (int) (4 * Math.random());
        ghostColision = checkCollision(eval, currentPos);
      }
    }

    moves = eval;

    // for (int i = 0; i < PacmanUtilities.numberOfGhosts; i++)
    // System.out.println("direction of ghost "+i+" = " +moves[i]);
    // System.out.println(" ");

    return moves;

  }

  public int[] newGhostDir (int pacX, int pacY, int[][] currentPos)
  {
    int[] tempDirections = new int[4];

    for (int k = 0; k < PacmanUtilities.numberOfGhosts; k++) {
      if (pacX < currentPos[k][0]) // pacman norther than ghost
      {
        tempDirections[k] = Room.NORTH;
      }

      if (pacX > currentPos[k][0]) // pacman souther than ghost
      {
        tempDirections[k] = Room.SOUTH;
      }

      if (pacY > currentPos[k][1]) // pacman easter than ghost
      {
        tempDirections[k] = Room.EAST;
      }

      if (pacY < currentPos[k][1]) // pacman wester than ghost
      {
        tempDirections[k] = Room.WEST;
      }
    }

    return tempDirections;
  }

  public boolean evaluateNewDirection (int[][] curPos, int i, int[] direction, Room[][] Maze)
  {
    boolean validChoice = true;

    if (Maze[curPos[i][0]][curPos[i][1]].walls[direction[i]] == 0)
      validChoice = false;

    return validChoice;

  }

  public boolean checkFlag (int[][] curPos, int i, int[] direction, Room[][] Maze)
  {
    boolean validChoice = true;

    if (direction[i] == Room.NORTH) {
      if (Maze[curPos[i][0] - 1][curPos[i][1]].isFlag()) {
        validChoice = false;
      }
    }

    if (direction[i] == Room.SOUTH) {
      if (Maze[curPos[i][0] + 1][curPos[i][1]].isFlag()) {
        validChoice = false;
      }
    }

    if (direction[i] == Room.EAST) {
      if (Maze[curPos[i][0]][curPos[i][1] + 1].isFlag()) {
        validChoice = false;
      }
    }

    if (direction[i] == Room.WEST) {
      if (Maze[curPos[i][0]][curPos[i][1] - 1].isFlag()) {
        validChoice = false;
      }
    }
    return validChoice;
  }

  public boolean[] checkCollision (int[] moves, int[][] currentPos)
  {
    boolean[] collision = new boolean[PacmanUtilities.numberOfGhosts];

    int[][] newPos = new int[4][2];

    for (int i = 0; i < PacmanUtilities.numberOfGhosts; i++) {

      if (moves[i] == 0) {
        newPos[i][0] = currentPos[i][0];
        newPos[i][1] = currentPos[i][1] - 1;
      } else if (moves[i] == 1) {
        newPos[i][0] = currentPos[i][0] + 1;
        newPos[i][1] = currentPos[i][1];
      } else if (moves[i] == 2) {
        newPos[i][0] = currentPos[i][0];
        newPos[i][1] = currentPos[i][1] + 1;
      } else {
        newPos[i][0] = currentPos[i][0] - 1;
        newPos[i][1] = currentPos[i][1];
      }

      collision[i] = false;
    }

    for (int k = 0; k < moves.length; k++) {
      // System.out.println("Ghost " + k + " new Position is (" + newPos[k][0] + "," + newPos[k][1] + ").");
    }

    for (int i = 0; i < PacmanUtilities.numberOfGhosts; i++) {
      for (int j = i + 1; j < PacmanUtilities.numberOfGhosts; j++) {
        if (newPos[i][0] == newPos[j][0] && newPos[i][1] == newPos[j][1]) {
          // System.out.println("Ghosts " + i + " and " + j + " are colliding");
          collision[j] = true;
        }

        if (newPos[i][0] == currentPos[j][0] && newPos[i][1] == currentPos[j][1] && newPos[j][0] == currentPos[i][0] && newPos[j][1] == currentPos[i][1]) {
          // System.out.println("Ghosts " + i + " and " + j + " are colliding");
          collision[j] = true;
        }

      }

    }
    return collision;
  }

  public int newPacmanDir (int pacX, int pacY, int[] currentPos)
  {
    int tempDirections = 5;

    if (pacX < currentPos[0]) // pacman norther than ghost
    {
      tempDirections = Room.NORTH;
    }

    if (pacX > currentPos[0]) // pacman souther than ghost
    {
      tempDirections = Room.SOUTH;
    }

    if (pacY > currentPos[1]) // pacman easter than ghost
    {
      tempDirections = Room.EAST;
    }

    if (pacY < currentPos[1]) // pacman wester than ghost
    {
      tempDirections = Room.WEST;
    }

    return tempDirections;
  }

}
