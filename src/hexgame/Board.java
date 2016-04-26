package hexgame;
import java.util.*;
import java.awt.*;

/**
 * @author Catherine Darling
 *
 */
public class Board implements BoardInterface {
	private int width;
	private int height;
	private Piece[][] boardView;
	private Piece firstMove;
	private Piece winner;
	
	public Board() {
		firstMove = Piece.UNSET;
		winner = Piece.UNSET;
	}
	
	/* (non-Javadoc)
	 * @see hexgame.BoardInterface#setBoardSize(int, int)
	 */
	@Override
	public boolean setBoardSize(int sizeX, int sizeY) throws InvalidBoardSizeException, BoardAlreadySizedException {
		// Check if either size value is less than one
		if (sizeX < 1 || sizeY < 1) {
			throw new InvalidBoardSizeException();
		}
		
		// Check if the board has already been created
		if (width > 0 || height > 0) {
			throw new BoardAlreadySizedException();
		}
		
		width = sizeX;
		height = sizeY;
		boardView = new Piece[height][width];
		
		for (int y = 0; y < boardView.length; y++) {	
			for (int x = 0; x < boardView[y].length; x++) {
				boardView[y][x] = Piece.UNSET;
			}
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see hexgame.BoardInterface#getBoardView()
	 */
	@Override
	public Piece[][] getBoardView() throws NoBoardDefinedException {		
		// Check if the board has yet to be defined with setBoardSize()
		checkBoardDefined();
		
		return boardView;
	}

	/* (non-Javadoc)
	 * @see hexgame.BoardInterface#placePiece(hexgame.Piece, hexgame.MoveInterface)
	 */
	@Override
	public boolean placePiece(Piece colour, MoveInterface move) throws PositionAlreadyTakenException,
			InvalidPositionException, InvalidColourException, NoBoardDefinedException {

		// Check if the board has yet to be defined with setBoardSize()
		checkBoardDefined();
		
		// Check if the colour being set is invalid
		int redCount = 0;
		int blueCount = 0;
		
		for (Piece[] row : boardView) {	
			for (Piece position : row) {
				if (position == Piece.RED) {
					redCount++;
				}
				else if (position == Piece.BLUE) {
					blueCount++;
				}
			}
		}
		
		// Record which colour is placed first
//		if (redCount == 0 && blueCount == 0) {
//			firstMove = colour;
//		}
			
		if (colour == Piece.UNSET || (colour == firstMove && redCount != blueCount)
			|| (colour != firstMove && redCount == blueCount)) {
			
			throw new InvalidColourException();
		}
		
		int xPosition = move.getXPosition();
		int yPosition = move.getYPosition();
		
		// Check if the specified position is invalid
		if (xPosition >= width || yPosition >= height) {
			throw new InvalidPositionException();
		}
		
		// Check if there is already a Piece in this position
		if (boardView[yPosition][xPosition] != Piece.UNSET) {
			throw new PositionAlreadyTakenException();
		}
		
		// Place the piece on the board at the specified location
		boardView[yPosition][xPosition] = colour;
		return true;
	}

	/* (non-Javadoc)
	 * @see hexgame.BoardInterface#gameWon()
	 */
	@Override
	public Piece gameWon() throws NoBoardDefinedException {
		// TODO 
		
		// Check if the board has yet to be defined with setBoardSize()
		checkBoardDefined();
		
		// Check if player has conceded
		if (winner == Piece.RED || winner == Piece.BLUE) {
			return winner;
		}
		
		if (getWinningPath(Piece.RED).size() > 0) {
			winner = Piece.RED;
		}
		else if (getWinningPath(Piece.BLUE).size() > 0) {
			winner = Piece.BLUE;
		}
		
		return winner;
	}
	
	public void checkBoardDefined() throws NoBoardDefinedException {
		if (width == 0 || height == 0) {
			throw new NoBoardDefinedException();
		}
	}
	
	public void setFirstMove(Piece colour) {
		firstMove = colour;
	}
	
	public Piece getFirstMove() {
		return firstMove;
	}
	
	// Set winner when player concedes
	public void setWinner(Piece colour) {
		winner = colour;
	}
	
	// Breadth first search algorithm
	// Returns empty ArrayList if no winning path
	public ArrayList<Point> getWinningPath(Piece colour) {
		ArrayList<Point> path = new ArrayList<Point>();
		int lineEnd = (colour == Piece.BLUE) ? height - 1 : width - 1;
		for (int i = lineEnd; i >= 0; i--) {
			Point source = (colour == Piece.BLUE) ? new Point(width - 1, i) : new Point(i, height - 1);
			if (boardView[source.y][source.x] == colour) {
				HashMap<Point, Integer> distances = new HashMap<Point, Integer>();
				HashMap<Point, Point> predecessors = new HashMap<Point, Point>();
				
				// Find all distances from the source and record the route
				int distance = 0;
				distances.put(source, distance);
				predecessors.put(source, null);
				boolean complete = false;
				while(!complete) {
					HashMap<Point, Integer> newDistances = new HashMap<Point, Integer>();
					for (Point currentPosition : distances.keySet()) {
						if (distances.get(currentPosition) == distance) {
							ArrayList<Point> connectedPositions = getConnectedPositions(currentPosition, colour);
							for (Point connectedPosition : connectedPositions) {
								if (!distances.containsKey(connectedPosition) && !newDistances.containsKey(connectedPosition)) {
									newDistances.put(connectedPosition, distance + 1);
									predecessors.put(connectedPosition, currentPosition);
								}
							}
						}	
					}
					
					if (newDistances.isEmpty()) {
						complete = true;
					}
					else {
						distances.putAll(newDistances);
						distance++;
					}
				}
				
				// Determine the shortest winning path
				int minDistance = (colour == Piece.BLUE) ? width : height; // height if RED
				Point invalidPosition = new Point(-1, -1);
				Point closestPosition = invalidPosition;
				int lineStart = (colour == Piece.BLUE) ? height : width;
				for (int j = 0; j < lineStart; j++) {
					Point position = (colour == Piece.BLUE) ? new Point(0, j) : new Point(j, 0);
					if (distances.containsKey(position)) {
						if (distances.get(position) < minDistance) {
							minDistance = distances.get(position);
							closestPosition = position;
						}
					}
				}
				
				// Record the winning path from left to right, if there is one
				if (!closestPosition.equals(invalidPosition) && (path.size() == 0 || minDistance < path.size() - 1)) {
					path.clear();
					while (closestPosition != null) {
						path.add(closestPosition);
						closestPosition = predecessors.get(closestPosition);
					}
				}
			}
		}
		
		return path;
	}
	
	public ArrayList<Point> getConnectedPositions(Point currentPosition, Piece colour) {
		Set<Point> neighbourPositions = new HashSet<Point>();
		int xCoord = (int) currentPosition.getX();
		int yCoord = (int) currentPosition.getY();
		
		int xPlus1 = (xCoord < width - 1) ? (xCoord + 1) : xCoord;
		int xMinus1 = (xCoord > 0) ? (xCoord - 1) : xCoord;
		int yPlus1 = (yCoord < height - 1) ? (yCoord + 1) : yCoord;
		int yMinus1 = (yCoord > 0) ? (yCoord - 1): yCoord;
		
		neighbourPositions.add(new Point(xPlus1, yCoord));
		neighbourPositions.add(new Point(xMinus1, yCoord));
		neighbourPositions.add(new Point(xCoord, yPlus1));
		neighbourPositions.add(new Point(xCoord, yMinus1));
		neighbourPositions.add(new Point(xPlus1, yMinus1));
		neighbourPositions.add(new Point(xMinus1, yPlus1));
		
		ArrayList<Point> connectedPositions = new ArrayList<Point>();
		for (Point position : neighbourPositions) {
			if (boardView[(int) position.getY()][(int) position.getX()] == colour) {
				connectedPositions.add(position);
			}
		}
		
		return connectedPositions;
	}
}
