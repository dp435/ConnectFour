/** Runs the Connect Four game. */
public class Game {

	private Solver player1;   // The first player
	private Solver player2;  // The second player
	private Board board;     // the board
	private Solver activePlayer;  // The possible moves to the player whose turn it is
	private GUI gui;
	private Board.Player winner;  // null

	//Change this if you would like a delay between plays
	private static final long SLEEP_INTERVAL= 0; //in milliseconds

	/** Have the computer play against itself, putting output int the default
	 * Java console.
	 * You can also have your new AI player play from here, with no GUI output.
	 * It may be useful for testing.
	 * If you want a human to play, use GUI.main.
	 */
	public static void main(String[] args) {
		/* p1 is the first player, p2 is the second player. This is set to
		 * have to Dummy players--playing randomly.
		 * To play the game with your own AI object, use assignments like you
		 * see in the comments after these two assignments. In those assignments,
		 * the second argument of the constructor is the depth to which AI
		 * searches the game space. */
		/**
        Solver p1= new Dummy(Board.Player.RED);
        Solver p2= new Dummy(Board.Player.YELLOW); 

        //Solver p1= new AI(Board.Player.RED, 5);
        //Solver p2= new AI(Board.Player.YELLOW, 5);

        Game game= new Game(p1, p2);
        game.runGame();

        /* When testing, you may want to comment out all the above statements
		 */
	
	}
		

	/* Test case for getPossibleMoves: empty board 
	 * Expected outcome: 7 possibilities */
	public static void emptyPossible() {
		Board b= new Board();
		Move[] moves= b.getPossibleMoves(Board.Player.RED);
		for (Move m : moves) {
			System.out.println(m);
		}
	}

	/* Test case for getPossibleMoves: partially filled board w/o winner
	 * Expected outcome: NUM_COLS - # of full cols */
	public static void partialPossible() {
		Board b= new Board();
		fillColumn(b, Board.Player.RED, 0);
		fillColumn(b, Board.Player.RED, 2);
		b.makeMove(new Move(Board.Player.YELLOW, 3));
		fillColumn(b, Board.Player.RED, 4);
		fillColumn(b, Board.Player.RED, 6);
		Move[] moves= b.getPossibleMoves(Board.Player.RED);
		for (Move m : moves) {
			System.out.println(m);
		}
	}

	/* Test case for getPossibleMoves: full board w/o winner
	 * Expected outcome: no possible moves */
	public static void fullPossible() {
		Board b= new Board();
		fillColumn(b, Board.Player.RED, 0);
		fillColumn(b, Board.Player.RED, 1);
		fillColumn(b, Board.Player.YELLOW, 2);
		fillColumn(b, Board.Player.YELLOW, 3);
		fillColumn(b, Board.Player.RED, 4);
		fillColumn(b, Board.Player.RED, 5);
		fillColumn(b, Board.Player.YELLOW, 6);
		Move[] moves= b.getPossibleMoves(Board.Player.RED);
		for (Move m : moves) {
			System.out.println(m);
		}
	}

	/* Test case for getPossibleMoves: board w/ winner
	 * Expected outcome: no possible moves */
	public static void winnerPossible() {
		Board b= new Board();
		b.makeMove(new Move(Board.Player.RED, 0));
		b.makeMove(new Move(Board.Player.YELLOW, 0));
		b.makeMove(new Move(Board.Player.RED, 1));
		b.makeMove(new Move(Board.Player.YELLOW, 1));
		b.makeMove(new Move(Board.Player.RED, 2));
		b.makeMove(new Move(Board.Player.YELLOW, 2));
		b.makeMove(new Move(Board.Player.RED, 3));
		b.makeMove(new Move(Board.Player.YELLOW, 3));
		b.makeMove(new Move(Board.Player.RED, 4));
		Move[] moves= b.getPossibleMoves(Board.Player.RED);
		for (Move m : moves) {
			System.out.println(m);
		}
	}

	/* Test case for initializeChildren: empty board
	 * Expected outcome: 7 children */
	public static void emptyChildren() {
		Board b= new Board();
		State s= new State(Board.Player.YELLOW, b, null);
		s.initializeChildren();
		for (State scan : s.getChildren()) {
			System.out.println(scan);
		}
	}
	
	/* Test case for initializeChildren: partially filled board w/o winner
	 * Expected outcome: 4 children */
	public static void partialChildren() {
		Board b= new Board();
		fillColumn(b, Board.Player.RED, 0);
		b.makeMove(new Move(Board.Player.YELLOW, 3));
		fillColumn(b, Board.Player.RED, 4);
		fillColumn(b, Board.Player.RED, 6);
		State s= new State(Board.Player.YELLOW, b, null);
		s.initializeChildren();
		for (State scan : s.getChildren()) {
			System.out.println(scan);
		}
	}
	
