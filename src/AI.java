import com.sun.org.apache.xerces.internal.parsers.IntegratedParserConfiguration;

/**
 * An instance represents a Solver that intelligently determines Moves using
 * algorithm Minimax.
 */
public class AI implements Solver {
	public enum Algorithm {
		MINIMAX, ALPHABETA;
	}

	public enum Evaluation {
		UTILITY, CONNECTIVITY;
	}

	private Board.Player player; // the current player

	/** The depth of the search in the game space when evaluating moves. */
	private int depth;

	private Algorithm algorithm;
	private Evaluation evaluation;

	private static Board.Player maximizer;
	private static Board.Player minimizer;

	private final int[] counterArray = {0,1,2,3,2,1,0};
	private static int nodeSearched = 0;
	
	private final int[][] utilityTable = { { 3, 4, 5, 7, 5, 4, 3 }, { 4, 6, 8, 10, 8, 6, 4 },
			{ 5, 8, 11, 13, 11, 8, 5 }, { 5, 8, 11, 13, 11, 8, 5 }, { 4, 6, 8, 10, 8, 6, 4 }, { 3, 4, 5, 7, 5, 4, 3 } };

	/**
	 * Constructor: an instance with player p who searches to depth d when
	 * searching the game space for moves.
	 */
	public AI(Board.Player p, int d, Algorithm a, Evaluation e) {
		player = p;
		depth = d;
		algorithm = a;
		evaluation = e;
	}

	/** See Solver.getMoves for the specification. */
	public @Override Move[] getMoves(Board b) {
		State s = new State(player, b, null);
		if (algorithm == Algorithm.MINIMAX) {
			AI currentPlayer = new AI(player, depth, algorithm, evaluation);
			AI.minimax(currentPlayer, s);
		}

		else if (algorithm == Algorithm.ALPHABETA) {
			AI currentPlayer = new AI(player, depth, algorithm, evaluation);
			AI.alphaBeta(currentPlayer, s);
		}
		int winScore = s.getValue();
		State[] children = s.getChildren();

		int size = 0;
		for (State c : children)
			if (winScore == c.getValue()) {
				size++;
			}
		Move[] moves = new Move[size];
		int i = 0;
		for (State c : children)
			if (winScore == c.getValue()) {
				moves[i] = c.getLastMove();
				i++;
			}
		return moves.length == 0 ? b.getPossibleMoves(player) : moves;
	}

	/** Call minimax in ai with state s. */
	public static void minimax(AI ai, State s) {
		long startTime = System.nanoTime();
		nodeSearched = 0;
		maximizer = s.getPlayer();
		minimizer = (maximizer == Board.Player.RED) ? Board.Player.YELLOW : Board.Player.RED;
		ai.minimax(s, ai.depth);
		if (ai.player == Board.Player.RED){
//			System.out.println((System.nanoTime() - startTime)/1000000.0);
			System.out.println(nodeSearched);
		}
	}

	public int minimax(State currentState, int depth) {
		if (depth == 0 || currentState.getBoard().hasConnectFour() == null)
			currentState.initializeChildren();
		State[] children = currentState.getChildren();
		int value = 0;

		// Case #1: Current node is a leaf node.
		if (children.length == 0 || depth == 0) {
			nodeSearched++;
			if (evaluation == Evaluation.UTILITY)
				value = evaluateUtility(currentState.getBoard());
			else if (evaluation == Evaluation.CONNECTIVITY)
				value = evaluateConnectivity(currentState.getBoard());
			currentState.setValue(value);
			return value;

			// Case #2: Current node is a maximizer.
		} else if (currentState.getPlayer() == maximizer) {
			nodeSearched++;
			value = Integer.MIN_VALUE;
			for (State child : children) {
				int childValue = minimax(child, depth - 1);
				if (childValue > value)
					value = childValue;
			}
			currentState.setValue(value);
			return value;

			// Case #3: Current node is a minimizer.
		} else {
			nodeSearched++;
			value = Integer.MAX_VALUE;
			for (State child : children) {
				int childValue = minimax(child, depth - 1);
				if (childValue < value)
					value = childValue;
			}
			currentState.setValue(value);
			return value;
		}
	}

	/** Call alpha-beta search in ai with state s. */
	public static void alphaBeta(AI ai, State s) {
		long startTime = System.nanoTime();
		nodeSearched = 0;
		maximizer = s.getPlayer();
		minimizer = (maximizer == Board.Player.RED) ? Board.Player.YELLOW : Board.Player.RED;
		ai.alphaBeta(s, ai.depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		if (ai.player == Board.Player.RED){
//			System.out.println((System.nanoTime() - startTime)/1000000.0);
			System.out.println(nodeSearched);
		}
	}

	public int alphaBeta(State currentState, int depth, int alpha, int beta) {
		// Initialize children iff not at max defined depth or there's no
		// winner.
		if (depth == 0 || currentState.getBoard().hasConnectFour() == null)
			currentState.initializeChildren();
		State[] children = currentState.getChildren();
		int value = 0;
		nodeSearched++;
		// Case #1: Current node is a leaf node.
		if (children.length == 0 || depth == 0) {
			if (evaluation == Evaluation.UTILITY)
				value = evaluateUtility(currentState.getBoard());
			else if (evaluation == Evaluation.CONNECTIVITY)
				value = evaluateConnectivity(currentState.getBoard());
			currentState.setValue(value);
			return value;

			// Case #2: Current node is a maximizer.
		} else if (currentState.getPlayer() == maximizer) {
			value = alpha;
			for (State child : children) {
				int childValue = alphaBeta(child, depth - 1, value, beta);
				if (childValue > value)
					value = childValue;
				if (value > beta) {
					currentState.setValue(beta);
					return beta;
				}
			}
			currentState.setValue(value);
			return value;

			// Case #3: Current node is a minimizer.
		} else {
			value = beta;
			for (State child : children) {
				int childValue = alphaBeta(child, depth - 1, alpha, value);
				if (childValue < value)
					value = childValue;
				if (value < alpha) {
					currentState.setValue(alpha);
					return alpha;
				}
			}
			currentState.setValue(value);
			return value;
		}
	}

	public int evaluateUtility(Board b) {
		Board.Player winner = b.hasConnectFour();
		if (winner != null && winner == player)
			return 1000000;
		else if (winner != null && winner != player)
			return -1000000;
		else {
			int utility = 138;
			for (int row = 0; row < Board.NUM_ROWS; row++) {
				for (int col = 0; col < Board.NUM_COLS; col++) {
					Board.Player tileColor = b.getTile(row, col);
					if (tileColor != null) {
						if (tileColor == player)
							utility += utilityTable[row][col];
						else
							utility -= utilityTable[row][col];
					}
				}
			}
			return utility;
		}
	}

	public int evaluateConnectivity(Board b) {
		int utility = 0;
		int counter = 0;
		for (Board.Player[] loc : b.winLocations()) {
			if (loc[0] != null && loc[0] == loc[1] && loc[0] == loc[2] && loc[0] == loc[3]) {
				if (loc[0].player() != player)
					utility -= 1000000;
				else if (loc[0].player() == player)
					utility += 1000000;
			} else if (loc[0] != null && loc[0] == loc[1] && loc[0] == loc[2]) {
				if (loc[0].player() != player)
					utility -= 8;
				else if (loc[0].player() == player)
					utility += 8;
			} else if (loc[0] != null && loc[0] == loc[1]) {
				if (loc[0].player() != player)
					utility -= 4;
				else if (loc[0].player() == player)
					utility += 4;
			} else {
				utility += counterArray[counter % 6];
				counter++;
			}
		}
		return utility;
	}
}
