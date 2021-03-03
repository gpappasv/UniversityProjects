/* Γιώργος-Εφραίμ Παππάς ΑΕΜ:9124 gpappasv@ece.auth.gr 6970650963
 * Ναπολέων Παπουτσάκης  ΑΕΜ:9170 napoleop@ece.auth.gr 6974331167
  
 */


package gr.auth.ee.dsproject.pacman;

/**
 * <p>
 * Title: DataStructures2006
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

  public int calculateNextPacmanPosition (Room[][] Maze, int[] currPosition)
  {

    int newDirection = -1;

    while (newDirection == -1) {

      int temp_dir = (int) (4 * Math.random());

      if (Maze[currPosition[0]][currPosition[1]].walls[temp_dir] == 1) {
        newDirection = temp_dir;
      }

    }
    step++;

    return newDirection;
  }

  // THIS IS THE FUNCTION TO IMPLEMENT!!!!!!
  //Συναρτηση για τον καθορισμο των εγκυρων κινησεων των φαντασματων.
  public int[] calculateNextGhostPosition (Room[][] Maze, int[][] currentPos)
  {

	  int [] ghostsArray = new int[PacmanUtilities.numberOfGhosts];// δημιουργια πινακα με θεσεις οσες και ο αριθμος ο φαντασματων.
	 //σαρωση του πινακα ghostsArray με χρηση του βρογχου for.
	  for(int i=0;i<PacmanUtilities.numberOfGhosts;i++) {
		  ghostsArray[i] = (int)( 4* Math.random()); //γεμισμα του πινακα με τυχαιες τιμες απο 0-3
	  }
	  //βρογχος for για τον ελεγχο της εγκυροτητας των κινησεων που καθοριζει ο πινακας ghostsArray.
	  for(int i=0;i<PacmanUtilities.numberOfGhosts;i++) {
		  int x= currentPos[i][0];// δημιουργια μεταβλητης για την αποθηκευση της τιμης της πρωτης στηλης του πινακα currentPos που περιεχει τις συντεταγμενες της θεσης του καθε φαντασματος.
		  int y = currentPos[i][1];// δημιουργια μεταβλητης για την αποθηκευση της τιμης της δευτερης στηλης του πινακα currentPos που περιεχει τις συντεταγμενες της θεσης του καθε φαντασματος.
		   boolean [] arr = checkCollision(ghostsArray, currentPos);// δημιουργια  πινακα τυπου boolean για την αποθηκευση του πινακα που επιστρεφει η συναρτηση checkCollision.
			int p = Maze[x][y].walls[ghostsArray[i]];// δημιουργια μεταβλητης τυπου ακεραιου για την αποθηκευση της τιμης της  Maze[x][y].walls[ghostsArray[i]] 
			 // βρογχος while που τρεχει οσο η κινηση του φαντασματος δεν ειναι εγκυρη. 
			while(p==0 || arr[i]) {
				  ghostsArray[i] = (int)( 4* Math.random()); // αποδοση αλλης τυχαιας τιμης απο 0-3
				  p = Maze[x][y].walls[ghostsArray[i]];// ενημερωση της τιμης της μεταβλητης p ωστε να μην ειναι ατερμνος ο βρογχος.
				   arr = checkCollision(ghostsArray, currentPos);//  ενημερωση της τιμης της μεταβλητης arr ωστε να μην ειναι ατερμνος ο βρογχος.
			  }
			
			
		  
	  }
  
	  	return ghostsArray;// επιστροφη του πινακα με τις εγκυρες κινησεις των φαντασματων
  
  }

  public boolean[] checkCollision (int[] moves, int[][] currentPos)
  {
    boolean[] collision = new boolean[PacmanUtilities.numberOfGhosts];

    int[][] newPos = new int[4][2];

    for (int i = 0; i < moves.length; i++) {

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

    for (int i = 0; i < moves.length; i++) {
      for (int j = i + 1; j < moves.length; j++) {
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

}