	/* Test case for initializeChildren: full board w/o winner
	 * Expected outcome: no children */
	public static void fullChildren() {
		Board b= new Board();
		b.makeMove(new Move(Board.Player.RED, 0));
		b.makeMove(new Move(Board.Player.YELLOW, 0));
		b.makeMove(new Move(Board.Player.RED, 1));
		b.makeMove(new Move(Board.Player.YELLOW, 1));
		b.makeMove(new Move(Board.Player.RED, 2));
		b.makeMove(new Move(Board.Player.YELLOW, 2));
		b.makeMove(new Move(Board.Player.RED, 3));
		b.makeMove(new Move(Board.Player.YELLOW, 3));
		b.makeMove(new Move(Board.Player.RED, 4));
		State s= new State(Board.Player.YELLOW, b, null);
		s.initializeChildren();
		for (State scan : s.getChildren()) {
			System.out.println(scan);
		}
	}
	
	/* Test case for initializeChildren: board w/ winner
	 * Expected outcome: no children */
	public static void winnerChildren() {
		Board b= new Board();
		b.makeMove(new Move(Board.Player.RED, 0));
		b.makeMove(new Move(Board.Player.YELLOW, 0));
		b.makeMove(new Move(Board.Player.RED, 1));
		b.makeMove(new Move(Board.Player.YELLOW, 1));
		b.makeMove(new Move(Board.Player.RED, 2));
		b.makeMove(new Move(Board.Player.YELLOW, 2));
		b.makeMove(new Move(Board.Player.RED, 3));
		b.makeMove(new Move(Board.Player.YELLOW, 3));
		b.makeMove(new Move(Board.Player.RED, 4));
		State s= new State(Board.Player.YELLOW, b, null);
		s.initializeChildren();
		for (State scan : s.getChildren()) {
			System.out.println(scan);
		}
	}

	/** Fill column c of board b, starting with player p.
	 * Precondition: p is not null and 0 <= c < NUM_COLS */
	public static void fillColumn(Board b, Board.Player p, int c) {
		while (b.getPlayer(0, c) == null) {
			b.makeMove(new Move(p, c));
			p= p.opponent();
		}
	}

	/** ************** Do not change anything below here ***************/

	/** Construct a new Game with p1 as the first player, p2 as the second player,
	 *  with b as the current Board state, and with it being p's turn to play
	 *  true means player 1, false means player 2). */
	public Game(Solver p1, Solver p2, Board b, boolean p) {
		this(p1, p2);
		board= b;
		activePlayer= (p ? player1 : player2);
	}

	/** Constructor: a new Game with p1 as the first player and p2 as the second player.
	 * It is p1's turn to play. 
	 *  Precondition: p1 and p2 are different */
	public Game(Solver p1, Solver p2) {
		player1= p1;
		player2= p2;
		board= new Board();
		activePlayer= player1;
	}

	/** Attach GUI gui to this Game model. */
	public void setGUI(GUI gui) {
		this.gui= gui;
	}

	/** Notify this Game that column col has been clicked by a user. */
	public void columnClicked(int col) {
		if (activePlayer instanceof Human) {
			((Human) activePlayer).columnClicked(col);
		}
	}

	/** Run the game until finished. If GUI is not initialized, the output will be 
	 *  sent to the console. */
	public void runGame() {
		while (!isGameOver()) {
			//Checking to see that the move can be made (not overflowing a column)
			boolean moveIsSafe= false;
			Move nextMove= null;
			while (!moveIsSafe) {
				Move[] bestMoves= activePlayer.getMoves(board);
				if (bestMoves.length == 0) {
					gui.setMsg("Game cannot continue until a Move is produced.");
					continue;
				} else {
					nextMove= bestMoves[0];
				}
				if (board.getTile(0,nextMove.getColumn()) == null) {
					moveIsSafe= true; 
				} else {
					gui.setMsg("Illegal Move: Cannot place disc in full column. Try again.");
				}
			}

			board.makeMove(nextMove);
			if (gui == null) {
				System.out.println(nextMove);
				System.out.println(board);
			} else {
				gui.updateGUI(board, nextMove);
			}
			activePlayer= (activePlayer == player1 ? player2 : player1);

			// The following code causes a delay so that you can easily view the plays
			// being made by the AIs
			try {
				Thread.sleep(SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (gui == null) {
			if (winner == null) {
				System.out.println("Tie game!");
			} else {
				System.out.println(winner + " won the game!!!");
			}
		} else {
			gui.notifyGameOver(winner);
		}
	}

	/** Return true iff this game is over. If the game
	 *  is over, set the winner field to the winner; if no winner
	 *  set the winner to null. */
	public boolean isGameOver() {
		winner= board.hasConnectFour();
		if (winner != null) return true;

		// if these is an unfilled tile, return false;
		for (int r= 0; r < Board.NUM_ROWS; r= r+1) {
			for (int c= 0; c < Board.NUM_COLS; c= c+1) {
				if (board.getTile(r, c) == null)
					return false;
			}
		}

		return true;
	}

}
