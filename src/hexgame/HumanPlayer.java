/**
 * 
 */
package hexgame;
import java.util.*;

/**
 * @author Catherine Darling
 *
 */
public class HumanPlayer implements PlayerInterface {
	private Piece colour;
	private GameState gameState;
	
	public HumanPlayer() {
		colour = Piece.UNSET;
		gameState = GameState.INCOMPLETE;
	}

	/* (non-Javadoc)
	 * @see hexgame.PlayerInterface#makeMove(hexgame.Piece[][])
	 */
	@Override
	public MoveInterface makeMove(Piece[][] boardView) throws NoValidMovesException {
		// Check if a valid move exists
		boolean validMoveExists = checkValidMoveExists(boardView);
		
		if (!validMoveExists) {
			throw new NoValidMovesException();
		}
		
		Scanner in = new Scanner(System.in);
		MoveInterface move = new Move();
		
		int x = 0;
		int y = 0;
		boolean validIn = false;
		while (!validIn) {
			System.out.print(getColourString() + "'s move: ");
			if (in.hasNextInt()) {
				x = in.nextInt();
				if (in.hasNextInt()) {
					y = in.nextInt();
					validIn = true;
					try {
						move.setPosition(x, y);
					} catch (InvalidPositionException e) {
						System.out.println(e);
						validIn = false;
					}
				}
				else {
					System.out.print("Invalid input. ");
					in.next();
				}
			}
			else {
				System.out.print("Invalid input. ");
				in.next();
			}
		}
		
		return move;
	}

	/* (non-Javadoc)
	 * @see hexgame.PlayerInterface#setColour(hexgame.Piece)
	 */
	@Override
	public boolean setColour(Piece colour) throws InvalidColourException, ColourAlreadySetException {
		// Check if a colour other than RED/BLUE was provided
		if (colour != Piece.RED && colour != Piece.BLUE) {
			throw new InvalidColourException();
		}
		
		// Check if the colour has already been set for this player
		if (this.colour != Piece.UNSET) {
			throw new ColourAlreadySetException();
		}
		
		this.colour = colour;
		return true;
	}

	/* (non-Javadoc)
	 * @see hexgame.PlayerInterface#finalGameState(hexgame.GameState)
	 */
	@Override
	public boolean finalGameState(GameState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean checkValidMoveExists(Piece[][] boardView) {
		boolean validMove = false;
		
		for (Piece[] row : boardView) {
			for (Piece position : row) {
				if (position == Piece.UNSET) {
					validMove = true;
				}
			}
		}
		
		return validMove;
	}
	
	public String getColourString() {
		if (colour == Piece.RED) {
			return "Red";
		}
		else if (colour == Piece.BLUE) {
			return "Blue";
		}
		
		return "Unset";
	}
}
