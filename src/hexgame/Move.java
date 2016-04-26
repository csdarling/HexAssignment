package hexgame;

/**
 * @author Catherine Darling
 *
 */
public class Move implements MoveInterface {
	private int xPosition;
	private int yPosition;
	private boolean conceded;
	
	public Move() {
		conceded = false;
	}

	/* (non-Javadoc)
	 * @see hexgame.MoveInterface#setPosition(int, int)
	 */
	@Override
	public boolean setPosition(int x, int y) throws InvalidPositionException {
		// Check if the specified position is invalid
		if (x < 0 || y < 0) {
			throw new InvalidPositionException();
		}
		
		xPosition = x;
		yPosition = y;
		return true;
	}

	/* (non-Javadoc)
	 * @see hexgame.MoveInterface#hasConceded()
	 */
	@Override
	public boolean hasConceded() {
		// TODO Auto-generated method stub
		return conceded;
	}

	/* (non-Javadoc)
	 * @see hexgame.MoveInterface#getXPosition()
	 */
	@Override
	public int getXPosition() {
		return xPosition;
	}

	/* (non-Javadoc)
	 * @see hexgame.MoveInterface#getYPosition()
	 */
	@Override
	public int getYPosition() {
		return yPosition;
	}

	/* (non-Javadoc)
	 * @see hexgame.MoveInterface#setConceded()
	 */
	@Override
	public boolean setConceded() {
		conceded = true;
		return true;
	}

}
