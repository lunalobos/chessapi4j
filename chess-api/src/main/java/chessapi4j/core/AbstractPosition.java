package chessapi4j.core;

import chessapi4j.Position;

/**
 * 
 * @author lunalobos
 *
 */
abstract class AbstractPosition implements Position {

	private static final long serialVersionUID = -3129022190813874561L;

	private int enPassant;
	
	private boolean whiteMove;

	private boolean shortCastleWhite;
	private boolean shortCastleBlack;
	private boolean longCastleWhite;
	private boolean longCastleBlack;
	
	private int movesCounter;// moves counter
	private int halfMovesCounter;// for 50 move draw rule
	
	private transient boolean checkmate;
	private transient boolean stalemate;
	private transient boolean fiftyMoves;
	private transient boolean repetitions;
	private transient boolean lackOfMaterial;
	
	
	
	public AbstractPosition() {
		enPassant = -1;
	}

	public AbstractPosition(int enPassant, boolean whiteMove, boolean shortCastleWhite, boolean shortCastleBlack,
			boolean longCastleWhite, boolean longCastleBlack, int movesCounter, int halfMovesCounter, boolean checkmate,
			boolean stalemate, boolean fiftyMoves, boolean repetitions, boolean lackOfMaterial) {
		this.enPassant = enPassant;
		this.whiteMove = whiteMove;
		this.shortCastleWhite = shortCastleWhite;
		this.shortCastleBlack = shortCastleBlack;
		this.longCastleWhite = longCastleWhite;
		this.longCastleBlack = longCastleBlack;
		this.movesCounter = movesCounter;
		this.halfMovesCounter = halfMovesCounter;
		this.checkmate = checkmate;
		this.stalemate = stalemate;
		this.fiftyMoves = fiftyMoves;
		this.repetitions = repetitions;
		this.lackOfMaterial = lackOfMaterial;
	}
	
	public boolean isWhiteMove() {
		return whiteMove;
	}
	public void setWhiteMove(boolean whiteMove) {
		this.whiteMove = whiteMove;
	}
	public boolean isShortCastleWhite() {
		return shortCastleWhite;
	}
	public void setShortCastleWhite(boolean shortCastleWhite) {
		this.shortCastleWhite = shortCastleWhite;
	}
	public boolean isShortCastleBlack() {
		return shortCastleBlack;
	}
	public void setShortCastleBlack(boolean shortCastleBlack) {
		this.shortCastleBlack = shortCastleBlack;
	}
	public boolean isLongCastleWhite() {
		return longCastleWhite;
	}
	public void setLongCastleWhite(boolean longCastleWhite) {
		this.longCastleWhite = longCastleWhite;
	}
	public boolean isLongCastleBlack() {
		return longCastleBlack;
	}
	public void setLongCastleBlack(boolean longCastleBlack) {
		this.longCastleBlack = longCastleBlack;
	}
	public int getMovesCounter() {
		return movesCounter;
	}
	public void setMovesCounter(int movesCounter) {
		this.movesCounter = movesCounter;
	}
	public int getEnPassant() {
		return enPassant;
	}
	public void setEnPassant(int enPassant) {
		this.enPassant = enPassant;
	}
	public int getHalfMovesCounter() {
		return halfMovesCounter;
	}
	public void setHalfMovesCounter(int halfMovesCounter) {
		this.halfMovesCounter = halfMovesCounter;
	}
	public boolean isCheckmate() {
		return checkmate;
	}
	public boolean isStalemate() {
		return stalemate;
	}
	public boolean isFiftyMoves() {
		return fiftyMoves;
	}
	public boolean isRepetitions() {
		return repetitions;
	}
	public boolean isLackOfMaterial() {
		return lackOfMaterial;
	}
	public void setCheckmate(boolean checkmate) {
		this.checkmate = checkmate;
	}
	public void setStalemate(boolean stalemate) {
		this.stalemate = stalemate;
	}
	public void setFiftyMoves(boolean fiftyMoves) {
		this.fiftyMoves = fiftyMoves;
	}
	public void setRepetitions(boolean repetitions) {
		this.repetitions = repetitions;
	}
	public void setLackOfMaterial(boolean lackOfMaterial) {
		this.lackOfMaterial = lackOfMaterial;
	}
	public void increaseMovesCounter() {
		if(whiteMove)
			movesCounter++;
	}

}
