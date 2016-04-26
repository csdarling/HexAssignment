package hexgame;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author Catherine Darling
 *
 */
public class GameManager implements GameManagerInterface {
	private HashMap<Piece, PlayerInterface> players;
	private Board board;
	
	public GameManager() {
		players = new HashMap<Piece, PlayerInterface>();
		board = new Board();
	}

	/* (non-Javadoc)
	 * @see hexgame.GameManagerInterface#specifyPlayer(hexgame.PlayerInterface, hexgame.Piece)
	 */
	@Override
	public boolean specifyPlayer(PlayerInterface player, Piece colour)
			throws InvalidColourException, ColourAlreadySetException {
		
		// Check if the colour is already allocated to a player
		if (players.containsKey(colour)) {
			throw new ColourAlreadySetException();
		}
		
		boolean isSet = player.setColour(colour);
		players.put(colour, player);
		return isSet;
	}

	/* (non-Javadoc)
	 * @see hexgame.GameManagerInterface#boardSize(int, int)
	 */
	@Override
	public boolean boardSize(int sizeX, int sizeY) throws InvalidBoardSizeException, BoardAlreadySizedException {
		return board.setBoardSize(sizeX, sizeY);
	}

	/* (non-Javadoc)
	 * @see hexgame.GameManagerInterface#playGame()
	 */
	@Override
	public boolean playGame() {
		// TODO 	
		Scanner in = setupScanner();
		setBoardDimensions(in);
		Piece[][] boardView = null;
		try {
			boardView = board.getBoardView();
		} catch (NoBoardDefinedException e) {
			e.printStackTrace();
		}
		setPlayerColours(in);
		
		ArrayList<Piece> colours = new ArrayList<Piece>();
		colours.add(board.getFirstMove());
		colours.add(getSecondMove(board.getFirstMove()));
		
		int moves = 0;
		Piece winner = Piece.UNSET;
		while (winner == Piece.UNSET) {
			MoveInterface move = new Move();
			Piece activeColour = colours.get(moves % 2);
			Piece inactiveColour = colours.get((moves + 1) % 2);
			printBoard(boardView);
			
			try {
				move = players.get(activeColour).makeMove(boardView);
			} catch (NoValidMovesException e) {
				System.out.println(e);
				board.setWinner(inactiveColour);
			}
			
			if (move.hasConceded()) {
				board.setWinner(inactiveColour);
			}
			else {
				try {
					board.placePiece(activeColour, move);
				} catch (PositionAlreadyTakenException | InvalidPositionException e) {
					System.out.println(e);
					continue;
				} catch (InvalidColourException | NoBoardDefinedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				winner = board.gameWon();
			} catch (NoBoardDefinedException e) {
				e.printStackTrace();
			}
			moves++;
		}
		
		try {
			printBoard(board.getBoardView());
		} catch (NoBoardDefinedException e) {
			e.printStackTrace();
		}
		
		String winnerString = (winner == Piece.RED) ? "RED" : "BLUE";
		System.out.println("Winner: " + winnerString);
		return true;
	}
	
	public void setupGUI() {
		JFrame frame = new JFrame("Hex Game");
		JPanel mainPanel = new JPanel();
		frame.setContentPane(mainPanel);
		frame.setVisible(true);
	}
	
	public Scanner setupScanner() {
		Scanner in = new Scanner(System.in);
		return in;
	}
	
	public void setBoardDimensions(Scanner in) {
		int width = getBoardDimension(in, "width");
		int height = getBoardDimension(in, "height");
		try {
			boardSize(width, height);
		} catch (InvalidBoardSizeException e) {
			System.out.println(e);
			setBoardDimensions(in);
		} catch (BoardAlreadySizedException e) {
			System.out.println(e);
		}
	}
	
	public int getBoardDimension(Scanner in, String dimension) {
		int dimensionIn = 0;
		boolean validIn = false;
		while (!validIn) {
			System.out.print("Enter board " + dimension + ": ");
			if (in.hasNextInt()) {
				dimensionIn = in.nextInt();
				validIn = true;
			} else {
				System.out.print("Invalid input. ");
				in.next();
			}
		}		
		return dimensionIn;
	}
	
	public void setPlayerColours(Scanner in) {
		System.out.print("Which colour moves first? Red or blue: ");
		boolean validIn = false;
		while (!validIn) {
			if (in.hasNext()) {
				validIn = setPlayerColour(in.next());
			}
			else {
				System.out.print("Invalid input. ");
				in.next();
			}
		}
	}
	
	public boolean setPlayerColour(String colourStr) {
		Piece colour1 = null;
		Piece colour2 = null;
		
		if (colourStr.toLowerCase().equals("red")) {
			colour1 = Piece.RED;
			colour2 = Piece.BLUE;
		}
		else if (colourStr.toLowerCase().equals("blue")) {
			colour1 = Piece.BLUE;
			colour2 = Piece.RED;
		}

		PlayerInterface player1 = new HumanPlayer();
		PlayerInterface player2 = new HumanPlayer();
		
		try {
			specifyPlayer(player1, colour1);
		} catch (InvalidColourException e) {
			System.out.println(e);
			return false;
		} catch (ColourAlreadySetException e) {
			System.out.println(e);
			return true;
		}
		
		try {
			specifyPlayer(player2, colour2);
		} catch (InvalidColourException | ColourAlreadySetException e) {
			e.printStackTrace();
		}
		
		board.setFirstMove(colour1);
		return true;
	}
	
	public boolean isGameWon() {
		boolean gameWon = false;
		try {
			gameWon = (board.gameWon() == Piece.RED || board.gameWon() == Piece.BLUE);
		} catch (NoBoardDefinedException e) {
			e.printStackTrace();
		}
		
		return gameWon;
	}
	
	public void printBoard(Piece[][] boardView) {
		System.out.println();
		String offset = "";
		for (Piece[] row : boardView) {
			System.out.print(offset);
			for (Piece cell : row) {
				switch (cell) {
					case RED:
						System.out.print("R ");
						break;
					case BLUE:
						System.out.print("B ");
						break;
					default:
						System.out.print("U ");
						break;
				}
			}
			System.out.println();
			offset += " ";
		}
		System.out.println();
	}

	public Piece getSecondMove(Piece firstMove) {
		if (firstMove == Piece.RED) {
			return Piece.BLUE;
		}
		else if (firstMove == Piece.BLUE) {
			return Piece.RED;
		}
		return null;
	}
}
